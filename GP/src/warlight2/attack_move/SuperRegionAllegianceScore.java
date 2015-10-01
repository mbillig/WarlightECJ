package warlight2.attack_move;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Created by Jonatan on 01-Oct-15.
 */
public class SuperRegionAllegianceScore extends GPNode{
    public int expectedChildren() {
        return 0;
    }

    @Override
    public String toString() {
        return "superRegionAllegianceScore";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
    }
}
