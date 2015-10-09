package warlight2.bt_attack_move.actions;

import bot.BotState;
import bt.leaf.Action;
import bt.utils.BooleanData;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import map.Region;
import move.AttackTransferMove;

/**
 * Created by Hallvard on 02.10.2015.
 */
public class RandomSuperiorAttack extends Action<BotState> {

    @Override
    public void run() {
        BotState state = getBlackboard();

        Region focal = state.getFocal();
        Region nonFocal = state.getNonFocal();

        int fa = state.getAvailableArmies(focal);
        int nfa = nonFocal.getArmies();

        if( !(fa > nfa) ) {
            fail();
            return;
        }
        int armies = nfa + (int)(Math.random()*(fa - nfa));

        AttackTransferMove move = new AttackTransferMove(state.getMyPlayerName(), focal, nonFocal, armies);
        state.addAttMove(move);
        success();
    }

    @Override
    public String toString() {
        return "randomSuperiorAttack";
    }

    @Override
    public void eval(EvolutionState evolutionState, int i, GPData gpData, ADFStack adfStack, GPIndividual gpIndividual, Problem problem) {
        BooleanData dat = (BooleanData)gpData;
        dat.result = Math.random() > 0.7;
    }
}
