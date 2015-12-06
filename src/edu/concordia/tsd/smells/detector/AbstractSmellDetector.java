package edu.concordia.tsd.smells.detector;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
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

	protected Set<String> deadToggles;

	private int numFiles = 0;

	public Set<ToggleSmell> getToggleSmells(ICProject icProject, final String methodName) {
		this.icProject = icProject;
		this.toggleMethodName = methodName;
		detectSmells();
		return toggleSmells;
	}

	protected void detectSmells() {
		ISourceRoot[] allSourceRoots = null;
		try {

			allSourceRoots = icProject.getAllSourceRoots();

			ICElement[] childrens = icProject.getChildren();

			for (ICElement icElement : childrens) {
				System.out.println("icElement " + icElement.getElementName());
			}

			for (ISourceRoot iSourceRoot : allSourceRoots) {
				ITranslationUnit[] allTU = iSourceRoot.getTranslationUnits();

				for (ITranslationUnit iTranslationUnit : allTU) {
					if (iTranslationUnit.isCXXLanguage() && iTranslationUnit.isSourceUnit()) {
						numFiles++;
						scanTranslationUnit(iTranslationUnit);
					}
				}
			}
		} catch (CModelException e) {
			e.printStackTrace();
		}
	}

	public long getNumFilesScanned() {
		return this.numFiles;
	}

	protected abstract void scanTranslationUnit(ITranslationUnit tu);
}
