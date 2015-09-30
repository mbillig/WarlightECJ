package warlight2;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import warlight2.data_types.DoubleData;
import warlight2.sense_world.SenseWorld;

/**
 * Created by Jonatan on 30-Sep-15.
 */
public class RandVar extends GPNode {
    public int expectedChildren() {
        return 0;
    }

    @Override
    public String toString() {
        return  " rand ";
    }

    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        DoubleData rd = ((DoubleData) (gpData));
        rd.x = ((SenseWorld)problem).rand;
    }
}
