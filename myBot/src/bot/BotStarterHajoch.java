package bot;

import map.*;
import move.AttackTransferMove;
import move.Move;
import move.PlaceArmiesMove;

import java.util.*;
import java.util.Map;

/**
 * Created by Hallvard on 07.10.2015.
 */
public class BotStarterHajoch implements Bot {

    HashMap<SuperRegion, Region> startLookup = null;

    Options plans = null;
    Options deployedPlans = null;

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


        final int armies = state.getStartingArmies();
        int used = 0;

        if(null==plans)
            plans = makePlans(state);
        if(plans.round != state.getRoundNumber())
            plans = makePlans(state);

        this.deployedPlans = new Options(state.getRoundNumber());
        deployedPlans.transfers.addAll(plans.transfers);

        int defCost = 0;
        for(Encounter d : plans.defenses) {
            int cost = d.probDefenseCost - d.from.getArmies();
            defCost += (cost < 0 ? 0: cost);
        }
        if(!(armies < enemyAnalysis(state).armiesEarnings))
        for(Encounter d : plans.defenses) {
            if(defCost > armies) {
                placements.add(new PlaceArmiesMove(state.getMyPlayerName(), d.from, d.minDefenseCost));
            } else {
                placements.add(new PlaceArmiesMove(state.getMyPlayerName(),d.from, d.probDefenseCost));
            }
        }

        //Take super
        for(Occupation o: plans.gainings) {
            if(used >= armies)
                break;
            for(Region r : o.attacks.keySet()) {
                    int armiesDiff = r.getArmies();
                    ArrayList<AttackTransferMove> temp = new ArrayList<>();
                    for(Region rr : o.attacks.get(r)) {
                        int extra = (Math.random() > 0.6 ? 1 : 2);
                        armiesDiff -= (rr.getArmies()+extra);
                        temp.add(new AttackTransferMove(state.getMyPlayerName(), r,rr, rr.getArmies()+extra));
                    }
                    if(armiesDiff < 0 && (-armiesDiff)>(armies-used) && armies-used<0){
                        break;
                    } else {
                        placements.add(new PlaceArmiesMove(state.getMyPlayerName(), r, (-armiesDiff)));
                        deployedPlans.attacks.addAll(temp);
                    }
            }
        }

        for(AttackTransferMove move : plans.attacks) {
            if(used >= armies) break;
            int cost = move.getArmies() - move.getFromRegion().getArmies();
            if(!(used + cost > armies)){
                deployedPlans.attacks.add(move);
                placements.add(new PlaceArmiesMove(state.getMyPlayerName(), move.getFromRegion(), cost));
            }
        }
        if(used < armies) {
            for(Map.Entry<Region, Region> entry : plans.expansions.entrySet()) {
                deployedPlans.expansions.put(entry.getKey(), entry.getValue());
                placements.add(new PlaceArmiesMove(state.getMyPlayerName(), entry.getKey(),
                        (entry.getValue().getArmies()+2) - entry.getKey().getArmies()));
            }
        }
        return placements;
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {

        ArrayList<AttackTransferMove> moves = new ArrayList<AttackTransferMove>();

        moves.addAll(deployedPlans.attacks);
        Collections.reverse(moves);

        moves.addAll(deployedPlans.transfers);

        return moves;
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

        }

        return null;
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

