package move.conditions;

import bot.BotState;
import bt.leaf.Condition;

/**
 * Created by Hallvard on 02.10.2015.
 */
public class IsFriendly extends Condition<BotState> {

    @Override
    public void run() {
        BotState state = getBlackboard();
        if(state.getNonFocal().ownedByPlayer(state.getMyPlayerName()))
            success();
        else
            fail();
    }

    @Override
    public String toString() {
        return "isFriendly";
    }
}
