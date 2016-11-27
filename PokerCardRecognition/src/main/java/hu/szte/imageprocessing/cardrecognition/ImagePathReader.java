package hu.szte.imageprocessing.cardrecognition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class contains file path reader methods. Which can get all files path
 * from a library. U can use this in two ways: read files from a directori which
 * is inside in the jar or outside files from your system.
 * 
 * @author zsarnok
 */
public class ImagePathReader {

	/**
	 * This method read all file location in the path and store these in a list.
	 * 
	 * @param directoryPath
	 *            (a path to a directory which contains script files)
	 * @param isDefault
	 *            (use the default settings or not)
	 * @return cqlFiles (path of script files)
	 */
	public String getOneImagePath(String file, boolean isDefault)
			throws Exception {
		String result = "";
		try {
			BufferedReader br;
			System.out.println("run: " + file);
			if (isDefault) { // default way get the script file from the jar
				br = new BufferedReader(new InputStreamReader(getClass()
						.getResourceAsStream("/" + file)));
			} else { // own setting way
				br = new BufferedReader(new FileReader(file));
			}
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			result = sb.toString();
		} catch (Exception e) {
			throw new Exception(e);
		}
		return result;
	}

	/**
	 * This method read all file location in the path and store these in a list.
	 * 
	 * @param directoryPath
	 *            (a path to a directory which contains script files)
	 * @param isDefault
	 *            (use the default settings or not)
	 * @return cqlFiles (path of script files)
	 */
	public List<String> getAllImageFilePathFromADirectory(String directoryPath,
			boolean isDefault) {
		System.out.println("Start files reading...");
		List<String> scriptFiles = new ArrayList<String>();
		try {
			if (isDefault) {
				File jarFile = new File(ImagePathReader.class
						.getProtectionDomain().getCodeSource().getLocation()
						.getPath());
				if (jarFile.isFile()) { // Run with JAR file
					final JarFile jar = new JarFile(jarFile);
					final Enumeration<JarEntry> entries = jar.entries(); // gives
																			// ALL
																			// entries
																			// in
																			// jar
					while (entries.hasMoreElements()) {
						final String name = entries.nextElement().getName();
						StringBuilder sb = new StringBuilder("");
						sb.append(directoryPath);
						sb.append("\\");
						if (name.startsWith(sb.toString())) { // filter
																// according to
																// the path
							scriptFiles.add(name);
						}
					}
					jar.close();
				}
			} else {
				System.out.println(directoryPath);
				File file = new File(directoryPath);
				String files[] = file.list();
				for (String f : files) {
					StringBuilder sb = new StringBuilder("");
					sb.append(directoryPath);
					sb.append("\\");
					sb.append(f);
					scriptFiles.add(sb.toString());
				}
			}
		} catch (Exception e) {
			System.err
					.println("An error was occurred during the image reading.");
			e.printStackTrace();
		}
		System.out.println("Image readings was finished successfully.");
		return scriptFiles;
	}
}