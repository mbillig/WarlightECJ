package texasholdem;

import com.theaigames.game.texasHoldem.TexasHoldem;
import com.theaigames.game.texasHoldem.TexasResults;
import com.theaigames.game.warlight2.GameResults;
import com.theaigames.game.warlight2.Warlight2;
import data_types.DoubleData;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

/**
 * Created by Jonatan on 06-Oct-15.
 */
public class TexasProblem extends GPProblem implements SimpleProblemForm {

    public static final String P_DATA = "data";

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        if (!(input instanceof DoubleData)) {
            state.output.fatal("GPData class must subclass from " + DoubleData.class, base.push(P_DATA), null);
        }
    }

    public String getTree(GPNode node) {
        if (node.children.length == 2)
            return "(" + getTree(node.children[0]) + node.toString() + getTree(node.children[1]) + ")";
        else if(node.children.length == 1)
            return node.toString() + "(" + getTree(node.children[0]) + ")";
        else
            return node.toString();
    }
    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i1) {

        if (!individual.evaluated) {
            DoubleData input = (DoubleData) (this.input);

            String tree = getTree(((GPIndividual) individual).trees[0].child).replace(" ", "");
            double runCount = 10;

            KozaFitness f = ((KozaFitness) individual.fitness);
            double wins = 0;
            try {
                String[] texasArgs = new String[]{"java -classpath out\\production\\texasbot bot.BotStarter " + tree, "java -jar randomTexasBot.jar"};
                for(int x = 0; x <runCount; x++){
                    TexasHoldem.main(texasArgs);
                    wins += TexasResults.getInstance().getWinner() == 1? 1: 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            double fitness = (double)(1-(wins/runCount));
            System.out.println(tree);
            System.out.println("wins = " + wins+"/"+runCount);


            f.setStandardizedFitness(evolutionState, fitness);
            f.hits = (int)wins;
            System.out.println(f.fitnessToStringForHumans());
            System.out.println();
            individual.evaluated = true;
        }
    }
}
