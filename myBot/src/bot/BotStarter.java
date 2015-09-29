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

        try (BufferedReader br = new BufferedReader(new FileReader("myBot\\src\\bot\\sense_world.txt"))) {

            //ECJ
            String expression = "";

            String line = br.readLine();
            while (line != null) {
                expression += line;
                line = br.readLine();
            }

            engine.eval("function tree(AvgNeighbourScore, SuperRegionScore) { return " + expression + "}");
            for (Region region : state.getPickableStartingRegions()) {
                scoredRegions.put(region, (Double) engine.eval("tree(" + getAvgNeighbourScore(region, state) + "," + getSuperRegionScore(region, state) + ");"));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        Map.Entry<Region,Double> maxEntry = null;
        for(Map.Entry<Region,Double> entry : scoredRegions.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();
    }

    public double getSuperRegionScore(Region region, BotState state) {
        //size - reward
        //return (region.getSuperRegion().getSubRegions().size() - region.getSuperRegion().getArmiesReward());
        return region.getSuperRegion().getArmiesReward();
    }

    public double getAvgNeighbourScore(Region region, BotState state) {
        LinkedList<Region> neighbours = region.getNeighbors();
        double sum = 0;
        for (Region neighbour : neighbours) {
            if (neighbour.ownedByPlayer(state.getMyPlayerName()))
                sum += 1;
            else if (neighbour.ownedByPlayer(state.getOpponentPlayerName()))
                sum -= 1;
            else
                sum +=0.2;
        }
        return sum / neighbours.size();
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

        try (BufferedReader br = new BufferedReader(new FileReader("myBot\\src\\bot\\sense_world.txt"))) {

            //ECJ
            String expression = "";

            String line = br.readLine();
            while (line != null) {
                expression += line;
                line = br.readLine();
            }

            engine.eval("function tree(AvgNeighbourScore, SuperRegionScore) { return " + expression + "}");
            for (Region region : state.getVisibleMap().getRegions()) {
                if(region.ownedByPlayer(myName))
                    scoredRegions.put(region, (Double) engine.eval("tree(" + getAvgNeighbourScore(region, state) + "," + getSuperRegionScore(region, state) + ");"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        Map.Entry<Region,Double> maxEntry = null;
        for(Map.Entry<Region,Double> entry : scoredRegions.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        placeArmiesMoves.add(new PlaceArmiesMove(myName, maxEntry.getKey(), state.getStartingArmies()));
        return placeArmiesMoves;
    }

    @Override
    /**
     * This method is called for at the second part of each round. This example attacks if a region has
     * more than 6 armies on it, and transfers if it has less than 6 and a neighboring owned region.
     * @return The list of PlaceArmiesMoves for one round
     */
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {

/*
        ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
        String myName = state.getMyPlayerName();
        int armies = 5;
        int maxTransfers = 10;
        int transfers = 0;

        for (Region fromRegion : state.getVisibleMap().getRegions()) {
            if (fromRegion.ownedByPlayer(myName)) //do an attack
            {
                ArrayList<Region> possibleToRegions = new ArrayList<Region>();
                possibleToRegions.addAll(fromRegion.getNeighbors());

                while (!possibleToRegions.isEmpty()) {
                    double rand = Math.random();
                    int r = (int) (rand * possibleToRegions.size());
                    Region toRegion = possibleToRegions.get(r);

                    if (!toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 6) //do an attack
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        break;
                    } else if (toRegion.getPlayerName().equals(myName) && fromRegion.getArmies() > 1
                            && transfers < maxTransfers) //do a transfer
                    {
                        attackTransferMoves.add(new AttackTransferMove(myName, fromRegion, toRegion, armies));
                        transfers++;
                        break;
                    } else
                        possibleToRegions.remove(toRegion);
                }
            }
        }
*/

        return null;
    }

    public static void main(String[] args) {
        try {
            OutputStreamWriter log = new OutputStreamWriter(new FileOutputStream(" C:\\Users\\Jonatan\\workspace\\ecj\\myBotLog.txt"));
            log.write("hello");
            log.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }

}