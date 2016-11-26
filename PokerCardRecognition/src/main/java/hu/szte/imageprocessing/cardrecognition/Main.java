package hu.szte.imageprocessing.cardrecognition;

import hu.szte.imageprocessing.cardrecognition.entity.Card;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
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
		for (String s : ImagePaths) {
			readAndStoreOneImage(s);
			whatIsTheHand();
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

	private static void learnImage(String s) {
		// TODO Auto-generated method stub
		Mat objectImage = Highgui.imread(s, Highgui.CV_LOAD_IMAGE_COLOR);
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
        System.out.println("Detecting key points...");
        featureDetector.detect(objectImage, objectKeyPoints);
        KeyPoint[] keypoints = objectKeyPoints.toArray();
        System.out.println(keypoints);

        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
        System.out.println("Computing descriptors...");
        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

	}

	private static void runCountOnInputImage(String imagePath) throws Exception {
		readAndStoreOneImage(imagePath);
		whatIsTheHand();
	}

	private static void readAndStoreOneImage(String pathToImage) {
		// FIXME kell a beolvasás és a lementése a képnek
		Highgui.imread(pathToImage);
	}

	private static void whatIsTheHand() throws Exception {
		ImageToCardsConverter imageToCardsConverter = new ImageToCardsConverter();
		List<Card> hand = imageToCardsConverter.getCardsFromImage();

		if (hand == null) {
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