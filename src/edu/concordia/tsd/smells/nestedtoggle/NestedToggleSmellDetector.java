package edu.concordia.tsd.smells.nestedtoggle;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;

/**
 * Identifies the cases of nested toggles in the source code.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class NestedToggleSmellDetector {

	private ICProject icProject;
	private String toggleMethodName;

	public NestedToggleSmellDetector(ICProject icProject, final String toggleMethodName) {
		this.icProject = icProject;
		this.toggleMethodName = toggleMethodName;
	}

	private void detectSmells() {

		ISourceRoot[] allSourceRoots = null;
		try {
			allSourceRoots = icProject.getAllSourceRoots();
			System.out.println("allSourceRoots " + allSourceRoots.length);

			for (ISourceRoot iSourceRoot : allSourceRoots) {

			}
			
			
		} catch (CModelException e) {
			e.printStackTrace();
		}

	}
}
