package edu.concordia.tsd.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryPathFinder {
	
	// Need to write generate the XML file with the corresponding paths 

	private final String PATH = "/Users/hitman4r44/Documents/Google_chrome_code/src/directoryTesting";
	List<String> directoryPathList = new ArrayList<String>();

	public void walk(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath());
				directoryPathList.add(f.getAbsoluteFile().toString().replace(PATH, ""));
			}
		}
	}

	public static void main(String[] args) {

		DirectoryPathFinder pathFinder = new DirectoryPathFinder();
		pathFinder.walk(pathFinder.PATH);
		printTheList(pathFinder.directoryPathList);
	}

	private static void printTheList(List<String> directoryPathList1) {

		for (int i = 0; i < directoryPathList1.size(); i++)

			System.out.println(directoryPathList1.get(i));

	}

}
