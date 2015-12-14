package move.conditions;

import bot.BotState;
import bt.leaf.Condition;

/**
 * Created by Hallvard on 07.10.2015.
 */
public class PartOfSameSuper extends Condition<BotState> {

    @Override
    public void run() {
        BotState state = getBlackboard();

        if(state.getFocal().getSuperRegion() == state.getNonFocal().getSuperRegion())
            success();
        else
            fail();
    }

    @Override
    public String toString() {
        return "partOfSameSuper";
    }
}
