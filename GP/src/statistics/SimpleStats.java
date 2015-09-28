package statistics;

import ec.EvolutionState;
import ec.Fitness;
import ec.Statistics;
import ec.util.Parameter;
import ec.vector.FloatVectorIndividual;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jonatan on 18-Sep-15.
 */
public class SimpleStats extends Statistics{
    // The parameter string and log number of the file for our readable
    // population
    public static final String P_POPFILE = "pop-file";
    public int popLog;

    // The parameter string and log number of the file for our best-genome-#3
    // individual
    public static final String P_INFOFILE = "info-file";
    public int infoLog;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        // set up popFile
        File popFile = state.parameters.getFile(base.push(P_POPFILE), null);
        if (popFile != null)
            try {
                popLog = state.output.addLog(popFile, true);
            } catch (IOException i) {
                state.output.fatal("An IOException occurred while trying to create the log " + popFile + ":\n" + i);
            }

        // set up infoFile
        File infoFile = state.parameters.getFile(base.push(P_INFOFILE), null);
        if (infoFile != null)
            try {
                infoLog = state.output.addLog(infoFile, true);
            } catch (IOException i) {
                state.output.fatal("An IOException occurred while trying to create the log " + infoFile + ":\n" + i);
            }
    }

    public void postEvaluationStatistics(final EvolutionState state) {
        // be certain to call the hook on super!
        super.postEvaluationStatistics(state);

        // write out a warning that the next generation is coming
        state.output.println("-----------------------\nGENERATION " + state.generation + "\n-----------------------",
                popLog);

        // print out the population
        state.population.printPopulation(state, popLog);

        double[] totalFitnessThisGen = new double[1];                      // per-subpop mean fitness this generation
        double[] meanFitnessThisGen = new double[1];

        // print out best genome #3 individual in subpop 0
        int best = 0;
        Fitness best_val = ((FloatVectorIndividual) state.population.subpops[0].individuals[0]).fitness;
        for (int y = 0; y < state.population.subpops[0].individuals.length; y++) {
            // We'll be unsafe and assume the individual is a
            // FloatVectorIndividual
            Fitness val = ((FloatVectorIndividual) state.population.subpops[0].individuals[y]).fitness;
            if (val.betterThan(best_val)) {
                best = y;
                best_val = val;
            }

            // sum up mean fitness for population
            totalFitnessThisGen[0] += state.population.subpops[0].individuals[y].fitness.fitness();

        }

        // compute mean fitness stats
        meanFitnessThisGen[0] = (state.population.subpops[0].individuals.length > 0 ? totalFitnessThisGen[0] / state.population.subpops[0].individuals.length : 0);

        state.output.print("" + meanFitnessThisGen[0] + " ", infoLog);
        state.population.subpops[0].individuals[best].printIndividualForHumans(state, infoLog);
    }
}
