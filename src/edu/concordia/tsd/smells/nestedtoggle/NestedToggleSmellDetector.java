package edu.concordia.tsd.smells.nestedtoggle;

import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;
import edu.concordia.tsd.smells.detector.AbstractSmellDetector;

/**
 * Identifies the cases of nested toggles in the source code.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class NestedToggleSmellDetector extends AbstractSmellDetector {

	@Override
	public ToggleSmellType getDetectorType() {
		return ToggleSmellType.NESTED_TOGGLE;
	}

	/**
	 * @override
	 */
	protected void scanTranslationUnit(ITranslationUnit tu) {
		try {
			IASTTranslationUnit astTU = tu.getAST();
			NestedFunctionCallExprVisitor visitor = new NestedFunctionCallExprVisitor(tu, toggleMethodName);
			astTU.accept(visitor);
			Set<ToggleSmell> smells = visitor.getSmells();
			toggleSmells.addAll(smells);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
