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
        String[] eval_move = new String[]{"-file", "GP\\src\\eval_move.params", "-p", "gp.tree.print-style=latex"};
        String[] eval_moveFPS = new String[]{"-file", "GP\\src\\eval_moveFPS.params", "-p", "gp.tree.print-style=latex"};
        String[] eval_move_competitive = new String[]{"-file", "GP\\src\\eval_move_competitive.params", "-p", "gp.tree.print-style=latex"};
        Evolve.main(eval_move);
    }
}
