package bot;

import bt.BehaviourTree;
import bt.utils.BehaviourTreeParser;
import map.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hallvard on 01.10.2015.
 */
public class BotStarterBt extends BotStarter {

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {

        BehaviourTree<BotState> tree = BehaviourTreeParser.generate(expression.get(), state);
        ArrayList<AttackTransferMove> moves = new ArrayList<>();

        state.resetAttMoves();

        List<Region> visible= state.getVisibleMap().getRegions();
        List<Region> friendly = new ArrayList<Region>();
        for(Region r : visible) {
            if(r.getPlayerName().equals(state.getMyPlayerName())) {
                friendly.add(r);
            }
        }
        outer:
        for(Region r : friendly) {
            state.setFocal(r);
            if(state.getAvailableArmies(r) < 2)
                continue;
            for(Region n : r.getNeighbors()) {
                state.setNonFocal(n);
                tree.step();
            }
        }
        return new ArrayList<AttackTransferMove>(state.getAttMoves());
    }
}
