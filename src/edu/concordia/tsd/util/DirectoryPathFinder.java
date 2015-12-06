package edu.concordia.tsd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectoryPathFinder {
	
	// Need to write generate the XML file with the corresponding paths 

	private final String PATH = "/Users/hitman4r44/Documents/Google_chrome_code/src/base";
	static List<String> directoryPathList = new ArrayList<String>();

	public void walk(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath());
				directoryPathList.add(f.getAbsoluteFile().toString().replace(PATH+"/", ""));
			}
		}
	}
	
	private static String[] getAllDirectoriesName(File file) {

		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		System.out.println(Arrays.toString(directories));

		return directories;
	}

	public static void main(String[] args) throws FileNotFoundException {

		DirectoryPathFinder pathFinder = new DirectoryPathFinder();
		pathFinder.walk(pathFinder.PATH);
		
		printTheList(pathFinder.directoryPathList);
		
		String[] allParentDirectory = getAllDirectoriesName(new File (pathFinder.PATH));
		
		pathFinder.generateTheMiddleXMLfile(allParentDirectory);
		
		//Merge all files into one file
		String sourceFile1Path = "resources/XMLFirstParagraph.txt";
		String sourceFile2Path = "resources/XMLMiddleParagraph.txt";
		String sourceFile3Path = "resources/XMLLastParagraph.txt";
		String mergedFilePath = "resources/.cproject";

		File[] files = new File[3];
		
		files[0] = new File(sourceFile1Path);
		files[1] = new File(sourceFile2Path);
		files[2] = new File(sourceFile3Path);

		File mergedFile = new File(mergedFilePath);

		MergerFiles.mergeFiles(files, mergedFile);		
		
	}

	private void generateTheMiddleXMLfile(String[] allParentDirectory) throws FileNotFoundException {
		String sourceEntitiesText = "<sourceEntries>\n";
		sourceEntitiesText = sourceEntitiesText + "<entry excluding=" + '"';

		for (int i = 0; i < allParentDirectory.length; i++) {

			if (i == 0) {
				sourceEntitiesText = sourceEntitiesText + allParentDirectory[i];
			} else {
				sourceEntitiesText = sourceEntitiesText + "|";
				sourceEntitiesText = sourceEntitiesText + allParentDirectory[i];
			}
		}

		sourceEntitiesText = sourceEntitiesText + '"';
		sourceEntitiesText = sourceEntitiesText + "flags=" + '"' + "VALUE_WORKSPACE_PATH|RESOLVED" + '"' + "kind=" + '"'
				+ "sourcePath" + '"' + "name=" + '"' + '"' + "/>\n";

		for (int i = 0; i < directoryPathList.size(); i++) {
			sourceEntitiesText = sourceEntitiesText + "\t<entry flags=" + '"' + "VALUE_WORKSPACE_PATH" + '"' + "kind="
					+ '"' + "sourcePath" + '"' + "name=" + '"' + directoryPathList.get(i) + '"' + "/>\n";
		}

		sourceEntitiesText = sourceEntitiesText + "</sourceEntries>";

		System.out.println(sourceEntitiesText);

		PrintWriter out = new PrintWriter("resources/XMLMiddleParagraph.txt");
		out.println(sourceEntitiesText);
		out.close();
		
	}

	private static void printTheList(List<String> directoryPathList1) {

		for (int i = 0; i < directoryPathList1.size(); i++)

			System.out.println(directoryPathList1.get(i));

	}

}
