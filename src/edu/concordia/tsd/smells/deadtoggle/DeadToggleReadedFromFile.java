package edu.concordia.tsd.smells.deadtoggle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DeadToggleReadedFromFile {

	private final String PATH = "resources/dead_toggles.txt";
	private BufferedReader reader;

	private String strLine = "";
	private StringTokenizer st = null;
	int lineNumber = 0;
	int tokenNumber = 0;

	public DeadToggleReadedFromFile() throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(PATH));
	}

	private List<DeadToggle> getDeadToggles() throws FileNotFoundException, IOException {

		List<DeadToggle> deadToggleList = new ArrayList<DeadToggle>();

		while ((strLine = reader.readLine()) != null) {
			lineNumber++;

			if (lineNumber != 1 && lineNumber != 2) {

				st = new StringTokenizer(strLine, "|");
				DeadToggle deadToggleObj = new DeadToggle();

				while (st.hasMoreTokens()) {
					// display dead toggle values
					tokenNumber++;

					if (tokenNumber == 1)
						deadToggleObj.setToggleValue(st.nextToken().replace("--", "").trim());
					else if (tokenNumber == 2) {
						deadToggleObj.setToggleFlagName(st.nextToken().trim());
					} else {
						st.nextToken();
					}

					// System.out.println("Line # " + lineNumber + ", Token # "
					// + tokenNumber + ", Token : " + st.nextToken());

				}

				// reset token number
				tokenNumber = 0;
				// Add DeadToggle Object into List
				deadToggleList.add(deadToggleObj);
			}

		} // End of While
		return deadToggleList;
	}// End of getDeadToggle Method

	public static void main(String[] args) throws IOException {
		
		DeadToggleReadedFromFile deadToggleReadedFromFile = new DeadToggleReadedFromFile();

		List<DeadToggle> deadToggleList = deadToggleReadedFromFile.getDeadToggles();

		for (int i = 0; i < deadToggleList.size(); i++) {
			System.out.println("ToggleFlagName(): " + deadToggleList.get(i).getToggleFlagName());
			System.out.println("ToggleValue(): " + deadToggleList.get(i).getToggleValue());
		}

	}// End of Main

}// End of class
