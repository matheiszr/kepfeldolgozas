package hu.szte.imageprocessing.cardrecognition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.szte.imageprocessing.cardrecognition.entity.Card;

/**
 * This class is the main class of the program.
 * This contains the main method which make 
 * methods on input and training Images 
 * and manage the console program.
 * @author Zsarnok
 *
 */
public class Main{
	 
	public static void main(String[] args){
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			// read the test Image
			int inputOption = 0;
			while(inputOption != 1 && inputOption != 2 && inputOption != 3){
				while(inputOption != 1 && inputOption != 2 && inputOption != 3){
					System.out.println("Pleas choose the following options:");
					System.out.println("1 : load a test Image fro");
					System.out.println("2 : count default test Images");
					System.out.println("3 : finish the program (exit)");
					System.out.println();
					inputOption = Integer.parseInt(br.readLine());
				}
				if(inputOption == 1){
					System.out.println("Pleas type the full path to the Image:");
					System.out.println();
					String inputPath = br.readLine();
					runCountOnInputImage(inputPath);
				}
				if(inputOption == 2)
					runDefaultTestImageCounts();	
				if(inputOption == 3)
					return;
				System.out.println();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
		
	private static void runDefaultTestImageCounts() throws Exception{
		List<String> ImagePaths = new ArrayList<String>(); // FIXME feltöltés
		for(String s : ImagePaths){
			readAndStoreOneImage(s);
			whatIsTheHand();
		}
	}	
	
	private static void runCountOnInputImage(String imagePath) throws Exception{
		readAndStoreOneImage(imagePath);
		whatIsTheHand();
	}
	
	private static void readAndStoreOneImage(String pathToImage){
		//FIXME kell a beolvasás és a lementése a képnek
	}
	
	private static void whatIsTheHand() throws Exception{
		ImageToCardsConverter imageToCardsConverter = new ImageToCardsConverter();
		List<Card> hand = imageToCardsConverter.getCardsFromImage();
		
		if(hand == null){
			System.out.println("Can't find 5 cards on the input image.");
			return;
		}
		
		HandCounter handCounter = new HandCounter(hand);
		handCounter.countHand();
		
		System.out.println("Cards from image:");
		StringBuilder sb = new StringBuilder("");
		for(Card c : hand){
			sb.append("|");
			sb.append(c.getSuit());
			sb.append(" ");
			sb.append(c.getValue());
			sb.append("| ");
		}
		System.out.println(sb.toString());
	}
	
	//private keptarolo a beolvasott kepnek //FIXME ez még kell
}