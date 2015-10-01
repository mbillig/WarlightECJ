package warlight2.bt_test;

import bt.leaf.Condition;
import bt.utils.BooleanData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;

/**
 * Created by Hallvard on 01.10.2015.
 */
public class Random<E> extends Condition<E> {

    @Override
    public String toString() {
        return "random";
    }

    @Override
    public void eval(EvolutionState evolutionState, int i, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        BooleanData dat = (BooleanData)gpData;
        dat.result = (Math.random() > 0.5 ? true : false);
    }
}
