package edu.concordia.tsd.smells.spreadtoggle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import edu.concordia.tsd.smells.ToggleSmellType;
import edu.concordia.tsd.smells.detector.AbstractSmellDetector;

/**
 * This smell detector class will detects spread of toggles among classes.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class ToggleSpreadSmellDetector extends AbstractSmellDetector {

	// Map of string toggle names or toggle reference constants versus of List
	// of ITranslationUnits
	private Map<String, List<ITranslationUnit>> toggleSpreadMap = new HashMap<String, List<ITranslationUnit>>();

	@Override
	public ToggleSmellType getDetectorType() {
		return ToggleSmellType.SPREAD_TOGGLE;
	}

	@Override
	protected void scanTranslationUnit(ITranslationUnit tu) {
		try {
			IASTTranslationUnit astTU = tu.getAST();
			
			
		} catch (CoreException e) {

		}
	}
}
