package hu.szte.imageprocessing.cardrecognition.entity;

import hu.szte.imageprocessing.cardrecognition.enums.EnumCardSuit;

/**
 * This entity represent a card for the algorithms 
 * @author Zsarnok
 *
 */
public class Card{
	// jön még ide kép
	private char value; // value of the card f.e.: 1,2 ... K, A ...
	private EnumCardSuit suit; // f.e.: Diamonds, Heart ....
	// esetleg tárolhatna card feature-t is az erre a lapra jellemzõ adatokkal
	
	public Card(){}
	
	public Card(EnumCardSuit suit, char value){
		this.value = value;
		this.suit = suit;
	}
	
	public char getValue() {
		return value;
	}
	public void setValue(char value) {
		this.value = value;
	}
	public EnumCardSuit getSuit() {
		return suit;
	}
	public void setSuit(EnumCardSuit suit) {
		this.suit = suit;
	}
	
	public String toString(){
		return this.suit.toString()+" "+this.value;
	}
}