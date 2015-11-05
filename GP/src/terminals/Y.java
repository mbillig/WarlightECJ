package terminals;

import data_types.IntData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import warlight2.WarlightProblem;

/**
 * Created by Jonatan on 13-Oct-15.
 */
public class Y extends GPNode{

    public int expectedChildren(){
        return 0;
    }

    @Override
    public String toString() {
        return " Y ";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        IntData rd = ((IntData)(gpData));
        rd.x = ((WarlightProblem)problem).Y;
    }
}
