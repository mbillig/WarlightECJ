package warlight2;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

;

/**
 * Created by Jonatan on 15-Sep-15.
 */
public class MultiValuedRegression extends GPProblem implements SimpleProblemForm {

    public static final String P_DATA = "data";

    public int currentX;
    public int currentY;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        if (!(input instanceof IntData)) {
            state.output.fatal("GPData class must subclass from " + IntData.class, base.push(P_DATA), null);
        }
    }

    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i1) {
        if (!individual.evaluated) {
            IntData input = (IntData) (this.input);

            int hits = 0;
            double sum = 0.0;
            int expectedResult;
            double result;

            for (int y = 0; y < 10; y++) {
                currentX = evolutionState.random[i1].nextInt();
                currentY = evolutionState.random[i1].nextInt();
                expectedResult = (currentX + currentY * (currentX < currentY?1:0)) - (currentX > (currentX > currentY?1:0)?1:0);
                ((GPIndividual) individual).trees[0].child.eval(evolutionState, i1, input, stack, ((GPIndividual) individual), this);

                result = Math.abs(expectedResult - input.x);
                if (result <= 0.01) hits++;
                sum += result;
            }
            KozaFitness f = ((KozaFitness) individual.fitness);
            f.setStandardizedFitness(evolutionState, sum);
            f.hits = hits;
            individual.evaluated = true;;
        }
    }
}
