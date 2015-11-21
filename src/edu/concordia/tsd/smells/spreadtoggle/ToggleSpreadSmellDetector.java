package edu.concordia.tsd.smells.spreadtoggle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;

/**
 * This smell detector class will detects spread of toggles among classes.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class ToggleSpreadSmellDetector {

	private ICProject icProject;
	private ISourceRoot allSourceRoots;

	// Map of string toggle names or toggle reference constants versus of List
	// of ITranslationUnits
	private Map<String, List<ITranslationUnit>> toggleSpreadMap = new HashMap<String, List<ITranslationUnit>>();

	public ToggleSpreadSmellDetector(ICProject icProject) throws CModelException {
		this.icProject = icProject;
		ISourceRoot[] allSourceRoots = icProject.getAllSourceRoots();
	}

	private void scanSourceRoot(ISourceRoot sourceRoot) {
		try {
			ITranslationUnit[] units = sourceRoot.getTranslationUnits();

			for (ITranslationUnit iTranslationUnit : units) {
				scanTranslationUnit(iTranslationUnit);
			}

		} catch (CModelException e) {
			e.printStackTrace();
		}

	}

	private void scanTranslationUnit(ITranslationUnit unit) {
	}
}
