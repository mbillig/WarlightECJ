package warlight2;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Problem;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * Created by Jonatan on 01-Oct-15.
 */
public class WarlightProblemCompetitive extends Problem implements GroupedProblemForm {
    @Override
    public void preprocessPopulation(EvolutionState evolutionState, Population population, boolean[] booleans, boolean b) {

    }

    @Override
    public void postprocessPopulation(EvolutionState evolutionState, Population population, boolean[] booleans, boolean b) {

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
    public void evaluate(EvolutionState evolutionState, Individual[] individuals, boolean[] booleans, boolean b, int[] ints, int i) {
        String tree0 = getTree(((GPIndividual)individuals[0]).trees[0].child).replace(" ", "");
        String tree1 = getTree(((GPIndividual)individuals[1]).trees[0].child).replace(" ", "");
    }
}
