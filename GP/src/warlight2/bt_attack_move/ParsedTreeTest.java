package warlight2.bt_attack_move;

import bot.BotState;
import bt.BehaviourTree;
import move.BehaviourTreeParser;

/**
 * Created by Hallvard on 02.10.2015.
 */
public class ParsedTreeTest {

    public static void main(String[] args) {
        String expression = "selector[failer(inverter(sequence[selector[sequence[sequence[maxAttack, onlyFriendlyNeighbors, onlyFriendlyNeighbors], failer(randomAttack), untilFail(onlyFriendlyNeighbors)], failer(failer(randomAttack)), untilFail(parallel[isFriendly, willCompleteSuper])], untilSucceed(selector[failer(focalStronger), inverter(isNeutral), selector[focalPartOfCompleteSuper, focalStronger, isFriendly]]), selector[failer(sequence[randomAttack, focalPartOfCompleteSuper, focalStronger]), untilFail(parallel[randomAttack, isNeutral]), sequence[failer(minSuperiorAttack), sequence[onlyFriendlyNeighbors, isNeutral, randomAttack], failer(isFriendly)]]])), untilSucceed(untilSucceed(succeeder(inverter(untilFail(inverter(randomSuperiorAttack)))))), failer(untilFail(succeeder(parallel[inverter(inverter(isFriendly)), untilSucceed(failer(randomSuperiorAttack))])))]";
        System.out.println(expression);
        System.out.println("------------------------");

        BehaviourTreeParser parser = new BehaviourTreeParser();
        BehaviourTree<BotState> bt = null;
        try {
            bt = BehaviourTreeParser.generate(expression, new BotState());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        System.out.println("------------------------");
        System.out.println(null == bt ? "null" : bt.toString());
    }
}
