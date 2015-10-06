/**
 * www.TheAIGames.com 
 * Heads Up Omaha pokerbot
 *
 * Last update: May 07, 2014
 *
 * @author Jim van Eeden, Starapple
 * @version 1.0
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */


package bot;

import poker.Card;
import poker.HandHoldem;
import poker.PokerMove;

import stevebrecher.HandEval;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class is the brains of your bot. Make your calculations here and return the best move with GetMove
 */
public class BotStarter implements Bot {

	public static String argExpression = "";

	ScriptEngineManager engineManager =
			new ScriptEngineManager();
	ScriptEngine engine =
			engineManager.getEngineByName("nashorn");

	/**
	 * Implement this method to return the best move you can. Currently it will return a raise the ordinal value
	 * of one of our cards is higher than 9, a call when one of the cards has a higher ordinal value than 5 and
	 * a check otherwise.
	 * @param state : The current state of your bot, with all the (parsed) information given by the engine
	 * @param timeOut : The time you have to return a move
	 * @return PokerMove : The move you will be doing
	 */
	@Override
	public PokerMove getMove(BotState state, Long timeOut) {
		HandHoldem hand = state.getHand();
		int handValue = getHandCategory(hand, state.getTable());

		String expression = argExpression;

		// Get the ordinal values of the cards in your hand
		double height1 = hand.getCard(0).getHeight().ordinal();
        double height2 = hand.getCard(1).getHeight().ordinal();
		try {
			engine.eval("function tree(height, mystack, opponentstack, betcost, potsize, handeval) { return " + expression + "}");
			height1 = (double) engine.eval("tree("
                    + height1 + ","
                    + state.getmyStack() + ","
                    + state.getOpponentStack() + ","
                    + state.getAmountToCall() + ","
                    + state.getPot() + ","
                    + handValue+ ");");
			height2 = (double) engine.eval("tree(" + height2 + ");");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		// Return the appropriate move according to our amazing strategy
		if( height1 > 9 || height2 > 9 ) {
			return new PokerMove(state.getMyName(), "raise", 2*state.getBigBlind());
		} else if( height1 > 5 && height2 > 5 ) {
			return new PokerMove(state.getMyName(), "call", state.getAmountToCall());
		} else {
			return new PokerMove(state.getMyName(), "check", 0);
		}
	}
	
	/**
	 * Calculates the bot's hand strength, with 0, 3, 4 or 5 cards on the table.
	 * This uses the com.stevebrecher package to get hand strength.
	 * @param hand : cards in hand
	 * @param table : cards on table
	 * @return HandCategory with what the bot has got, given the table and hand
	 */
	public int getHandCategory(HandHoldem hand, Card[] table) {
		if( table == null || table.length == 0 ) { // there are no cards on the table
			return hand.getCard(0).getHeight() == hand.getCard(1).getHeight() // return a pair if our hand cards are the same
					? 1
					: 0;
		}

		long handCode = hand.getCard(0).getNumber() + hand.getCard(1).getNumber();
		
		for( Card card : table ) { handCode += card.getNumber(); }
		
		if( table.length == 3 ) { // three cards on the table
			return HandEval.hand5Eval(handCode);
		}
		if( table.length == 4 ) { // four cards on the table
			return HandEval.hand6Eval(handCode);
		}
		return HandEval.hand7Eval(handCode); // five cards on the table
	}
	
	/**
	 * small method to convert the int 'rank' to a readable enum called HandCategory
	 */
	public HandEval.HandCategory rankToCategory(int rank) {
		return HandEval.HandCategory.values()[rank >> HandEval.VALUE_SHIFT];
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		argExpression = args[0];

		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}

}
