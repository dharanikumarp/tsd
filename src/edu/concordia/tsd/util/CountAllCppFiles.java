package edu.concordia.tsd.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class CountAllCppFiles {
	
	static String path = "/Users/hitman4r44/Documents/Google_chrome_code/src";
	static String[] allDirectoryList;
	
	public static void main(String[] args) {
		
		allDirectoryList = getAllDirectoriesName(new File(path));
		walkin(new File("/Users/hitman4r44/Documents/Google_chrome_code/src/.git"));
		
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

	public static void walkin(File dir) {
		String pattern = "txt";

		if (!dir.isHidden()) {

			File listFile[] = dir.listFiles();

			if (listFile != null) {
				for (int i = 0; i < listFile.length; i++) {

					if (listFile[i].isDirectory()) {
						walkin(listFile[i]);
					} else {
						if (listFile[i].getName().endsWith(pattern)) {
							System.out.println(listFile[i].getPath());
						}
					}
				}
			}

		}
	}
}
