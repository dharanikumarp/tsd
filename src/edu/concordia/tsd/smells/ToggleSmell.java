package edu.concordia.tsd.smells;

import java.util.HashSet;
import java.util.Set;

/**
 * To contain smell information.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class ToggleSmell {

	private ToggleSmellType smellType;
	private String fileName;
	// Can hold multiple locations in a single file. Stores line numbers
	private Set<Integer> locations;

	public ToggleSmell(final ToggleSmellType smellType, final String fileName) {
		this(smellType, fileName, new HashSet<Integer>());
	}

	public ToggleSmell(final ToggleSmellType smellType, final String fileName, Set<Integer> locations) {
		this.smellType = smellType;
		this.fileName = fileName;
		this.locations = locations;
	}

	public void addLocation(int location) {
		locations.add(location);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ToggleSmell [smellType=").append(smellType).append(", fileName=").append(fileName)
				.append(", locations=").append(locations).append("]");
		return builder.toString();
	}
}
