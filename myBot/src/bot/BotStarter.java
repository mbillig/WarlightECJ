/**
 * Warlight AI Game Bot
 * <p>
 * Last update: January 29, 2015
 *
 * @author Jim van Eeden
 * @version 1.1
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */

package bot;

/**
 * This is a simple bot that does random (but correct) moves.
 * This class implements the Bot interface and overrides its Move methods.
 * You can implement these methods yourself very easily now,
 * since you can retrieve all information about the match from variable “state”.
 * When the bot decided on the move to make, it returns an ArrayList of Moves.
 * The bot is started by creating a Parser to which you add
 * a new instance of your bot, and then the parser is started.
 */

import java.io.*;
import java.util.*;
import java.util.logging.*;

import map.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class BotStarter implements Bot {

    ScriptEngineManager engineManager =
            new ScriptEngineManager();
    ScriptEngine engine =
            engineManager.getEngineByName("nashorn");

    public static Optional<String> expression;

    public final static Logger LOGGER = Logger.getLogger(BotStarter.class.getName());
    static {
        try {
            Handler handler = new FileHandler("C:\\work\\Master\\WarlightECJ\\botlogTestererer.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
        } catch (IOException e) {e.printStackTrace();}
        LOGGER.log(Level.SEVERE, "YO");
    }

    @Override
    /**
     * A method that returns which region the bot would like to start on, the pickable regions are stored in the BotState.
     * The bots are asked in turn (ABBAABBAAB) where they would like to start and return a single region each time they are asked.
     * This method returns one random region from the given pickable regions.
     */
    public Region getStartingRegion(BotState state, Long timeOut) {

        HashMap<Region, Double> scoredRegions = new HashMap<>();

        String expression = getDummyExpression();
        try {
            engine.eval("function tree(AvgNeighbourScore, SuperRegionScore) { return " + expression + "}");
            for (Region region : state.getPickableStartingRegions()) {
                scoredRegions.put(region, (Double) engine.eval("tree(" + getAvgNeighbourScore(region, state) + "," + getSuperRegionScore(region, state) + ");"));
            }

        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return getMaxFromHashMap(scoredRegions).getKey();
    }

    public double getSuperRegionScore(Region region, BotState state) {
        //size - reward
        return (region.getSuperRegion().getSubRegions().size() - region.getSuperRegion().getArmiesReward());
        //return region.getSuperRegion().getArmiesReward();
    }

    public double getAvgNeighbourScore(Region region, BotState state) {
        LinkedList<Region> neighbours = region.getNeighbors();
        double sum = 0;
        for (Region neighbour : neighbours) {
            if (neighbour.ownedByPlayer(state.getMyPlayerName()))
                sum += 0.5;
            else if (neighbour.ownedByPlayer(state.getOpponentPlayerName()))
                sum += 1;
            else
                sum += 0.1;
        }
        return sum / neighbours.size();
    }

    public Map.Entry<Region, Double> getMaxFromHashMap(HashMap<Region, Double> input) {
        Map.Entry<Region, Double> maxEntry = null;
        for (Map.Entry<Region, Double> entry : input.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return maxEntry;
    }

    public double getEnemyOrFriendly(Region region, BotState state) {
        if (region.ownedByPlayer(state.getMyPlayerName()))
            return 0;
        else
            return 1;
    }

    public double getNumSoldiers(Region region, BotState state) {
        return region.getArmies();
    }

    public String getDummyExpression(){
        return "Math.sqrt (0-SuperRegionScore)";
    }

    public String getExpression() {
        String expression = "";
        try (BufferedReader br = new BufferedReader(new FileReader("myBot\\src\\bot\\sense_world.txt"))) {

            String line = br.readLine();
            while (line != null) {
                expression += line;
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return expression;
        //return "Math.tan( 0-( 0-( Math.sqrt(( Math.sqrt( 0-( SuperRegionScore )) /  Math.sin( SuperRegionScore ))))))";
        // return "SuperRegionScore - Math.cos (SuperRegionScore)";
        // return "Math.sqrt (0-SuperRegionScore)
    }

    @Override
    /**
     * This method is called for at first part of each round. This example puts two armies on random regions
     * until he has no more armies left to place.
     * @return The list of PlaceArmiesMoves for one round
     */
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {

        HashMap<Region, Double> scoredRegions = new HashMap<>();
        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
        String myName = state.getMyPlayerName();

        String expression = getDummyExpression();
        try {
            engine.eval("function tree(AvgNeighbourScore, SuperRegionScore) { return " + expression + "}");
            for (Region region : state.getVisibleMap().getRegions()) {
                if (region.ownedByPlayer(myName))
                    scoredRegions.put(region, (Double) engine.eval("tree(" + getAvgNeighbourScore(region, state) + "," + getSuperRegionScore(region, state) + ");"));
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        int availableUnits = state.getStartingArmies();
        int firstArmy, secondArmy = 0;

        if(availableUnits%2==0){
            firstArmy = availableUnits/2;
            secondArmy = availableUnits/2;
        } else{
            firstArmy = (availableUnits+1)/2;
            secondArmy = (availableUnits-1)/2;
        }

        //place first half of units
        Region bestRegion = getMaxFromHashMap(scoredRegions).getKey();
        placeArmiesMoves.add(new PlaceArmiesMove(myName, bestRegion, firstArmy));
        scoredRegions.remove(bestRegion);
        //place second half
        return placeArmiesMoves;
    }

    @Override
    /**

     */
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {

        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        String myName = state.getMyPlayerName();
        String expression = getExpression();
        int maxmoves = 10;

        try {
            engine.eval("function tree(AvgNeighbourScore, SuperRegionScore, numSoldiers, enemyOrFriendly) { return " + expression + "}");

            for (Region fromRegion : state.getVisibleMap().getRegions()) {
                if (maxmoves > 0 && fromRegion.ownedByPlayer(myName) && fromRegion.getArmies() > 4) {
                    HashMap<Region, Double> scoredRegions = new HashMap<>();

                    for (Region possibleToRegion : fromRegion.getNeighbors()) {
                        scoredRegions.put(possibleToRegion, (Double) engine.eval("tree("
                                + getAvgNeighbourScore(possibleToRegion, state) + ","
                                + getSuperRegionScore(possibleToRegion, state) + ","
                                + getNumSoldiers(possibleToRegion, state) + "," +
                                + getEnemyOrFriendly(possibleToRegion, state) + ")"));
                    }
                    attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, getMaxFromHashMap(scoredRegions).getKey(), fromRegion.getArmies() - 1));
                    maxmoves--;
                }
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return attackTransferMoves;
    }

    public static void main(String[] args) {

        expression = Optional.of(args[0]);

        BotParser parser = new BotParser(new BotStarterBt());
        parser.run();
    }

}