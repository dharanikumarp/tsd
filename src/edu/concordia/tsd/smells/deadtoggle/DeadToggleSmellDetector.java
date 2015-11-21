package edu.concordia.tsd.smells.deadtoggle;

import java.util.List;

import org.eclipse.cdt.core.model.ICProject;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class DeadToggleSmellDetector {

	private ICProject project;
	private List<String> deadToggles;

	public DeadToggleSmellDetector(ICProject project, List<String> deadToggles) {
		this.project = project;
		this.deadToggles = deadToggles;
	}

	private void scanTranslationUnit() {
		
		
	}

	public void detectSmells() {

	}
}
