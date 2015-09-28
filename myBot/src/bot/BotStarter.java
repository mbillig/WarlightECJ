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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import bsh.EvalError;
import bsh.Interpreter;
import map.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;


public class BotStarter implements Bot {
    public Interpreter bsh;

    @Override
    /**
     * A method that returns which region the bot would like to start on, the pickable regions are stored in the BotState.
     * The bots are asked in turn (ABBAABBAAB) where they would like to start and return a single region each time they are asked.
     * This method returns one random region from the given pickable regions.
     */
    public Region getStartingRegion(BotState state, Long timeOut) {

        bsh = new Interpreter();

        //ECJ
        String expression = "";
        try (BufferedReader br = new BufferedReader(new FileReader("myBot\\src\\bot\\sense_world.txt"))) {
            String line = br.readLine();

            while (line != null) {
                expression += line;
                line = br.readLine();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

        HashMap<Region, Double> scoredRegions = new HashMap<>();
        try {
            for (Region region : state.getPickableStartingRegions()) {
                bsh.set("AvgNeighbourScore", getAvgNeighbourScore(region, state));
                bsh.set("SuperRegionScore", getSuperRegionScore(region, state));
                scoredRegions.put(region, (Double) bsh.eval(expression));
            }
        } catch (EvalError err) {
            System.out.println("bsh error");
            err.printStackTrace();
        }

        System.out.println(scoredRegions.toString());
        //ECJ
        return Collections.max(scoredRegions.entrySet(), (entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).getKey();
    }

    public double getSuperRegionScore(Region region, BotState state) {
        return 1.5;
    }

    public double getAvgNeighbourScore(Region region, BotState state) {
        return 0.2;
    }

    @Override
    /**
     * This method is called for at first part of each round. This example puts two armies on random regions
     * until he has no more armies left to place.
     * @return The list of PlaceArmiesMoves for one round
     */
    public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) {

        ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
        String myName = state.getMyPlayerName();
        int armies = 2;
        int armiesLeft = state.getStartingArmies();
        LinkedList<Region> visibleRegions = state.getVisibleMap().getRegions();

        while (armiesLeft > 0) {
            double rand = Math.random();
            int r = (int) (rand * visibleRegions.size());
            Region region = visibleRegions.get(r);

            if (region.ownedByPlayer(myName)) {
                placeArmiesMoves.add(new PlaceArmiesMove(myName, region, armies));
                armiesLeft -= armies;
            }
        }

        return placeArmiesMoves;
    }

    @Override
    /**
     * This method is called for at the second part of each round. This example attacks if a region has
     * more than 6 armies on it, and transfers if it has less than 6 and a neighboring owned region.
     * @return The list of PlaceArmiesMoves for one round
     */
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
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

        return attackTransferMoves;
    }

    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }

}