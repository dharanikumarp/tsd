package edu.concordia.tsd.smells.detector;

import java.util.Set;

import org.eclipse.cdt.core.model.ICProject;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 */
public interface ToggleSmellDetector {

	public ToggleSmellType getDetectorType();

	/**
	 * @return the Set of detected toggle smells
	 */
	public Set<ToggleSmell> getToggleSmells(ICProject project, String methodName);
}
