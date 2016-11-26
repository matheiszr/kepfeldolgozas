package hu.szte.imageprocessing.cardrecognition.enums;

public enum EnumCardSuit{
	HEARTS,
	SPADES,
	DIAMONDS,
	CLUBS;
		
	public static EnumCardSuit getEnumFromString(String cardString){
		cardString = cardString.toUpperCase();
		switch(cardString){
			case "HEARTH":
				return EnumCardSuit.HEARTS;
			case "SPADES":
				return EnumCardSuit.SPADES;
			case "DIAMONDS":
				return EnumCardSuit.DIAMONDS;
			case "CLUBS":
				return EnumCardSuit.CLUBS;
			default:
				throw new IllegalArgumentException("Not a valid enum name: " + cardString);
		}
	}
}