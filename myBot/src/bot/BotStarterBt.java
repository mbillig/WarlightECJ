package bot;

import bt.BehaviourTree;
import bt.decorator.UntilFail;
import map.Region;
import move.AttackTransferMove;
import move.BehaviourTreeParser;

import java.io.*;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.logging.*;

/**
 * Created by Hallvard on 01.10.2015.
 */
public class BotStarterBt extends BotStarter {

    public static final Logger LOGGER = Logger.getLogger(BotStarterBt.class.getName());
    static {
        try {
            Handler handler = new FileHandler("C:\\work\\Master\\WarlightECJ\\botLogTest.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
        } catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) {
        LOGGER.log(Level.INFO, "0");
        LOGGER.log(Level.INFO, expression.get());
        LOGGER.log(Level.INFO, "0.1");
        AttackTransferMove m = new AttackTransferMove("", null, null, 0);
        LOGGER.log(Level.INFO, "0.3");
        UntilFail f = new UntilFail();
        LOGGER.log(Level.INFO, "0.5");
        BehaviourTree<BotState> stateBehaviourTree = new BehaviourTree<BotState>();
        LOGGER.log(Level.INFO, "0.6");


        BehaviourTree<BotState> tree = BehaviourTreeParser.generate(expression.get(), state);
        LOGGER.log(Level.INFO, "1");

        state.resetAttMoves();

        List<Region> visible= state.getVisibleMap().getRegions();
        List<Region> friendly = new ArrayList<Region>();
        LOGGER.log(Level.INFO, "2");
        for(Region r : visible) {
            if(r.getPlayerName().equals(state.getMyPlayerName())) {
                friendly.add(r);
            }
        }
        LOGGER.log(Level.INFO, "3");
        outer:
        for(Region r : friendly) {
            state.setFocal(r);
            if(state.getAvailableArmies(r) < 2)
                continue outer;
            for(Region n : r.getNeighbors()) {
                state.setNonFocal(n);
                tree.step();
            }
        }
        LOGGER.log(Level.SEVERE, String.format("Round %d \n", state.getRoundNumber()));
        for(AttackTransferMove atm : state.getAttMoves()) {
            LOGGER.log(Level.INFO, atm.getString()+"\n");
        }
        return new ArrayList<AttackTransferMove>(state.getAttMoves());
    }

    public static void main(String[] args) {

        expression = Optional.of(args[0]);
//        expression = Optional.of("selector[inverter(selector[isFriendly,inverter(focalStronger),randomSuperiorAttack]),inverter(selector[inverter(isFriendly),focalStronger,maxAttack]), succeeder(isFriendly)]");
        BotParser parser = new BotParser(new BotStarterBt());
        parser.run();
    }
/*
    private void print(String output) {
        PrintWriter printer;
        try {
            printer = new PrintWriter(new FileWriter("C:\\work\\Master\\WarlightECJ\\botlog.txt", true));
            printer.write(output);
            printer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
}
