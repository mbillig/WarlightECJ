package texasholdem;

import com.theaigames.game.texasHoldem.TexasHoldem;
import com.theaigames.game.texasHoldem.TexasResults;
import ec.Evolve;

/**
 * Created by Jonatan on 06-Oct-15.
 */
public class EvalIndividual {
    public static void main(String[] args) {
        double wins = 0;
        double runcount = 100;
        String expression = "handeval+2";

        String[] texasArgs = new String[]{"java -classpath out\\production\\texasbot bot.BotStarter " + expression, "java -jar randomTexasBot.jar"};
        for(int x = 0; x <runcount; x++){
            try {
                TexasHoldem.main(texasArgs);
                wins += TexasResults.getInstance().getWinner() == 1? 1: 0;
                System.out.println(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(expression);
        System.out.println(runcount);
        System.out.println("Wins = "+ wins+"/"+runcount);
        System.out.println("fitness = " + (1-(wins/runcount)));
    }
}
