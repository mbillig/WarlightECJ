package warlight2;

import com.theaigames.game.warlight2.GameResults;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.koza.KozaFitness;
import com.theaigames.game.warlight2.Warlight2;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import data_types.DoubleData;

/**
 * Created by Jonatan on 24-Sep-15.
 */
public class WarlightProblem extends GPProblem implements SimpleProblemForm {

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

            //System.out.println();
            //individual.printIndividualForHumans(evolutionState, 0);
            //System.out.println();

            try {
                String[] warlightArgs = new String[]{"map2.txt", "java -classpath out\\production\\myBot bot.BotStarter " + tree, "java -jar randomWarlightBot.jar", "100"};
                Warlight2.main(warlightArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double gameScore = GameResults.getInstance().getScore();
            double scoreRatio = 1- (gameScore/100);
            double landControlRatio = GameResults.getInstance().getLandControlledRatioPlayer1();
            double fitness = 1;

            if(GameResults.getInstance().getWinner() == 0 || GameResults.getInstance().getWinner() == 1)
                fitness = 1- (scoreRatio + landControlRatio)/2;
            //System.out.println("Score = " + GameResults.getInstance().getScore());

            System.out.println(tree);
            System.out.println("Gamescore " + scoreRatio);
            System.out.println("landControlRatio " + landControlRatio);
            System.out.println("Fitness = " + fitness);
            System.out.println("winner = " + GameResults.getInstance().getWinner());
            System.out.println("");

            KozaFitness f = ((KozaFitness) individual.fitness);
            f.setStandardizedFitness(evolutionState, fitness);
            individual.evaluated = true;
        }
    }
}
