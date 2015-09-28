package warlight2;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Created by Jonatan on 15-Sep-15.
 */
public class Sub extends GPNode {

    public int expectedChildren() {
        return 2;
    }

    @Override
    public String toString() {
        return " - ";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        int result;
        IntData rd = ((IntData) (gpData));

        children[0].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        result = rd.x;

        children[1].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        rd.x = result - rd.x;
    }
}
