package texasholdem.moves;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Created by Jonatan on 06-Oct-15.
 */
public class Raise extends GPNode {
    public int expectedChildren() {
        return 0;
    }

    @Override
    public String toString() {
        return "raise";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
    }
}
