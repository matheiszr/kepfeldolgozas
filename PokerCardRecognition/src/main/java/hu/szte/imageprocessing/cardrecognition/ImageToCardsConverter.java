package hu.szte.imageprocessing.cardrecognition;

import hu.szte.imageprocessing.cardrecognition.entity.Card;
import hu.szte.imageprocessing.cardrecognition.enums.EnumCardSuit;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * This class make from the input a Card entity list.
 * 
 * @author pataiadam
 *
 */
public class ImageToCardsConverter {
	private static Map<String, ArrayList<MatOfKeyPoint>> learningSet = new HashMap<String, ArrayList<MatOfKeyPoint>>();
	private String learningPath = null;
	private int featureDetectorType = DescriptorExtractor.SIFT;
	private HashMap<Card, Area> polymap;
	private Map<Card, Mat> scene_corner_map;
	

	public ImageToCardsConverter(int featureDetectorType) throws Exception {
		this.featureDetectorType = featureDetectorType;
		this.learningPath = System.getProperty("user.dir")
				+ "\\src\\main\\resources\\pictures\\learning";
		learning(this.learningPath);
	}

	public ImageToCardsConverter(int featureDetectorType, String path)
			throws Exception {
		this.featureDetectorType = featureDetectorType;
		this.learningPath = path;
		learning(this.learningPath);
	}

	public List<Card> getCards(String pathToImage) {
		Mat img = Highgui.imread(pathToImage, Highgui.CV_LOAD_IMAGE_COLOR);
		List<Card> estimated = getEstimatedCards(img);
		
		Mat img2 = new Mat();
		img.copyTo(img2);
		for(Card c : estimated){
			Mat scene_corners = scene_corner_map.get(c);
			Point p1 = new Point(scene_corners.get(0, 0));
			Point p2 = new Point(scene_corners.get(1, 0));
			Point p3 = new Point(scene_corners.get(2, 0));
			Point p4 = new Point(scene_corners.get(3, 0));

			int[] xs = { (int) p1.x, (int) p2.x, (int) p3.x, (int) p4.x };
			int[] ys = { (int) p1.y, (int) p2.y, (int) p3.y, (int) p4.y };
			Area poly = new Area(new Polygon(xs, ys, 4));

			Core.line(img2, p1, p2, new Scalar(0, 255, 0), 4);
			Core.line(img2, p2, p3, new Scalar(0, 255, 0), 4);
			Core.line(img2, p3, p4, new Scalar(0, 255, 0), 4);
			Core.line(img2, p4, p1, new Scalar(0, 255, 0), 4);
			Core.putText(img2, c.toString(), p1, Core.FONT_HERSHEY_SIMPLEX, 1,  new Scalar(255, 0, 0));
			// System.out.println("kiír");
		}
		
		/*for(Mat scene_corners : scene_corner_map.values()){
			Point p1 = new Point(scene_corners.get(0, 0));
			Point p2 = new Point(scene_corners.get(1, 0));
			Point p3 = new Point(scene_corners.get(2, 0));
			Point p4 = new Point(scene_corners.get(3, 0));

			int[] xs = { (int) p1.x, (int) p2.x, (int) p3.x, (int) p4.x };
			int[] ys = { (int) p1.y, (int) p2.y, (int) p3.y, (int) p4.y };
			Area poly = new Area(new Polygon(xs, ys, 4));

			Core.line(img2, p1, p2, new Scalar(255, 0, 0), 1);
			Core.line(img2, p2, p3, new Scalar(255, 0, 0), 1);
			Core.line(img2, p3, p4, new Scalar(255, 0, 0), 1);
			Core.line(img2, p4, p1, new Scalar(255, 0, 0), 1);
			// System.out.println("kiír");
		}*/
			
		new ImageViewer().show(img2);
		return estimated;
	}

	private void learning(String path) throws Exception {
		List<String> ImagePaths = new ImagePathReader()
				.getAllImageFilePathFromADirectory(path, false);
		for (String s : ImagePaths) {
			learnImage(s);
		}
	}

	private ArrayList<MatOfKeyPoint> analyzeImage(Mat objectImage) {
		MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
		FeatureDetector featureDetector = FeatureDetector
				.create(featureDetectorType);
		featureDetector.detect(objectImage, objectKeyPoints);

		MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
		DescriptorExtractor descriptorExtractor = DescriptorExtractor
				.create(featureDetectorType);
		descriptorExtractor.compute(objectImage, objectKeyPoints,
				objectDescriptors);
		ArrayList<MatOfKeyPoint> r = new ArrayList<MatOfKeyPoint>();
		r.add(objectKeyPoints);
		r.add(objectDescriptors);
		return r;
	}

	private void learnImage(String s) {
		// TODO Auto-generated method stub
		Mat objectImage = Highgui.imread(s, Highgui.CV_LOAD_IMAGE_COLOR);
		learningSet.put(s, analyzeImage(objectImage));
	}

