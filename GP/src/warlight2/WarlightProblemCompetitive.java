package warlight2;

import com.theaigames.game.warlight2.GameResults;
import com.theaigames.game.warlight2.Warlight2;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Problem;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleFitness;

import java.util.ArrayList;

/**
 * Created by Jonatan on 01-Oct-15.
 */
public class WarlightProblemCompetitive extends Problem implements GroupedProblemForm {

    //initialise trials to an arraylist
    @Override
    public void preprocessPopulation(EvolutionState evolutionState, Population population, boolean[] updateFitness, boolean countVictoriesOnly) {
        if (updateFitness[0]) {
            for (int i = 0; i < population.subpops[0].individuals.length; i++) {
                ((SimpleFitness) (population.subpops[0].individuals[i].fitness)).trials = new ArrayList();
            }
        }
    }

    @Override
    public void postprocessPopulation(EvolutionState state, Population population, boolean[] updateFitness, boolean countVictoriesOnly) {
        if (updateFitness[0]) {
            for (int i = 0; i < population.subpops[0].individuals.length; i++) {
                SimpleFitness fit = ((SimpleFitness) (population.subpops[0].individuals[i].fitness));

                // average of the trials we got
                int len = fit.trials.size();
                double sum = 0;
                for (int l = 0; l < len; l++)
                    sum += ((Double) (fit.trials.get(l))).doubleValue();
                sum /= len;

                fit.setFitness(state, sum, false);
                population.subpops[0].individuals[i].evaluated = true;

                fit.trials = null;
            }
        }
    }

    public String getTree(GPNode node) {
        if (node.children.length == 2)
            return "(" + getTree(node.children[0]) + node.toString() + getTree(node.children[1]) + ")";
        else if (node.children.length == 1)
            return node.toString() + "(" + getTree(node.children[0]) + ")";
        else
            return node.toString();
    }

    @Override
    public void evaluate(EvolutionState state, Individual[] individuals, boolean[] updateFitness, boolean countVictoriesOnly, int[] ints, int i) {
        String tree0 = getTree(((GPIndividual) individuals[0]).trees[0].child).replace(" ", "");
        String tree1 = getTree(((GPIndividual) individuals[1]).trees[0].child).replace(" ", "");

        try {
            String[] warlightArgs = new String[]{"map.txt", "java -classpath out\\production\\myBot bot.BotStarter " + tree0, "java -classpath out\\production\\myBot bot.BotStarter " + tree1};
            Warlight2.main(warlightArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        double fitness0 = 1 - GameResults.getInstance().setLandControlledRatioPlayer1();
        double fitness1 = 1 - GameResults.getInstance().setLandControlledRatioPlayer2();
        double score = fitness0 - fitness1;

        if (updateFitness[0]) {
            SimpleFitness fit = ((SimpleFitness) (individuals[0].fitness));
            fit.trials.add(new Double(score));

            // set the fitness because if we're doing Single Elimination Tournament, the tournament
            // needs to know who won this time around.  Don't bother declaring the ideal here.
            fit.setFitness(state, score, false);
        }

        if (updateFitness[1]) {
            SimpleFitness fit = ((SimpleFitness) (individuals[1].fitness));
            fit.trials.add(new Double(-score));

            // set the fitness because if we're doing Single Elimination Tournament, the tournament
            // needs to know who won this time around.
            fit.setFitness(state, -score, false);
        }


/*
        KozaFitness f0 = ((KozaFitness) individuals[0].fitness);
        f0.setStandardizedFitness(state, fitness0);
        individuals[0].evaluated = true;
        updateFitness[0] = true;

        KozaFitness f1 = ((KozaFitness) individuals[1].fitness);
        f1.setStandardizedFitness(state, fitness1);
        individuals[1].evaluated = true;
        updateFitness[1] = true;*/
        System.out.println(tree0);
        System.out.println("        VS");
        System.out.println(tree1);
        System.out.println("Fitness = " + score);
        System.out.println("");
    }
}
