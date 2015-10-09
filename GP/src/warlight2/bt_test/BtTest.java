package warlight2.bt_test;

import bt.utils.BooleanData;
import com.theaigames.game.warlight2.GameResults;
import com.theaigames.game.warlight2.Warlight2;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Hallvard on 01.10.2015.
 */
public class BtTest extends GPProblem implements SimpleProblemForm {

    @Override
    public void evaluate(EvolutionState evolutionState, Individual individual, int i, int i2) {
        if(!individual.evaluated) {
            BooleanData input = (BooleanData)this.input;
            String tree = (((GPIndividual)individual).trees[0].child).toString();
            PrintWriter printer;
            try {
                printer = new PrintWriter("mybot\\src\\bot\\bt_test.txt", "UTF-8");
                printer.write(tree);
                printer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //tree = "selector[inverter(selector[isFriendly,inverter(focalStronger),randomSuperiorAttack]),inverter(selector[inverter(isFriendly),focalStronger,maxAttack]), succeeder(isFriendly)]";

            System.out.println("\n"+tree+"\n");


            String[] warlightArgs = new String[] {"map.txt", "java -classpath out\\production\\myBot bot.BotStarterBt "+tree, "java -jar randomBot.jar"};

            try {
                Warlight2.main(warlightArgs);
            } catch (Exception e) {e.printStackTrace();}

            int winner = GameResults.getInstance().getWinner();
            int rounds = GameResults.getInstance().getScore();
            double landControlled = GameResults.getInstance().getLandControlledRatio();

            double roundRatio = 1 - (rounds/60);

            double winBonus = roundRatio*0.5;
            double areaBonus = landControlled*0.5;

            double fitness = 1 - (winBonus+areaBonus);
            if(winner == 2)
                fitness = 1;

            System.out.println("Fitness: "+fitness);

            KozaFitness f = (KozaFitness)individual.fitness;
            f.setStandardizedFitness(evolutionState, fitness);
            individual.evaluated = true;
        }
    }
}