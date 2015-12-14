package warlight2.bt_test;

import bot.BotState;
import bt.BehaviourTree;
import move.BehaviourTreeParser;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by Hallvard on 13.10.2015.
 */
public class BtRunTest {

    private static final Logger LOGGER = Logger.getLogger(BtRunTest.class.getName());
    static {
        try {
            Handler handler = new FileHandler("C:\\work\\Master\\WarlightECJ\\botlogTestrrrttt.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
        } catch (IOException e) {e.printStackTrace();}
    }

    public static void main(String[] args) {
        String expression = "selector[inverter(selector[isFriendly,inverter(focalStronger),randomSuperiorAttack]),inverter(selector[inverter(isFriendly),focalStronger,maxAttack]), succeeder(isFriendly)]";
        BotState state = new BotState();
        BehaviourTree<BotState> tree = BehaviourTreeParser.generate(expression, state);
//        tree.step();
        LOGGER.log(Level.INFO, "HOLY DUCJING HEARING AIDS");
    }
}
