package edu.concordia.tsd.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MergerFiles {

	public static void main(String[] args) {
		
		String sourceFile1Path = "resources/XMLFirstParagraph.txt";
		String sourceFile2Path = "resources/XMLMiddleParagraph.txt";
		String sourceFile3Path = "resources/XMLLastParagraph.txt";

		String mergedFilePath = "resources/.cproject";

		File[] files = new File[3];
		
		files[0] = new File(sourceFile1Path);
		files[1] = new File(sourceFile2Path);
		files[2] = new File(sourceFile3Path);

		File mergedFile = new File(mergedFilePath);

		mergeFiles(files, mergedFile);
	}

	public static void mergeFiles(File[] files, File mergedFile) {

		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(mergedFile, true);
			out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (File f : files) {
			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));

				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
