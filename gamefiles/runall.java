package gamefiles;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import players.*;

public class runall {
    
    public static void main(String[] args) {
        try (PrintWriter csv = new PrintWriter(new FileWriter("runall-results.csv", false))) {
            competeAllPlayers(csv);
        } catch (IOException e) {
            System.out.println("Failed to create CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void competeAllPlayers(PrintWriter csv) {
        botInterface[] bots = {
            new copycatplusone(),
            new bet20everytime(),
            new slowburn(),
            new chatgpt()
        };
        String[] names = {
            "copycatplusone",
            "bet20everytime",
            "slowburn",
            "chatgpt"
        };

        int n = bots.length;
        String[][] results = new String[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.println("========================================");
                System.out.println("Match: " + names[i] + " vs " + names[j]);
                int result = compete(bots[i], bots[j]);
                String outcome;
                if (result == 1) {
                    outcome = names[i] + " wins.";
                    results[i][j] = "W";
                } else if (result == 2) {
                    outcome = names[j] + " wins.";
                    results[i][j] = "L";
                } else {
                    outcome = "Tie.";
                    results[i][j] = "T";
                }
                System.out.println("Match result: " + outcome);
                System.out.println("========================================\n");
            }
        }

        csv.print(",");
        for (int j = 0; j < n; j++) {
            csv.print(names[j]);
            if (j < n - 1) csv.print(",");
        }
        csv.println();

        for (int i = 0; i < n; i++) {
            csv.print(names[i] + ",");
            for (int j = 0; j < n; j++) {
                csv.print(results[i][j] == null ? "" : results[i][j]);
                if (j < n - 1) csv.print(",");
            }
            csv.println();
        }
    }

    /**
     * 
     * @param p1 player 1
     * @param p2 player 2
     * @return 1 for player 1, 2 for player 2, and 0 for a tie.
     */
    public static int compete(botInterface p1, botInterface p2){

        gameState g = new gameState(100);
        final int maxRounds = 20;

        System.out.println("Starting competition: player1=" + p1.getClass().getSimpleName() + ", player2=" + p2.getClass().getSimpleName());
        System.out.println("Initial tokens: p1=" + g.p1Tokens + ", p2=" + g.p2Tokens + "\n");

        for (int i = 0; i < maxRounds; i++){
            
            g.currentRound = i;
            System.out.println("--- Round " + i + " ---");
            System.out.println("Round start tokens: p1=" + g.p1Tokens + ", p2=" + g.p2Tokens);

            readOnlyGameState rg1 = g.getReadOnly(true);
            readOnlyGameState rg2 = g.getReadOnly(false);

            int bet1 = p1.getBet(rg1);
            System.out.println("Player 1 bet request: " + bet1 + " (available=" + rg1.getMyTokens() + ")");
            if (bet1 <= 0 || (bet1 < 5 && rg1.getMyTokens() > 5) || bet1 > rg1.getMyTokens()){
                g.p1Tokens = -1;
                System.out.println("Player 1 invalid bet: " + bet1 + " -> eliminated");
            } else {
                g.p1bet = bet1;
                System.out.println("Player 1 accepted bet: " + g.p1bet);
            }

            int bet2 = p2.getBet(rg2);
            System.out.println("Player 2 bet request: " + bet2 + " (available=" + rg2.getMyTokens() + ")");
            if (bet2 <= 0 || (bet2 < 5 && rg2.getMyTokens() > 5) || bet2 > rg2.getMyTokens()){
                g.p2Tokens = -1;
                System.out.println("Player 2 invalid bet: " + bet2 + " -> eliminated");
            } else {
                g.p2bet = bet2;
                System.out.println("Player 2 accepted bet: " + g.p2bet);
            }
            
            if (g.p1Tokens == -1 && g.p2Tokens == -1) {
                System.out.println("Illegal bet in round " + i);
                System.out.println("Game history so far: " + Arrays.toString(g.state));
                System.out.println("Both players eliminated. Tie.");
                return 0;
            }
            if (g.p1Tokens == -1) {
                System.out.println("Illegal bet in round " + i);
                System.out.println("Game history so far: " + Arrays.toString(g.state));
                System.out.println("Player 1 eliminated. Player 2 wins.");
                return 2;
            }
            if (g.p2Tokens == -1) {
                System.out.println("Illegal bet in round " + i);
                System.out.println("Game history so far: " + Arrays.toString(g.state));
                System.out.println("Player 2 eliminated. Player 1 wins.");
                return 1;
            }       

            if (g.p1bet != g.p2bet){
                g.p1Tokens -= bet1;
                g.p2Tokens -= bet2;
                System.out.println("Bets differ. Deducting bets: p1-" + bet1 + ", p2-" + bet2);

                if (g.p1bet > g.p2bet) { 
                    g.p1Tokens += g.p2bet * 2;
                    System.out.println("Player 1 wins round " + i + ": gains " + (g.p2bet * 2) + " tokens");
                } else if (g.p2bet > g.p1bet) {
                    g.p2Tokens += g.p1bet * 2;
                    System.out.println("Player 2 wins round " + i + ": gains " + (g.p1bet * 2) + " tokens");
                } 

            } else {
                System.out.println("Bets tied at " + g.p1bet + ". No token transfer this round.");
            }

            System.out.println("Round end tokens: p1=" + g.p1Tokens + ", p2=" + g.p2Tokens + "\n");
            g.newTurn(g.p1bet, g.p2bet);
        }

        System.out.println("Competition complete.");
        System.out.println("Final tokens: p1=" + g.p1Tokens + ", p2=" + g.p2Tokens);
        System.out.println("Full game history: " + Arrays.toString(g.state));
        if (g.p1Tokens > g.p2Tokens) {
            System.out.println("Result: Player 1 wins.");
            return 1;
        }
        if (g.p2Tokens > g.p1Tokens) {
            System.out.println("Result: Player 2 wins.");
            return 2;
        }
        System.out.println("Result: tie.");
        return 0;
    }

}


