package edu.concordia.tsd.smells.combtoggle;

import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;
import edu.concordia.tsd.smells.detector.AbstractSmellDetector;

/**
 * This toggle smell detector will detect smells of using combinatorial toggles
 * for an execution of a code block. This is a file level toggle smell.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class CombinatorialToggleSmellDetector extends AbstractSmellDetector {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.concordia.tsd.smells.detector.IToggleSmellDetector#getDetectorType()
	 */
	@Override
	public ToggleSmellType getDetectorType() {
		return ToggleSmellType.COMBINATORIAL_TOGGLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.concordia.tsd.smells.detector.AbstractSmellDetector#
	 * scanTranslationUnit(org.eclipse.cdt.core.model.ITranslationUnit)
	 */
	@Override
	protected void scanTranslationUnit(ITranslationUnit tu) {

		IASTTranslationUnit iastTU;
		try {
			iastTU = tu.getAST();
			CombIFstatementsVisitor visitor = new CombIFstatementsVisitor(tu, toggleMethodName);
			iastTU.accept(visitor);

			Set<ToggleSmell> smells = visitor.getSmells();

			if (!smells.isEmpty()) {
				this.toggleSmells.addAll(smells);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