    private Options makePlans(BotState state) {

        //Occupy
        ArrayList<Occupation> occupationPlans = new ArrayList<>();

        for(SuperRegion sr : state.getVisibleMap().getSuperRegions()) {
            int cost = 0;
            int costExtra = 0;
            boolean enemyInside = false;
            int armiesDeployed = 0;
            int areasMissing = 0;

            //Can the superregion be yours in this round?
            LinkedList<Region> subtractions = new LinkedList<>();
            LinkedList<Region> regions = new LinkedList<>(sr.getSubRegions());

            HashMap<Region, ArrayList<Region>> attacks = new HashMap<>();

            for(Region r : regions) {
                int armiesCost = 0;
                if(r.ownedByPlayer(state.getMyPlayerName())) {
                    subtractions.addAll(r.getNeighbors());
                    subtractions.add(r);
                    armiesDeployed += r.getArmies();
                    for(Region neigh : r.getNeighbors()) {
                        if(!neigh.ownedByPlayer(state.getMyPlayerName())
                                && neigh.getSuperRegion()==sr
                                && !attacks.containsValue(neigh)) {
                            if(attacks.containsKey(r)){
                                attacks.get(r).add(neigh);
                            } else {
                                ArrayList<Region> list = new ArrayList<>();
                                list.add(neigh);
                                attacks.put(r, list);
                            }
                            armiesCost += neigh.getArmies()+1;
                        }
                    }
                } else {
                    areasMissing++;
                    if(r.ownedByPlayer(state.getOpponentPlayerName())) {
                        enemyInside=true;
                    }
                    cost += r.getArmies();
                }
                if(armiesCost < r.getArmies()) {
                    costExtra += (armiesCost-r.getArmies());
                }
            }
            regions.removeAll(subtractions);
            if(regions.size() == 0) {
                Occupation p = new Occupation();
                p.attacks = attacks;
                p.cost = cost+areasMissing;
                p.enemy = enemyInside;
                p.sr = sr;
                p.costExtra = costExtra;
                p.gain = sr.getArmiesReward();
                occupationPlans.add(p);
            }
        }

        HashMap<Region, Region> expansions = new HashMap<>();
        ArrayList<Encounter> defense = new ArrayList<>();
        ArrayList<AttackTransferMove> attackOptions = new ArrayList<>();
        ArrayList<AttackTransferMove> transfers = new ArrayList<>();

        for(Region r: state.getVisibleMap().getRegions()) {
            if(r.ownedByPlayer(state.getMyPlayerName())) {
                boolean neighboringEnemy = false;
                boolean partOfCompleteSuper = true;
                boolean onlyFriendlyNeighbors = true;
                int enemyArmies = 0;

                for(Region rr : r.getNeighbors()) {
                    if(rr.ownedByPlayer(state.getOpponentPlayerName())) {
                        neighboringEnemy = true;
                        enemyArmies += rr.getArmies();
                        onlyFriendlyNeighbors = false;
                        attackOptions.add(new AttackTransferMove(state.getMyPlayerName(), r,rr, rr.getArmies()+enemyAnalysis(state).armiesEarnings));
                    } else {
                        if(!rr.ownedByPlayer(state.getMyPlayerName())) {
                            onlyFriendlyNeighbors = false;
                            boolean noEnemy = true;
                            if(rr.getSuperRegion()!=r.getSuperRegion()) {
                                for (Region rrs : rr.getSuperRegion().getSubRegions()) {
                                    if (rrs.ownedByPlayer(state.getOpponentPlayerName())) {
                                        noEnemy = false;
                                        break;
                                    }
                                }
                                if(noEnemy)
                                    expansions.put(r, rr);
                            }
                        }
                    }
                }
                for(Region rr : r.getSuperRegion().getSubRegions()) {
                    if(!rr.ownedByPlayer(state.getMyPlayerName())) {
                        partOfCompleteSuper = false;
                    }
                }
                if(neighboringEnemy && partOfCompleteSuper) {
                    defense.add(new Encounter(
                            r, enemyArmies, enemyArmies+enemyAnalysis(state).armiesEarnings,
                            r.getSuperRegion().getArmiesReward()
                    ));
                }
                if(onlyFriendlyNeighbors){
                    transfers.add(new AttackTransferMove(state.getMyPlayerName(), r, null, r.getArmies()-1));
                }
            }
        }
        Options options = new Options(defense, attackOptions, occupationPlans, expansions, transfers, state.getRoundNumber());
        return options;
    }

    static class Options {
        public final ArrayList<Encounter> defenses;
        public final ArrayList<AttackTransferMove> attacks;
        public final ArrayList<Occupation> gainings;
        public final HashMap<Region, Region> expansions;
        public final ArrayList<AttackTransferMove> transfers;
        public final int round;

        Options(int round) {
            this.defenses = new ArrayList<>();
            this.attacks = new ArrayList<>();
            this.gainings = new ArrayList<>();
            this.expansions = new HashMap<>();
            this.transfers = new ArrayList<>();
            this.round = round;
        }
        Options(ArrayList<Encounter> defenses, ArrayList<AttackTransferMove> attacks, ArrayList<Occupation> gainings, HashMap<Region, Region> expansions, ArrayList<AttackTransferMove> transfers, int round) {
            this.defenses = defenses;
            this.attacks = attacks;
            this.gainings = gainings;
            this.expansions = expansions;
            this.transfers = transfers;
            this.round = round;
        }
    }

    static class Encounter  {
        Region from;
        Region to;
        int minDefenseCost;
        int probDefenseCost;
        int potentialLoss;
        public Encounter(Region from, int min, int prob, int loss) {
            this.from = from;
            this.minDefenseCost = min;
            this.probDefenseCost = prob;
            this.potentialLoss = loss;
        }
    }

    static class Occupation {
        public SuperRegion sr;
        public HashMap<Region, ArrayList<Region>> attacks;
        public int cost;
        public int costExtra;
        public int gain;
        public boolean enemy;
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

    public static void main(String[] args) {

        BotParser parser = new BotParser(new BotStarterHajoch());
        parser.run();
    }


}
