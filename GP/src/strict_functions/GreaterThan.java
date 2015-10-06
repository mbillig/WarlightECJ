package strict_functions;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import data_types.IntData;

/**
 * Created by Jonatan on 17-Sep-15.
 */
public class GreaterThan extends GPNode {

    public int expectedChildren() {
        return 2;
    }

    @Override
    public String toString() {
        return ">";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        int a, b;
        IntData rd = ((IntData) (gpData));

        children[0].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        a = rd.x;
        children[1].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        b = rd.x;

        rd.x = (a>b)? 1:0;
    }
}
