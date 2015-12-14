package move.conditions;

import bot.BotState;
import bt.leaf.Condition;

/**
 * Created by Hallvard on 02.10.2015.
 */
public class FocalStronger extends Condition<BotState> {
    @Override
    public void run() {
        BotState state = getBlackboard();
        if(state.getFocal().getArmies() > state.getNonFocal().getArmies())
            success();
        else
            fail();
    }

    @Override
    public String toString() {
        return "focalStronger";
    }
}
