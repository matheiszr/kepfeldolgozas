package hu.szte.imageprocessing.cardrecognition;

import hu.szte.imageprocessing.cardrecognition.entity.Card;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.features2d.DescriptorExtractor;

/**
 * This class is the main class of the program. This contains the main method
 * which make methods on input and training Images and manage the console
 * program.
 * 
 * @author zsarnok, pataiadam
 *
 */
public class Main {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	private static ImageToCardsConverter converter;

	public static void main(String[] args) {
		try {
			converter = new ImageToCardsConverter(DescriptorExtractor.SIFT);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			// read the test Image
			int inputOption = 0;
			while (inputOption <= 0 || inputOption >= 4) {
				while (inputOption <= 0 || inputOption >= 4) {
					System.out.println("Pleas choose the following options:");
					System.out.println("1 : load a test Image from");
					System.out.println("2 : count default test Images");
					System.out.println("3 : finish the program (exit)");
					System.out.println();
					inputOption = Integer.parseInt(br.readLine());
				}
				if (inputOption == 1) {
					//FIXME elesben ezeket vissza tenni!!!!!
					 System.out.println("Pleas type the full path to the Image:");
					 System.out.println();
					 String inputPath = br.readLine();
					 runCountOnInputImage(inputPath); 
				
//					// FIXME elesben ezt kommentezd ki!!!!!
//					String imagePath = System.getProperty("user.dir")
//							+ "\\src\\main\\resources\\pictures\\test\\test_5.jpg";
//					runCountOnInputImage(imagePath);
				}
				if (inputOption == 2)
					runDefaultTestImageCounts();
				if (inputOption == 3)
					return;
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method run for only one image. You can add own image to the program.
	 * 
	 * @param imagePath
	 *            (your image which would you like count)
	 * @throws Exception
	 */
	private static void runCountOnInputImage(String imagePath) throws Exception {
		List<Card> cards = converter.getCards(imagePath);
		whatIsTheHand(cards);
	}

	/**
	 * This method run all default test pictures from resurces/test
	 * 
	 * @throws Exception
	 */
	private static void runDefaultTestImageCounts() throws Exception {
		String defaultTestPictures = System.getProperty("user.dir")
				+ "\\src\\main\\resources\\pictures\\test";
		List<String> ImagePaths = new ImagePathReader()
				.getAllImageFilePathFromADirectory(defaultTestPictures, false);
		for (String path : ImagePaths) {
			runCountOnInputImage(path);
		}
	}

	/**
	 * This method print to the terminal what is the hand and print the
	 * recognized cards.
	 * 
	 * @param hand
	 *            (List of Card entities which contains 5 cards)
	 * @throws Exception
	 */
	private static void whatIsTheHand(List<Card> hand) throws Exception {
		if (hand == null || hand.size() < 5) {
			System.out.println("Can't find 5 cards on the input image.");
			return;
		}

		HandCounter handCounter = new HandCounter(hand);
		System.out.println();
		System.out.println("#####################################");
		System.out.println("Estimated hand: " + handCounter.countHand());

		System.out.println("Cards from image:");
		StringBuilder sb = new StringBuilder("");
		for (Card c : hand) {
			sb.append("|");
			sb.append(c.getSuit());
			sb.append(" ");
			sb.append(c.getValue());
			sb.append("| ");
		}
		System.out.println(sb.toString());
		System.out.println("#####################################");
	}
}