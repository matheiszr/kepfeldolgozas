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
	private static Map<String, MatOfKeyPoint> learningSet = new HashMap<String, MatOfKeyPoint>();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			// read the test Image
			int inputOption = 0;
			learning();
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

	private static void runDefaultTestImageCounts() throws Exception {
		List<String> ImagePaths = new ArrayList<String>();
		ImagePaths.add(System.getProperty("user.dir")
				+ "\\src\\main\\resources\\test\\test_12.jpg");
		for (String s : ImagePaths) {
			whatIsTheHand(getEstimatedCards(s));
		}
	}

	private static void learning() throws Exception {
		List<String> ImagePaths = new ImagePathReader()
				.getAllImageFilePathFromADirectory(
						System.getProperty("user.dir")
								+ "\\src\\main\\resources\\pictures\\learning",
						false);
		for (String s : ImagePaths) {
			learnImage(s);
		}
	}

	private static MatOfKeyPoint analyzeImage(Mat objectImage) {
		MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
		FeatureDetector featureDetector = FeatureDetector
				.create(FeatureDetector.SIFT);
		// System.out.println("Detecting key points...");
		featureDetector.detect(objectImage, objectKeyPoints);

		MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
		DescriptorExtractor descriptorExtractor = DescriptorExtractor
				.create(DescriptorExtractor.SIFT);
		// System.out.println("Computing descriptors...");
		descriptorExtractor.compute(objectImage, objectKeyPoints,
				objectDescriptors);
		return objectDescriptors;
	}

	private static void learnImage(String s) {
		// TODO Auto-generated method stub
		Mat objectImage = Highgui.imread(s, Highgui.CV_LOAD_IMAGE_COLOR);
		String[] sp = s.split("\\\\");
		String card = sp[sp.length - 1].split("\\.")[0];
		learningSet.put(card, analyzeImage(objectImage));
	}

	private static void runCountOnInputImage(String imagePath) throws Exception {
		//whatIsTheHand();
	}
	
	public static List<Card> sortHashMap(final HashMap<Card, Integer> map) {
	    Set<Card> set = map.keySet();
	    List<Card> keys = new ArrayList<Card>(set);

	    Collections.sort(keys, new Comparator<Card>() {

	        @Override
	        public int compare(Card s1, Card s2) {
	            return Integer.compare(map.get(s2), map.get(s1)); //reverse order
	        }
	    });

	    return keys;
	}

	private static List<Card> getEstimatedCards(String pathToImage) {
		// FIXME kell a beolvasás és a lementése a képnek
		System.out.println(pathToImage);
		Mat img = Highgui.imread(pathToImage, Highgui.CV_LOAD_IMAGE_COLOR);
		MatOfKeyPoint descriptor = analyzeImage(img);
		HashMap<Card, Integer> map = new HashMap<Card, Integer>();
		for (Entry<String, MatOfKeyPoint> e : learningSet.entrySet()) {
			int value = matcher(e.getKey(), e.getValue(), descriptor);
			String[] cs = e.getKey().split("_");
			Card c = new Card(EnumCardSuit.getEnumFromString(cs[0]), cs[1].charAt(0));
			map.put(c, value);
		}

		return sortHashMap(map).subList(0, 5);
		
	}

	private static int matcher(String card, MatOfKeyPoint objectDescriptors,
			MatOfKeyPoint sceneDescriptors) {
		List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
		if (objectDescriptors.type() != CvType.CV_32F) {
			objectDescriptors.convertTo(objectDescriptors, CvType.CV_32F);
		}
		if (sceneDescriptors.type() != CvType.CV_32F) {
			objectDescriptors.convertTo(sceneDescriptors, CvType.CV_32F);
		}
		DescriptorMatcher descriptorMatcher = DescriptorMatcher
				.create(DescriptorMatcher.FLANNBASED);
		// System.out.println("Matching object and scene images...");
		descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors,
				matches, 10);

		// System.out.println("Calculating good match list...");
		LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

		float nndrRatio = 0.55f;

		for (int i = 0; i < matches.size(); i++) {
			MatOfDMatch matofDMatch = matches.get(i);
			DMatch[] dmatcharray = matofDMatch.toArray();
			DMatch m1 = dmatcharray[0];
			DMatch m2 = dmatcharray[1];

			if (m1.distance <= m2.distance * nndrRatio) {
				goodMatchesList.addLast(m1);

			}
		}

		
		if (goodMatchesList.size() >= 7) {
			//System.out.println(card + " : " + goodMatchesList.size());
		}
		
		return goodMatchesList.size();

	}

	private static void whatIsTheHand(List<Card> hand) throws Exception {
		if (hand == null || hand.size()<5) {
			System.out.println("Can't find 5 cards on the input image.");
			return;
		}

		HandCounter handCounter = new HandCounter(hand);
		handCounter.countHand();

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