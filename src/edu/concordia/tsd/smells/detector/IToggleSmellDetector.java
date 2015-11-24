package edu.concordia.tsd.smells.detector;

import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.model.ICProject;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;

/**
 * @author  dharani kumar palani (d_palan@encs.concordia.ca)
 */
public interface IToggleSmellDetector {

	/**
	 * @uml.property  name="detectorType"
	 * @uml.associationEnd  
	 */
	public ToggleSmellType getDetectorType();

	/**
	 * @param deadToggleFlags TODO
	 * @return the Set of detected toggle smells
	 */
	public Set<ToggleSmell> getToggleSmells(ICProject project, String methodName, List<String> deadToggleFlags);
}
