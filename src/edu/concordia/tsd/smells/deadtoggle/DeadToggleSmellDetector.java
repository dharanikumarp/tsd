package edu.concordia.tsd.smells.deadtoggle;

import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.concordia.tsd.smells.ToggleSmellType;
import edu.concordia.tsd.smells.detector.AbstractSmellDetector;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class DeadToggleSmellDetector extends AbstractSmellDetector {

	@Override
	public ToggleSmellType getDetectorType() {
		return ToggleSmellType.DEAD_TOGGLE;
	}

	@Override
	protected void scanTranslationUnit(ITranslationUnit tu) {
		
	}
}
