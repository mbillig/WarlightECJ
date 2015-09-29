package warlight2.sense_world;

import com.theaigames.game.warlight2.GameResults;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPTree;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleFitness;
import warlight2.JarCompiler;
import com.theaigames.game.warlight2.Warlight2;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPProblem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import warlight2.DoubleData;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

/**
 * Created by Jonatan on 24-Sep-15.
 */
public class SenseWorld extends GPProblem implements SimpleProblemForm {

    public static final String P_DATA = "data";

    public int AvgNeighbourScore;
    public int SuperRegionScore;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        if (!(input instanceof DoubleData)) {
            state.output.fatal("GPData class must subclass from " + DoubleData.class, base.push(P_DATA), null);
        }
    }

    public String getTree(GPNode node) {
        if (node.children.length != 0)
            return "(" + getTree(node.children[0]) + node.toString() + getTree(node.children[1]) + ")";
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

            JarCompiler JC = new JarCompiler();

            try {
                JC.run("out\\production\\mybot", "myBot.jar");
            } catch (IOException ioe) {
                System.out.println("io exception");
                ioe.printStackTrace();
            }
            System.out.println();
            individual.printIndividualForHumans(evolutionState, 0);
            System.out.println();

            String[] warlightArgs = new String[]{"map.txt", "java -jar myBot.jar", "java -jar randomBot.jar"};

            try {
                Warlight2.main(warlightArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Connect to warlight and calculate fitness
            double fitness =(double) (GameResults.getInstance().getWinner()
                    + (GameResults.getInstance().getScore() / 160));
            System.out.println("winner = " + GameResults.getInstance().getWinner());
            System.out.println("Score = " + GameResults.getInstance().getScore());
            System.out.println("Fitness = " + fitness);

            KozaFitness f = ((KozaFitness) individual.fitness);
            f.setStandardizedFitness(evolutionState, fitness);
            individual.evaluated = true;
        }
    }
}
