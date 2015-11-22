/**
 * 
 */
package edu.concordia.tsd.smells.detector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.concordia.tsd.smells.ToggleSmell;

/**
 * @author dharani kumar palan(d_palan@encs.concordia.ca)
 *
 */
public abstract class AbstractSmellDetector implements IToggleSmellDetector {

	protected Set<ToggleSmell> toggleSmells = new HashSet<ToggleSmell>();
	protected ICProject icProject;
	protected String toggleMethodName;

	public Set<ToggleSmell> getToggleSmells(ICProject icProject, final String methodName, List<String> deadToggleFlags) {
		this.icProject = icProject;
		this.toggleMethodName = methodName;
		detectSmells();
		return toggleSmells;
	}

	private void detectSmells() {
		ISourceRoot[] allSourceRoots = null;
		try {
			allSourceRoots = icProject.getAllSourceRoots();
			System.out.println("allSourceRoots " + allSourceRoots.length);

			for (ISourceRoot iSourceRoot : allSourceRoots) {
				ITranslationUnit[] allTU = iSourceRoot.getTranslationUnits();

				for (ITranslationUnit iTranslationUnit : allTU) {
					if (iTranslationUnit.isCXXLanguage() && iTranslationUnit.isSourceUnit()) {
						scanTranslationUnit(iTranslationUnit);
					}
				}
			}
		} catch (CModelException e) {
			e.printStackTrace();
		}
	}

	protected abstract void scanTranslationUnit(ITranslationUnit tu);
}
