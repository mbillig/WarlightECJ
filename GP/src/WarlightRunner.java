import com.theaigames.game.warlight2.Warlight2;
import ec.Evolve;
import warlight2.JarCompiler;

import java.io.IOException;

/**
 * Created by Jonatan on 21-Sep-15.
 */
public class WarlightRunner {
    public static void main(String[] args) throws Exception {
        String[] senseWorld = new String[]{"-file", "GP\\src\\sense_world.params"};
        String[] eval_move = new String[]{"-file", "GP\\src\\eval_move.params"};

        JarCompiler JC = new JarCompiler();

        try {
            JC.run("out\\production\\mybot", "myBot.jar");
        } catch (IOException ioe) {
            System.out.println("io exception");
            ioe.printStackTrace();
        }

        Evolve.main(eval_move);
        //Evolve.main(senseWorld);
    }
}
