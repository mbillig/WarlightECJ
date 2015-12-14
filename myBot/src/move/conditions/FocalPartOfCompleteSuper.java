package move.conditions;

import bot.BotState;
import bt.leaf.Condition;
import map.Region;

/**
 * Created by Hallvard on 02.10.2015.
 */
public class FocalPartOfCompleteSuper extends Condition<BotState> {

    @Override
    public String toString() {
        return "focalPartOfCompleteSuper";
    }

    @Override
    public void run() {
        BotState state = getBlackboard();
        for(Region r : state.getFocal().getSuperRegion().getSubRegions()) {
            if(!r.ownedByPlayer(state.getMyPlayerName())) {
                fail();
                return;
            }
        }
        success();
    }
}
