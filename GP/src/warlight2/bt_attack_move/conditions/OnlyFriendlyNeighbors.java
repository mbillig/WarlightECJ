package warlight2.bt_attack_move.conditions;

import bot.BotState;
import bt.leaf.Condition;
import map.Region;

/**
 * Created by Hallvard on 02.10.2015.
 */
public class OnlyFriendlyNeighbors extends Condition<BotState> {

    @Override
    public void run() {
        BotState state = getBlackboard();
        for(Region r : state.getNonFocal().getNeighbors())
            if(!r.ownedByPlayer(state.getMyPlayerName())) {
                fail();
                return;
            }
        success();
    }

    @Override
    public String toString() {
        return "onlyFriendlyNeighbors";
    }
}
