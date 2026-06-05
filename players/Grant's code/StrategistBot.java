import java.util.ArrayList;

/**
 * StrategistBot — A deterministic adaptive betting bot.
 *
 * Core strategy: analyze the opponent's betting patterns, infer their style,
 * and mirror or counteract accordingly. Uses a multi-phase approach:
 *
 *  Phase 1 (rounds 1-3): Probe with moderate bets to gather data.
 *  Phase 2 (rounds 4+):  Classify opponent as Aggressive, Conservative, or Mirroring,
 *                         then adapt:
 *    - vs Aggressive:   bet big to match or slightly undercut (force them into bad trades)
 *    - vs Conservative: bet their amount + a small margin to inch ahead
 *    - vs Mirroring:    shift bets unpredictably using a deterministic pseudo-pattern
 *
 *  End-game (last ~3 rounds): If ahead, bet minimum to protect lead.
 *                              If behind, bet aggressively to catch up.
 *
 * Constraints always enforced:
 *  - Bet >= 5 when tokens > 5
 *  - Bet > 0 always
 *  - Bet <= myTokens always
 */
public class StrategistBot implements botInterface {

    private static final int TOTAL_ROUNDS = 10; // adjust if organizer specifies differently
    private static final int PROBE_ROUNDS = 3;
    private static final int END_GAME_ROUNDS = 3;

    @Override
    public String getName() {
        return "StrategistBot";
    }

    @Override
    public int getBet(readOnlyGameState state) {
        int myTokens      = state.getMyTokens();
        int oppTokens     = state.getOpponentTokens();
        int round         = state.getCurrentRound();
        ArrayList<Integer>[] fullState = state.getFullState();
        ArrayList<Integer> myHistory   = fullState[0];
        ArrayList<Integer> oppHistory  = fullState[1];

        int bet;

        // ── End-game: protect lead or go all-in to catch up ──────────────────
        if (round > TOTAL_ROUNDS - END_GAME_ROUNDS) {
            if (myTokens > oppTokens) {
                // Protect lead: bet minimum legal amount
                bet = minBet(myTokens);
            } else if (myTokens < oppTokens) {
                // Need to catch up: bet aggressively
                bet = (int) Math.round(myTokens * 0.85);
            } else {
                // Tied: bet slightly more than minimum to try to break the tie
                bet = Math.min(myTokens, minBet(myTokens) + 5);
            }
            return clamp(bet, myTokens);
        }

        // ── Probe phase: moderate bets to gather data ─────────────────────────
        if (round <= PROBE_ROUNDS) {
            // Bet roughly 40% in probe rounds, deterministic
            bet = Math.max(minBet(myTokens), (int)(myTokens * 0.40));
            return clamp(bet, myTokens);
        }

        // ── Strategy phase: classify and adapt ───────────────────────────────
        double oppAvgBet    = average(oppHistory);
        double oppAvgTokens = oppTokens; // current snapshot
        double myAvgBet     = average(myHistory);

        // Opponent's aggression ratio = avg bet / their current tokens
        // We can approximate their "starting stack" from game state:
        // (their tokens) + (sum of their bets that lost) - (sum of their wins)
        // Instead, just use their bet sizes relative to mine as a heuristic.

        double oppBetRatio = (oppAvgBet > 0 && myAvgBet > 0)
                ? oppAvgBet / myAvgBet
                : 1.0;

        // Detect mirroring: opponent's bets track ours closely
        boolean isMirroring = detectMirroring(myHistory, oppHistory);

        // Detect aggression: opponent bets significantly more than me
        boolean isAggressive = oppBetRatio > 1.3;

        // Detect conservative: opponent bets significantly less than me
        boolean isConservative = oppBetRatio < 0.7;

        if (isMirroring) {
            // Break the mirror: use a deterministic offset pattern to stay unpredictable
            // Alternate between betting high and lower, seeded by round number
            int pattern = round % 4;
            switch (pattern) {
                case 0: bet = (int)(myTokens * 0.60); break;
                case 1: bet = (int)(myTokens * 0.25); break;
                case 2: bet = (int)(myTokens * 0.50); break;
                default: bet = (int)(myTokens * 0.35); break;
            }

        } else if (isAggressive) {
            // Match their aggression: bet slightly more than their last bet
            int oppLastBet = state.getOpponentBet();
            bet = oppLastBet + (int)(oppLastBet * 0.10) + 1;
            // Cap at 75% of our stack so we don't go broke in one round
            bet = Math.min(bet, (int)(myTokens * 0.75));

        } else if (isConservative) {
            // They're playing safe — we bet moderately to build a lead without
            // overexposing ourselves. Use ~35% of stack.
            bet = (int)(myTokens * 0.35);

        } else {
            // Balanced opponent: bet slightly above their average to edge ahead
            bet = (int)(oppAvgBet * 1.1) + 2;
            bet = Math.min(bet, (int)(myTokens * 0.55));
        }

        return clamp(bet, myTokens);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns the minimum legal bet given the current token count. */
    private int minBet(int tokens) {
        return tokens > 5 ? 5 : 1;
    }

    /**
     * Clamps the bet so that:
     *  - bet >= 5 if tokens > 5 (else >= 1)
     *  - bet <= tokens
     */
    private int clamp(int bet, int tokens) {
        int min = minBet(tokens);
        int max = tokens;
        return Math.max(min, Math.min(max, bet));
    }

    /** Returns the average of a list, or 0.0 if empty. */
    private double average(ArrayList<Integer> list) {
        if (list.isEmpty()) return 0.0;
        int sum = 0;
        for (int v : list) sum += v;
        return (double) sum / list.size();
    }

    /**
     * Detects if the opponent is mirroring our bets.
     * We look at the last N rounds and check if their bets track ours closely
     * (within 20% of each other on average).
     */
    private boolean detectMirroring(ArrayList<Integer> myHistory, ArrayList<Integer> oppHistory) {
        int n = Math.min(myHistory.size(), oppHistory.size());
        if (n < 2) return false;

        int lookback = Math.min(n, 3);
        double totalRatio = 0.0;
        int valid = 0;

        for (int i = n - lookback; i < n; i++) {
            int myBet  = myHistory.get(i);
            int oppBet = oppHistory.get(i);
            if (myBet > 0) {
                double ratio = (double) oppBet / myBet;
                totalRatio += ratio;
                valid++;
            }
        }

        if (valid == 0) return false;
        double avgRatio = totalRatio / valid;
        // "Mirroring" if opponent's bets are consistently 80–120% of ours
        return avgRatio >= 0.80 && avgRatio <= 1.20;
    }
}
