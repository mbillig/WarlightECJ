package warlight2;

import ec.Evolve;
import visualisation.PopFitVsSizeChart;


/**
 * Created by Jonatan on 21-Sep-15.
 */
public class WarlightRunner {
    public static void main(String[] args) throws Exception {
        String[] senseWorld = new String[]{"-file", "GP\\src\\sense_world.params"};
        String[] eval_move = new String[]{"-file", "GP\\src\\warlight2\\eval_move.params", "-p", "gp.tree.print-style=latex"};
        String[] reinforce = new String[]{"-file", "GP\\src\\reinforce.params", "-p", "gp.tree.print-style=latex"};
        String[] eval_moveFPS = new String[]{"-file", "GP\\src\\eval_moveFPS.params", "-p", "gp.tree.print-style=latex"};
        String[] eval_move_competitive = new String[]{"-file", "GP\\src\\warlight2\\eval_move_competitive.params"};
        String[] experiment = new String[]{"-file", "GP\\src\\warlight2\\experiment.params"};
        String[] attack_strength = new String[]{"-file", "GP\\src\\warlight2\\attack_strength.params"};
        Evolve.main(eval_move);
    }
}
