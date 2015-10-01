package warlight2.attack_move;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Created by Jonatan on 30-Sep-15.
 */
public class EnemyOrFriendly extends GPNode {
    public int expectedChildren() {
        return 0;
    }

    @Override
    public String toString() {
        return " enemyOrFriendly";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
    }
}
