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
public class Div extends GPNode{

    public int expectedChildren() {
        return 2;
    }

    @Override
    public String toString() {
        return " / ";
    }

    //protected division
    @Override
    public void eval(EvolutionState state, int thread, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        DoubleData rd = ((DoubleData) (gpData));

        children[1].eval(state, thread, gpData, adfStack, gpIndividual, problem);
        if(rd.x == 0.0)
            rd.x = 1.0;
        else
        {
            double result;
            result = rd.x;

            children[0].eval(state, thread, gpData, adfStack, gpIndividual, problem);
            rd.x = rd.x/ result;
        }
    }
}
