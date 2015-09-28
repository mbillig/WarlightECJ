package warlight2;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Created by Jonatan on 17-Sep-15.
 */
public class IfGreaterThan extends GPNode {

    public int expectedChildren() {
        return 4;
    }

    @Override
    public String toString() {
        return "if(>)";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        int a, b, c, d;
        IntData rd = ((IntData) (gpData));

        children[0].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        a = rd.x;
        children[1].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        b = rd.x;
        children[2].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        c = rd.x;
        children[3].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        d = rd.x;

        rd.x = (a > b) ? c : d;
    }
}
