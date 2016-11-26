package hu.szte.imageprocessing.cardrecognition;

import hu.szte.imageprocessing.cardrecognition.entity.Card;
import hu.szte.imageprocessing.cardrecognition.enums.EnumCardSuit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;

/**
 * This class is the main class of the program. This contains the main method
 * which make methods on input and training Images and manage the console
 * program.
 * 
 * @author Zsarnok
 *
 */
public class Main {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			// read the test Image
			int inputOption = 0;
			while (inputOption <= 0 || inputOption >= 4) {
				while (inputOption <= 0 || inputOption >= 4) {
					System.out.println("Pleas choose the following options:");
					System.out.println("1 : load a test Image fro");
					System.out.println("2 : count default test Images");
					System.out.println("3 : finish the program (exit)");
					System.out.println();
					inputOption = Integer.parseInt(br.readLine());
				}
				if (inputOption == 1) {
					System.out
							.println("Pleas type the full path to the Image:");
					System.out.println();
					String inputPath = br.readLine();
					runCountOnInputImage(inputPath);
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

	private static void runCountOnInputImage(String imagePath) throws Exception {
		//whatIsTheHand();
	}
	
	private static void runDefaultTestImageCounts() throws Exception {
		String defaultPath= System.getProperty("user.dir")
				+ "\\src\\main\\resources\\test\\test_8.jpg";
		List<Card> cards = new ImageToCardsConverter().getCards(defaultPath);
		whatIsTheHand(cards);
	}

	private static void whatIsTheHand(List<Card> hand) throws Exception {
		if (hand == null || hand.size()<5) {
			System.out.println("Can't find 5 cards on the input image.");
			return;
		}

		HandCounter handCounter = new HandCounter(hand);

		System.out.println("Estimated hand: "+handCounter.countHand());

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
	}

	// private keptarolo a beolvasott kepnek //FIXME ez még kell
}