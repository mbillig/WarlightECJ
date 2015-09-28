package GPBT.composite;

import GPBT.Composite;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

/**
 * Created by Jonatan on 16-Sep-15.
 */

public class Sequence extends Composite {

    @Override
    public String toString() {
        return "sequence";
    }

    @Override
    public void eval(EvolutionState evolutionState, int i, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        for(GPNode child : this.children)
            child.eval(evolutionState, i, gpData, adfStack, gpIndividual, problem);
    }
}
