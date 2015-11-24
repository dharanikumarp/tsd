package edu.concordia.tsd.smells;

import java.util.Set;

/**
 * To contain smell information.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class ToggleSmell {

	/**
	 * @uml.property  name="smellType"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ToggleSmellType smellType;
	/**
	 * @uml.property  name="toggleContexts"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="edu.concordia.tsd.smells.ToggleContext"
	 */
	private Set<ToggleContext> toggleContexts;

	public ToggleSmell(final ToggleSmellType smellType, final Set<ToggleContext> tcs) {
		this.smellType = smellType;
		this.toggleContexts = tcs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ToggleSmell [smellType=").append(smellType).append(", toggleContexts=").append(toggleContexts)
				.append("]");
		return builder.toString();
	}
}
