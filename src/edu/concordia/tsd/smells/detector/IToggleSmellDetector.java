package edu.concordia.tsd.smells.detector;

import java.util.Set;

import org.eclipse.cdt.core.model.ICProject;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 */
public interface IToggleSmellDetector {

	/**
	 * Returns the smell detector type.
	 */
	public ToggleSmellType getDetectorType();

	/**
	 * @param project
	 *            - the C++ project to scan
	 * @param methodName
	 *            - the toggle method name
	 * @return the Set of detected toggle smells
	 */
	public Set<ToggleSmell> getToggleSmells(ICProject project, String methodName);

	/**
	 * Returns the number of files scanned by this detector.
	 */
	public long getNumFilesScanned();
}
