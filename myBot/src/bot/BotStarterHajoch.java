package bot;

import map.*;
import move.AttackTransferMove;
import move.Move;
import move.PlaceArmiesMove;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map;

/**
 * Created by Hallvard on 07.10.2015.
 */
public class BotStarterHajoch implements Bot {

    HashMap<SuperRegion, Region> startLookup = null;

    @Override
    public Region getStartingRegion(BotState state, Long timeOut) {
        //Init
        if(null==startLookup)
            startLookup = getBestRegionPerSuper(state);
        //Choses the best Region for the Best SuperRegion available
        SuperRegion best = null;
        for(SuperRegion sr : startLookup.keySet()) {
            if(null==best)
                best = sr;
             else if(superRegionRatio(sr) > superRegionRatio(best))
                best = sr;
        }
        //Backup: Random Region is chosen
        if(startLookup.size() == 0) {
            List<Region> pickable = state.getPickableStartingRegions();
            return pickable.get(new Random().nextInt(pickable.size()));
        }

        return startLookup.remove(best);
    }

    @Override
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {

        ArrayList<PlaceArmiesMove> placements = new ArrayList<PlaceArmiesMove>();

        map.Map map = state.getVisibleMap();

        LinkedList<Region> regions = map.getRegions();

        Map<Region, Integer> need = new HashMap<>();

        for(Region r : regions) {
            if(r.ownedByPlayer(state.getMyPlayerName())) {
                int rNeed = getNeed(r);
            }
        }



        return null;
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
        return null;
    }

    private Need getNeed(Region r, BotState state) {
        LinkedList<Region> neighList = r.getNeighbors();

        boolean partOfCompleteSuper = true;
        int regionsMissingSuper = 0;
        boolean superContainsEnemy = false;
        int armiesInSuper = 0;
        int myArmiesInSuper = 0;

        for(Region rr : r.getSuperRegion().getSubRegions()) {
            if (!rr.ownedByPlayer(state.getMyPlayerName())) {
                partOfCompleteSuper = false;
                regionsMissingSuper++;
                if(rr.ownedByPlayer(state.getOpponentPlayerName()))
                    superContainsEnemy = true;
                else
                    armiesInSuper += rr.getArmies();
            } else {
                myArmiesInSuper += rr.getArmies();
            }
        }

        int regionsOccupiedInSuper = r.getSuperRegion().getSubRegions().size() - regionsMissingSuper;
        int enemyForcesClose = 0;

        for(Region rr : neighList) {
            if(rr.ownedByPlayer(state.getOpponentPlayerName())) {
                enemyForcesClose += rr.getArmies();
            }
        }


        if(partOfCompleteSuper && enemyForcesClose > 0){
            int myArmies = r.getArmies();
            int certainEnemyGain = enemyAnalysis(state).armiesEarnings;
            return new Need(Need.Type.DEFEND, (enemyForcesClose+certainEnemyGain-myArmies));
        }

        if(!partOfCompleteSuper && !superContainsEnemy) {
            int need = armiesInSuper - r.getArmies();


            if(need <)
            return new Need(Need.Type.OCCUPY, )
        }

        return 0;
    }

    private EnemyAnalysis enemyAnalysis(BotState state) {
        ArrayList<Move> moves = state.getOpponentMoves();

        int armiesEarned = 0;

        for(Move move : moves){
            if(move instanceof PlaceArmiesMove) {
                armiesEarned += ((PlaceArmiesMove) move).getArmies();
            }
        }
        return new EnemyAnalysis(armiesEarned);

    }

    static class EnemyAnalysis {
        final int armiesEarnings;
        public EnemyAnalysis(int armiesEarnings){
            this.armiesEarnings = armiesEarnings;
        }
    }

    static class Need implements Comparable<Need>{
        enum Type {
            DEFEND,
            ATTACK,
            OCCUPY
        }
        final int armies;
        final Type type;
        int gain = 0;

        public Need(Type type, int armies) {
            this.armies = armies;
            this.type = type;
        }

        @Override
        public int compareTo(Need o) {
            return 0;
        }
    }

    private HashMap<SuperRegion, Region> getBestRegionPerSuper(BotState state) {

        HashMap<SuperRegion, Region> srl = new HashMap<>();
        for(Region r : state.getPickableStartingRegions()) {
            SuperRegion sr = r.getSuperRegion();
            if(!srl.containsKey(sr)) {
                srl.put(sr, r);
            } else {
                Region other = srl.get(sr);
                if(neighborsInSameSuper(r) > neighborsInSameSuper(other))
                    srl.put(sr, r);
            }
        }
        return srl;
    }

    private double superRegionRatio(SuperRegion sr) {
        return sr.getArmiesReward()/sr.getSubRegions().size();
    }

    private int neighborsInSameSuper(Region region) {
        SuperRegion sr = region.getSuperRegion();
        int count = 0;
        for(Region r : region.getNeighbors())
            if(r.getSuperRegion()==sr)
                count++;
        return count;
    }


}
