package move;

import bot.BotState;
import bt.BehaviourTree;
import bt.Task;
import bt.composite.Parallel;
import bt.composite.Selector;
import bt.composite.Sequence;
import bt.decorator.*;
import move.actions.*;
import move.conditions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hallvard on 30.09.2015.
 */
public class BehaviourTreeParser {

    private static final HashMap<String, Class<? extends Task>> DEFAULT_IMPORTS = new HashMap();
    static {
        Class<? extends Task>[] classes = new Class[]{
                //Default BT
                Parallel.class,
                Selector.class,
                Sequence.class,
                Failer.class,
                Inverter.class,
                Succeeder.class,
                UntilFail.class,
                UntilSucceed.class,
                //Actions and Conditions
                MaxAttack.class,
                MinSuperiorAttack.class,
                RandomAttack.class,
                RandomSuperiorAttack.class,
                FocalPartOfCompleteSuper.class,
                FocalStronger.class,
                IsEnemy.class,
                IsFriendly.class,
                IsNeutral.class,
                OnlyFriendlyNeighbors.class,
                WillCompleteSuper.class,
                PartOfSameSuper.class
        };
        for (Class<? extends Task> c : classes) {
//            String fqcn = c.getName();
            String cn = c.getSimpleName();
            String alias = Character.toLowerCase(cn.charAt(0))
                    + (cn.length() > 1 ? cn.substring(1) : "");
            DEFAULT_IMPORTS.put(alias, c);
        }
    }

    HashMap<String, String> userImports = new HashMap<String, String>();

    private <T> List<T> toList(Collections c) {
        return null;
    }

    public static BehaviourTree<BotState> generate(String exp, BotState state) {
        BehaviourTree<BotState> tree = new BehaviourTree<BotState>();
        tree.setBlackboard(state);

        String builder = "";
        Task<BotState> current = tree;
        for(char c: exp.replace(" ", "").toCharArray()) {
            if(Character.isAlphabetic(c)) {
                builder = builder+c;
                continue;
            }
            Task<BotState> next = null;
            boolean hasNext = false;
            if(!builder.equals("")) {
                next = getTask(builder);
                hasNext = true;
            }
            if(c == ']' || c == ')') {
                if(hasNext)
                    current.addChild(next);
                current = current.getParent();
            } else if(c == '[') {
                current.addChild(next);
                next.setParent(current);
                current = next;
            } else if(c == '(') {
                current.addChild(next);
                next.setParent(current);
                current = next;
            } else if(c == ',') {
                current.addChild(next);
            }
            builder = "";
        }
        return tree;
    }

    private static Task<BotState> getTask(String name) {
        Class<? extends Task> clazz = DEFAULT_IMPORTS.get(name);
        System.out.println(name);
        System.out.println(null==clazz ? "null" : clazz.getSimpleName());
        System.out.println("---");
        Task obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }


}
