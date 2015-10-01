package warlight2.sense_world;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import warlight2.data_types.DoubleData;

/**
 * Created by Jonatan on 24-Sep-15.
 */
public class SuperRegionScore extends GPNode{
    public int expectedChildren() {
        return 0;
    }

    @Override
    public String toString() {
        return " SuperRegionScore ";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
    }
}
