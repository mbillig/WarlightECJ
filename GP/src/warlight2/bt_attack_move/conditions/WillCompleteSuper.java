package warlight2.bt_attack_move.conditions;

import bot.BotState;
import bt.leaf.Condition;
import map.Region;

/**
 * Created by Hallvard on 02.10.2015.
 */
public class WillCompleteSuper extends Condition<BotState> {

    @Override
    public void run() {
        BotState state = getBlackboard();
        if(state.getNonFocal().ownedByPlayer(state.getMyPlayerName())) {
            fail();
            return;
        }
        for(Region r : state.getNonFocal().getSuperRegion().getSubRegions()) {
            if(r == state.getNonFocal())
                continue;
            if(!r.ownedByPlayer(state.getMyPlayerName())) {
                fail();
                return;
            }
        }
        success();
    }

    @Override
    public String toString() {
        return "willCompleteSuper";
    }
}
