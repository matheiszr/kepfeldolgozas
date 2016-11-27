package hu.szte.imageprocessing.cardrecognition;

import hu.szte.imageprocessing.cardrecognition.entity.Card;
import hu.szte.imageprocessing.cardrecognition.enums.EnumCardSuit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
 * This class make from the input a Card entity list.
 * 
 * @author pataiadam
 *
 */
public class ImageToCardsConverter {
	private static Map<String, MatOfKeyPoint> learningSet = new HashMap<String, MatOfKeyPoint>();
	private String learningPath = null;
	private int featureDetectorType = DescriptorExtractor.SIFT;

	public ImageToCardsConverter(int featureDetectorType) throws Exception {
		this.featureDetectorType=featureDetectorType;
		this.learningPath = System.getProperty("user.dir")
				+ "\\src\\main\\resources\\pictures\\learning";
		learning(this.learningPath);
	}

	public ImageToCardsConverter(int featureDetectorType, String path) throws Exception {
		this.featureDetectorType=featureDetectorType;
		this.learningPath = path;
		learning(this.learningPath);
	}

	public List<Card> getCards(String pathToImage) {
		Mat img = Highgui.imread(pathToImage, Highgui.CV_LOAD_IMAGE_COLOR);
		return getEstimatedCards(img);
	}

	private void learning(String path) throws Exception {
		List<String> ImagePaths = new ImagePathReader()
				.getAllImageFilePathFromADirectory(path, false);
		for (String s : ImagePaths) {
			learnImage(s);
		}
	}

	private MatOfKeyPoint analyzeImage(Mat objectImage) {
		MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
		FeatureDetector featureDetector = FeatureDetector
				.create(featureDetectorType);
		featureDetector.detect(objectImage, objectKeyPoints);

		MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
		DescriptorExtractor descriptorExtractor = DescriptorExtractor
				.create(featureDetectorType);
		descriptorExtractor.compute(objectImage, objectKeyPoints,
				objectDescriptors);
		return objectDescriptors;
	}

	private void learnImage(String s) {
		// TODO Auto-generated method stub
		Mat objectImage = Highgui.imread(s, Highgui.CV_LOAD_IMAGE_COLOR);
		String[] sp = s.split("\\\\");
		String card = sp[sp.length - 1].split("\\.")[0];
		learningSet.put(card, analyzeImage(objectImage));
	}

	private List<Card> sortHashMap(final HashMap<Card, Integer> map) {
		Set<Card> set = map.keySet();
		List<Card> keys = new ArrayList<Card>(set);

		Collections.sort(keys, new Comparator<Card>() {

			@Override
			public int compare(Card s1, Card s2) {
				return Integer.compare(map.get(s2), map.get(s1)); // reverse
																	// order
			}
		});

		return keys;
	}

	private List<Card> getEstimatedCards(Mat img) {
		MatOfKeyPoint descriptor = analyzeImage(img);
		HashMap<Card, Integer> map = new HashMap<Card, Integer>();
		for (Entry<String, MatOfKeyPoint> e : learningSet.entrySet()) {
			int value = matcher(e.getKey(), e.getValue(), descriptor);
			String[] cs = e.getKey().split("_");
			Card c = new Card(EnumCardSuit.getEnumFromString(cs[0]),
					cs[1].charAt(0));
			map.put(c, value);
		}

		return sortHashMap(map).subList(0, 5);

	}

	private int matcher(String card, MatOfKeyPoint objectDescriptors,
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
			// System.out.println(card + " : " + goodMatchesList.size());
		}

		return goodMatchesList.size();
	}
	
	public int getFeatureDetectorType() {
		return featureDetectorType;
	}

	public void setFeatureDetectorType(int featureDetectorType) {
		this.featureDetectorType = featureDetectorType;
	}
}