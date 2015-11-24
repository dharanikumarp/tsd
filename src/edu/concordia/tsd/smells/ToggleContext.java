package edu.concordia.tsd.smells;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author dharani kumar palani(d_palan@encs.concordia.ca)
 */
public class ToggleContext {

	/**
	 * @uml.property  name="toggleFlag"
	 */
	private String toggleFlag;
	/**
	 * @uml.property  name="fileVsLocations"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Integer" qualifier="fileName:java.lang.String java.util.Set"
	 */
	private Map<String, Set<Integer>> fileVsLocations = new HashMap<String, Set<Integer>>();

	public ToggleContext(final String toggleFlag) {
		this.toggleFlag = toggleFlag;
	}

	public void addFileAndLocation(String fileName, int location) {
		Set<Integer> locations = fileVsLocations.get(fileName);

		if (locations != null) {
			locations.add(location);
		} else {
			locations = new HashSet<Integer>();
			locations.add(location);
			fileVsLocations.put(fileName, locations);
		}
	}
	
	public void setLocationForFile(String fileName, Set<Integer> locations) {
		fileVsLocations.put(fileName, locations);
	}

	/**
	 * @return
	 * @uml.property  name="toggleFlag"
	 */
	public String getToggleFlag() {
		return this.toggleFlag;
	}

	public Map<String, Set<Integer>> getFileAndLocations() {
		return this.fileVsLocations;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ToggleContext [toggleFlag=").append(toggleFlag).append(", fileVsLocations=")
				.append(fileVsLocations).append("]");
		return builder.toString();
	}
	
	
}
