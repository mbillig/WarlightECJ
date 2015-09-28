package GPBT;

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
public abstract class Composite  extends GPNode {
    public void checkConstraints(final EvolutionState state,
                                 final int tree,
                                 final GPIndividual typicalIndividual,
                                 final Parameter individualBase) {
        super.checkConstraints(state, tree, typicalIndividual, individualBase);
        if (children.length < 1)
            state.output.error("Incorrect number of children for node " +
                    toStringForError() + " at " +
                    individualBase);
    }
}
