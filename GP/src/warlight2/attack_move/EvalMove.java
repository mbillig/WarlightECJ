package warlight2.attack_move;

import com.theaigames.game.warlight2.GameResults;
import com.theaigames.game.warlight2.Warlight2;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import warlight2.data_types.DoubleData;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Jonatan on 30-Sep-15.
 */
public class EvalMove  extends GPProblem implements SimpleProblemForm {
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

            String tree = getTree(((GPIndividual) individual).trees[0].child);
            // System.out.println(tree);

            PrintWriter printer;
            try {
                printer = new PrintWriter("myBot\\src\\bot\\sense_world.txt", "UTF-8");
                printer.write(tree);
                printer.close();
/*                for (String word : individual.toString().split(" ")) {
                    printer.write(word);
                }*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            System.out.println();
            System.out.println(tree);
            // individual.printIndividualForHumans(evolutionState, 0);
            System.out.println();

            String[] warlightArgs = new String[]{"map.txt", "java -jar myBot.jar", "java -jar randomBot.jar"};

            try {
                Warlight2.main(warlightArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Connect to warlight and calculate fitness
/*            double fitness =(double) (GameResults.getInstance().getWinner()
                    + (GameResults.getInstance().getScore() / 160));*/

            double fitness = 1 - GameResults.getInstance().getLandControlledRatio();
            System.out.println("winner = " + GameResults.getInstance().getWinner());
            System.out.println("Score = " + GameResults.getInstance().getScore());
            System.out.println("LandControlledRatio = " + GameResults.getInstance().getLandControlledRatio());
            System.out.println("Fitness = " + fitness);

            KozaFitness f = ((KozaFitness) individual.fitness);
            f.setStandardizedFitness(evolutionState, fitness);
            individual.evaluated = true;
        }
    }
}
