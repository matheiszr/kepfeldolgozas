package hu.szte.imageprocessing.cardrecognition;

//import java.util.ArrayList;
import hu.szte.imageprocessing.cardrecognition.entity.Card;

import java.util.List;

//import hu.szte.imageprocessing.cardrecognition.enums.EnumCardSuit;

/**
 * This class make a count for the hand.
 * 
 * @author Zsarnok
 *
 */
public class HandCounter {

	private List<Card> hand;

	public HandCounter(List<Card> hand) {
		this.hand = hand;
	}

	// public static void main(String[] args){
	// List<Card> hand = new ArrayList<Card>();
	// Card c1 = new Card(EnumCardSuit.HEARTS, 'K');
	// Card c2 = new Card(EnumCardSuit.HEARTS, 'Q');
	// Card c3 = new Card(EnumCardSuit.HEARTS, 'J');
	// Card c4 = new Card(EnumCardSuit.HEARTS, 'A');
	// Card c5 = new Card(EnumCardSuit.HEARTS, 'T');
	// hand.add(c1);
	// hand.add(c2);
	// hand.add(c3);
	// hand.add(c4);
	// hand.add(c5);
	//
	// HandCounter hc = new HandCounter(hand);
	// try {
	// System.out.println(hc.countHand());
	// } catch(Exception e){
	// e.printStackTrace();
	// }
	// }

	/**
	 * This method say what the hand is for 5 card.
	 * 
	 * @return the hands value
	 * @throws Exception
	 */
	public String countHand() throws Exception {
		try {
			int i = 0;
			int cardValues[] = new int[5];
			int pieceOfHearts = 0;
			int piecesOfSpades = 0;
			int piecesOfDiamonds = 0;
			int piecesOfClubs = 0;
			boolean allHasSameSpade = false;
			boolean isStraight = false;

			for (Card card : hand) {
				switch (card.getSuit()) {
				case HEARTS:
					pieceOfHearts++;
					break;
				case SPADES:
					piecesOfSpades++;
					break;
				case DIAMONDS:
					piecesOfDiamonds++;
					break;
				case CLUBS:
					piecesOfClubs++;
					break;
				}
				switch (card.getValue()) {
				case 'T':
					cardValues[i] = 10;
					break;
				case 'J':
					cardValues[i] = 11;
					break;
				case 'Q':
					cardValues[i] = 12;
					break;
				case 'K':
					cardValues[i] = 13;
					break;
				case 'A':
					int valueOfA = 14;
					for (Card c : hand) {
						if (c.getValue() == '2') {
							for (Card cTwo : hand) {
								if (cTwo.getValue() == 'Q') { // flush
																// conjecture
									valueOfA = 14;
								}
							}
							valueOfA = 1;
						}
					}
					cardValues[i] = valueOfA;
					break;
				default: // if its a number
					cardValues[i] = Character.getNumericValue(card.getValue());
					break;
				}
				i++;
			}
			// order the block:
			cardValues = orderBlock(cardValues);

			// check all spades is equals?
			if (pieceOfHearts == 5 || piecesOfSpades == 5
					|| piecesOfDiamonds == 5 || piecesOfClubs == 5) {
				allHasSameSpade = true;
			}
			// check the 5 card is a straight?
			int howManyIsGood = 0; // if a value is equals the next one + 1 add
									// +1 to this int
			for (int j = 0; j < cardValues.length; j++) {
				if ((j < cardValues.length - 1)
						&& (cardValues[j] == cardValues[j + 1] - 1)) {
					howManyIsGood++;
				}
			}
			if (howManyIsGood == 4) {
				isStraight = true;
			}
			// results:
			if (isStraight && allHasSameSpade && cardValues[4] == 14)
				return "Royal Flush";
			if (isStraight && allHasSameSpade)
				return "Straight flush";
			// because its an ordered block and the two option is: xyyyy or
			// xxxxy
			if (cardValues[0] == cardValues[3]
					|| cardValues[2] == cardValues[4])
				return "Four of a kind";
			// because its an ordered lblockist and the two option is: xxyyy or
			// xxxyy
			if ((cardValues[0] == cardValues[2] && cardValues[3] == cardValues[4])
					|| (cardValues[0] == cardValues[1] && cardValues[2] == cardValues[4]))
				return "Full house";
			if (allHasSameSpade && !isStraight)
				return "Flush";
			if (isStraight && !allHasSameSpade)
				return "Straight";
			// because its an ordered block and the three options is: xxxyz or
			// xyyyz or xyzzz
			if ((cardValues[0] == cardValues[2])
					|| (cardValues[1] == cardValues[3])
					|| (cardValues[2] == cardValues[4]))
				return "Three of a kind";
			int numOfPairs = getNumOfPairs(cardValues);
			if (numOfPairs == 2)
				return "Two pair";
			if (numOfPairs == 1)
				return "One pair";

		} catch (Exception e) {
			throw new Exception(e);
		}
		return "High card";
	}

	/**
	 * This method search pairs in an int[].
	 * 
	 * @param block
	 *            (int[])
	 * @return num of pairs (int)
	 */
	private int getNumOfPairs(int[] block) {
		int numOfPairs = 0;
		for (int i = 0; i < block.length; i++) {
			for (int j = i + 1; j < block.length; j++) {
				if (block[i] == block[j])
					numOfPairs++;
			}
		}
		return numOfPairs;
	}

	/**
	 * This method make an ascending order for an int[].
	 * 
	 * @param block
	 *            (int[])
	 * @return the ordered block.
	 */
	private int[] orderBlock(int[] block) {
		boolean isOrdered = false;
		while (!isOrdered) {
			isOrdered = true;
			for (int j = 1; j < block.length; j++) {
				if (block[j - 1] > block[j]) {
					int v = block[j - 1];
					block[j - 1] = block[j];
					block[j] = v;
					isOrdered = false;
				}
			}
		}
		return block;
	}
}