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

	/**
	 * @uml.property name="toggleSmells"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="edu.concordia.tsd.smells.ToggleSmell"
	 */
	protected Set<ToggleSmell> toggleSmells = new HashSet<ToggleSmell>();
	/**
	 * @uml.property name="icProject"
	 * @uml.associationEnd
	 */
	protected ICProject icProject;
	/**
	 * @uml.property name="toggleMethodName"
	 */
	protected String toggleMethodName;

	/**
	 * @uml.property name="deadToggles"
	 */
	protected Set<String> deadToggles;

	public Set<ToggleSmell> getToggleSmells(ICProject icProject, final String methodName, Set<String> deadToggleFlags) {
		this.icProject = icProject;
		this.toggleMethodName = methodName;
		this.deadToggles = deadToggleFlags;
		detectSmells();
		return toggleSmells;
	}

	protected void detectSmells() {
		ISourceRoot[] allSourceRoots = null;
		try {
			allSourceRoots = icProject.getAllSourceRoots();
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
