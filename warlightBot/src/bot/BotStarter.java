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

import map.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class BotStarter implements Bot {

    public static String argExpression = "";

    ScriptEngineManager engineManager =
            new ScriptEngineManager();
    ScriptEngine engine =
            engineManager.getEngineByName("nashorn");

    @Override
    /**
     * A method that returns which region the bot would like to start on, the pickable regions are stored in the BotState.
     * The bots are asked in turn (ABBAABBAAB) where they would like to start and return a single region each time they are asked.
     * This method returns one random region from the given pickable regions.
     */
    public Region getStartingRegion(BotState state, Long timeOut) {

        HashMap<Region, Double> scoredRegions = new HashMap<>();

        String expression = "Math.sqrt((0-(((AvgNeighbourScore-numFromSoldiers)/1/(AvgNeighbourScore)))-(0-(Math.sqrt(numFromSoldiers))/1/((superRegionAllegianceScore-superRegionAllegianceScore)))))";
        try {
            engine.eval("function tree(AvgNeighbourScore, SuperRegionScore, numFromSoldiers, superRegionAllegianceScore, regionNeighbourAllegianceScore) { return " + expression + "}");
            for (Region region : state.getPickableStartingRegions()){
                    scoredRegions.put(region, (Double) engine.eval("tree("
                            + getAvgNeighbourScore(region, state) + ","
                            + getSuperRegionScore(region, state) + ","
                            + getNumSoldiers(region, state) + ","
                            + getSuperRegionAllegianceScore(region, state) + ","
                            + getRegionNeighbourAllegianceScore(region, state) + ");"));
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return getMaxFromHashMap(scoredRegions).getKey();
    }

    public double getSuperRegionScore(Region region, BotState state) {
        return region.getSuperRegion().getArmiesReward()/region.getSuperRegion().getSubRegions().size();
        //return (region.getSuperRegion().getSubRegions().size() - region.getSuperRegion().getArmiesReward());
        //return region.getSuperRegion().getArmiesReward();
    }

    public double getSuperRegionAllegianceScore(Region region, BotState state) {
        double total = 0.0;
        for(Region subRegion: region.getSuperRegion().getSubRegions()){
            if(subRegion.ownedByPlayer(state.getMyPlayerName()))
                total -= 1;
            else if (subRegion.ownedByPlayer(state.getOpponentPlayerName()))
                total += 1;
        }
        return total/ region.getSuperRegion().getSubRegions().size();
    }

    public double getRegionNeighbourAllegianceScore (Region region, BotState state) {
        double total = 0.0;
        for(Region neighbour: region.getNeighbors()){
            if(neighbour.ownedByPlayer(state.getMyPlayerName()))
                total -= 1;
            else if (neighbour.ownedByPlayer(state.getOpponentPlayerName()))
                total += 1;
        }
        return total/ region.getNeighbors().size();
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

    public double getAllegiance(Region region, BotState state) {
        if (region.ownedByPlayer(state.getMyPlayerName()))
            return 1;
        else if (region.ownedByPlayer(state.getOpponentPlayerName()))
            return -1;
        else return 0;
    }

    public double getNumSoldiers(Region region, BotState state) {
        return region.getArmies();
    }

    public void printString(String output){
        PrintWriter printer;
        try {
            printer = new PrintWriter("botOutput.txt", "UTF-8");
            printer.write(output);
            printer.close();
/*                for (String word : individual.toString().split(" ")) {
                    printer.write(word);
                }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

        String expression = "Math.sqrt(0-((Math.sqrt(1/((superRegionAllegianceScore*superRegionAllegianceScore)))/(SuperRegionScore*Math.sqrt((numFromSoldiers+Math.sqrt(AvgNeighbourScore)))))))";
        try {
            engine.eval("function tree(AvgNeighbourScore, SuperRegionScore, numFromSoldiers, superRegionAllegianceScore, regionNeighbourAllegianceScore) { return " + expression + "}");
            for (Region region : state.getVisibleMap().getRegions()) {
                if (region.ownedByPlayer(myName))
                    scoredRegions.put(region, (Double) engine.eval("tree("
                            + getAvgNeighbourScore(region, state) + ","
                            + getSuperRegionScore(region, state) + ","
                            + getNumSoldiers(region, state) + ","
                            + getSuperRegionAllegianceScore(region, state) + ","
                            + getRegionNeighbourAllegianceScore(region, state) + ");"));
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        int availableUnits = state.getStartingArmies();
        int firstArmy, secondArmy = 0;

        if (availableUnits % 2 == 0) {
            firstArmy = availableUnits / 2;
            secondArmy = availableUnits / 2;
        } else {
            firstArmy = (availableUnits + 1) / 2;
            secondArmy = (availableUnits - 1) / 2;
        }

        //place first half of units
        Region bestRegion = getMaxFromHashMap(scoredRegions).getKey();
        placeArmiesMoves.add(new PlaceArmiesMove(myName, bestRegion, firstArmy));
        scoredRegions.remove(bestRegion);
        //place second half
        placeArmiesMoves.add(new PlaceArmiesMove(myName, getMaxFromHashMap(scoredRegions).getKey(), secondArmy));

        return placeArmiesMoves;
    }

    @Override
    /**

     */
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {

        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        String myName = state.getMyPlayerName();
        String expression = "((0-(allegiance)+(1/(0-(superRegionAllegianceScore))/(1/(SuperRegionScore)*Math.sqrt(numFromSoldiers))))+superRegionAllegianceScore)";
        int maxmoves = 10;

        try {
            engine.eval("function tree(SuperRegionScore, numFromSoldiers, numToSoldiers, superRegionAllegianceScore, regionNeighbourAllegianceScore,  allegiance) { return " + expression + "}");

            for (Region fromRegion : state.getVisibleMap().getRegions()) {
                if (fromRegion.ownedByPlayer(myName) && fromRegion.getArmies() > 5) {
                    HashMap<Region, Double> scoredRegions = new HashMap<>();

                    for (Region possibleToRegion : fromRegion.getNeighbors()) {
                        scoredRegions.put(possibleToRegion, (Double) engine.eval("tree("
                                + getSuperRegionScore(possibleToRegion, state) + ","
                                + getNumSoldiers(fromRegion, state) + "," +
                                + getNumSoldiers(possibleToRegion, state) + ","
                                + getSuperRegionAllegianceScore(possibleToRegion, state) + ","
                                + getRegionNeighbourAllegianceScore(possibleToRegion, state) + ","
                                + getAllegiance(possibleToRegion, state) + ");"));
                    }
                    attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, getMaxFromHashMap(scoredRegions).getKey(), fromRegion.getArmies() - 1));
                }
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return attackTransferMoves;
    }


    public static void main(String[] args) {
        argExpression = args[0];

        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }

}