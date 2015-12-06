package edu.concordia.tsd.smells.deadtoggle;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import edu.concordia.tsd.smells.ToggleContext;
import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;
import edu.concordia.tsd.smells.detector.AbstractSmellDetector;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class DeadToggleSmellDetector extends AbstractSmellDetector {

	private Set<String> deadToggles = null;

	public DeadToggleSmellDetector() {
		try {
			deadToggles = new DeadAndStableToggleFlagsLoader().getDeadToggleFlags();
			System.out.println("deadToggles " + deadToggles);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ToggleSmellType getDetectorType() {
		return ToggleSmellType.DEAD_TOGGLE;
	}

	@Override
	protected void scanTranslationUnit(ITranslationUnit tu) {

		// do not scan and get AST if there are no dead toggles.
		if (deadToggles == null || deadToggles.isEmpty()) {
			return;
		}

		try {
			IASTTranslationUnit astTU = tu.getAST();
			astTU.accept(new ToggleMethodNameVisitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private class ToggleMethodNameVisitor extends ASTVisitor {

		public ToggleMethodNameVisitor() {
			super(true);
			shouldVisitStatements = true;
		}

		@Override
		public int visit(IASTStatement statement) {

			IASTFunctionCallExpression fcexpr = null;

			if (statement instanceof ICPPASTIfStatement) {
				final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;

				IASTExpression conditionalExpr = cppIFStatement.getConditionExpression();

				if (conditionalExpr != null && conditionalExpr instanceof IASTFunctionCallExpression
						&& conditionalExpr.getRawSignature().contains(toggleMethodName)) {
					fcexpr = (IASTFunctionCallExpression) conditionalExpr;
				} else if (conditionalExpr instanceof IASTBinaryExpression) {
					// This is the case where the function call expression
					// is mingled with other conditions.
				} else if (conditionalExpr instanceof ICPPASTUnaryExpression) {
					// For negated checks inside the IF statement.
					ICPPASTUnaryExpression unaryExpr = (ICPPASTUnaryExpression) conditionalExpr;

					IASTExpression expr = unaryExpr.getOperand();

					if (expr.getRawSignature().contains(toggleMethodName)) {
						fcexpr = (IASTFunctionCallExpression) expr;
					}
				}

				if (fcexpr != null) {
					checkDeadToggleSmell(fcexpr);
				}
			}
			return super.visit(statement);
		}

		private void checkDeadToggleSmell(IASTFunctionCallExpression fcexpr) {
			IASTInitializerClause[] arguments = fcexpr.getArguments();
			String argName = arguments[0].getRawSignature();

			for (String string : deadToggles) {
				if (string.contains(argName)) {
					Set<ToggleContext> set = new HashSet<ToggleContext>();
					ToggleContext tc = new ToggleContext(argName);
					tc.addFileAndLocation(fcexpr.getContainingFilename(),
							fcexpr.getFileLocation().getStartingLineNumber());
					set.add(tc);
					toggleSmells.add(new ToggleSmell(ToggleSmellType.DEAD_TOGGLE, set));
				}
			}
		}
	}
}