	private List<Card> sortHashMap(final HashMap<Card, Integer> map) {
		Set<Card> set = map.keySet();
		List<Card> keys = new ArrayList<Card>(set);

		Collections.sort(keys, new Comparator<Card>() {

			@Override
			public int compare(Card s1, Card s2) {
				return Integer.compare(map.get(s2), map.get(s1));
			}
		});

		return keys;
	}

	private List<Card> getEstimatedCards(Mat img) {
		ArrayList<MatOfKeyPoint> analizedImage = analyzeImage(img);
		MatOfKeyPoint descriptor = analizedImage.get(1);
		HashMap<Card, Integer> map = new HashMap<Card, Integer>();
		polymap = new HashMap<Card, Area>();
		scene_corner_map=new HashMap<Card, Mat>();
		for (Entry<String, ArrayList<MatOfKeyPoint>> e : learningSet.entrySet()) {
			LinkedList<DMatch> goodMatchesList = matcher(e.getKey(), e
					.getValue().get(1), descriptor);

			String[] sp = e.getKey().split("\\\\");
			String card = sp[sp.length - 1].split("\\.")[0];
			String[] cs = card.split("_");
			Card c = new Card(EnumCardSuit.getEnumFromString(cs[0]),
					cs[1].charAt(0));

			if (goodMatchesList.size() >= 4) {
				List<KeyPoint> objKeypointlist = e.getValue().get(0).toList();
				List<KeyPoint> scnKeypointlist = analizedImage.get(0).toList();

				LinkedList<Point> objectPoints = new LinkedList<>();
				LinkedList<Point> scenePoints = new LinkedList<>();

				for (int i = 0; i < goodMatchesList.size(); i++) {
					objectPoints.addLast(objKeypointlist.get(goodMatchesList
							.get(i).queryIdx).pt);
					scenePoints.addLast(scnKeypointlist.get(goodMatchesList
							.get(i).trainIdx).pt);
				}

				MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
				objMatOfPoint2f.fromList(objectPoints);
				MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
				scnMatOfPoint2f.fromList(scenePoints);

				Mat homography = Calib3d.findHomography(objMatOfPoint2f,
						scnMatOfPoint2f, Calib3d.RANSAC, 3);

				Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
				Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

				Mat objectImage = Highgui.imread(e.getKey(),
						Highgui.CV_LOAD_IMAGE_COLOR);

				obj_corners.put(0, 0, new double[] { 0, 0 });
				obj_corners.put(1, 0, new double[] { objectImage.cols(), 0 });
				obj_corners.put(2, 0, new double[] { objectImage.cols(),
						objectImage.rows() });
				obj_corners.put(3, 0, new double[] { 0, objectImage.rows() });

				// System.out
				// .println("Transforming object corners to scene corners...");
				Core.perspectiveTransform(obj_corners, scene_corners,
						homography);
				scene_corner_map.put(c, scene_corners);
				Point p1 = new Point(scene_corners.get(0, 0));
				Point p2 = new Point(scene_corners.get(1, 0));
				Point p3 = new Point(scene_corners.get(2, 0));
				Point p4 = new Point(scene_corners.get(3, 0));

				int[] xs = { (int) p1.x, (int) p2.x, (int) p3.x, (int) p4.x };
				int[] ys = { (int) p1.y, (int) p2.y, (int) p3.y, (int) p4.y };
				Area poly = new Area(new Polygon(xs, ys, 4));
				
				if (poly.isSingular()) {
					polymap.put(c, poly);
					map.put(c, goodMatchesList.size());
					System.out.println(c.toString() + " -> " + goodMatchesList.size());
				} else {
					goodMatchesList = new LinkedList<DMatch>();
				}
			}

			
		}

		boolean k = true;
		while (k) {
			if (map.size() <= 5 || polymap.size() <= 5) {
				k = false;
				break;
			}
			List<Card> list = sortHashMap(map).subList(0, 5);

			for (int i = 0; i < list.size() - 1; i++) {
				Card c1 = list.get(i);
				boolean l=false;
				for(int j = i+1; j < list.size(); j++){
					Card c2 = list.get(j);
					Area intersect = new Area(polymap.get(c1));
					intersect.intersect(polymap.get(c2));
					if (!intersect.isEmpty()) {
						map.remove(c2);
						l=true;
						break;
					}
				}
				if(l){
					break;
				}


				if (i == 3) {
					k = false;
				}
			}
		}

		return sortHashMap(map).subList(0, 5);

	}

	private LinkedList<DMatch> matcher(String card,
			MatOfKeyPoint objectDescriptors, MatOfKeyPoint sceneDescriptors) {
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

		float nndrRatio = 0.6f;

		for (int i = 0; i < matches.size(); i++) {
			MatOfDMatch matofDMatch = matches.get(i);
			DMatch[] dmatcharray = matofDMatch.toArray();
			DMatch m1 = dmatcharray[0];
			DMatch m2 = dmatcharray[1];

			if (m1.distance <= m2.distance * nndrRatio) {
				goodMatchesList.addLast(m1);

			}
		}
		return goodMatchesList;
	}

	public int getFeatureDetectorType() {
		return featureDetectorType;
	}

	public void setFeatureDetectorType(int featureDetectorType) {
		this.featureDetectorType = featureDetectorType;
	}
}