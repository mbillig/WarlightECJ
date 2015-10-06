package functions;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import data_types.DoubleData;

/**
 * Created by Jonatan on 30-Sep-15.
 */
public class Inv extends GPNode {
    public int expectedChildren() {
        return 1;
    }

    @Override
    public String toString() {
        return " 1/";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        DoubleData rd = ((DoubleData) (gpData));

        children[0].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        rd.x = 1.0/rd.x;
    }
}
