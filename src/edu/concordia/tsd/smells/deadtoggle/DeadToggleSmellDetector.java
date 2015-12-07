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
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
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

					if (fcexpr != null) {
						checkDeadToggleSmell(fcexpr);
					}

				} else if (conditionalExpr instanceof IASTBinaryExpression) {
					// This is the case where the function call expression
					// is mingled with other conditions.

					ICPPASTBinaryExpression cppASTBinaryExpression = (ICPPASTBinaryExpression) conditionalExpr;

					Set<String> allToggleFlagsUsed = getAllToggleFlags(cppASTBinaryExpression, new HashSet<String>());
					allToggleFlagsUsed.remove(null);

					for (String toggleFlagUsed : allToggleFlagsUsed) {
						for (String deadToggleFlagFromRepo : deadToggles) {
							if (deadToggleFlagFromRepo.contains(toggleFlagUsed)) {
								Set<ToggleContext> set = new HashSet<ToggleContext>();
								ToggleContext tc = new ToggleContext(toggleFlagUsed);
								tc.addFileAndLocation(cppASTBinaryExpression.getContainingFilename(),
										cppASTBinaryExpression.getFileLocation().getStartingLineNumber());
								set.add(tc);
								toggleSmells.add(new ToggleSmell(ToggleSmellType.DEAD_TOGGLE, set));
							}
						}
					}

				} else if (conditionalExpr instanceof ICPPASTUnaryExpression) {
					// For negated checks inside the IF statement.
					ICPPASTUnaryExpression unaryExpr = (ICPPASTUnaryExpression) conditionalExpr;

					IASTExpression expr = unaryExpr.getOperand();

					if (expr.getRawSignature().contains(toggleMethodName)) {
						fcexpr = (IASTFunctionCallExpression) expr;

						if (fcexpr != null) {
							checkDeadToggleSmell(fcexpr);
						}
					}
				}

			}
			return PROCESS_CONTINUE;
		}

		private void checkDeadToggleSmell(IASTFunctionCallExpression fcexpr) {
			IASTInitializerClause[] arguments = fcexpr.getArguments();
			String argName = arguments[0].getRawSignature();

			// System.out.println("argName " + argName);
			argName = argName.substring(1, argName.length() - 1);
			// System.out.println("argName " + argName);

			if (argName.contains("switches::")) {
				argName = argName.substring(10, argName.length());
			}

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

		private Set<String> getAllToggleFlags(ICPPASTBinaryExpression expr, Set<String> accumulator) {
			IASTExpression[] operandArray = { expr.getOperand1(), expr.getOperand2() };

			for (IASTExpression iastExpression : operandArray) {
				if (iastExpression instanceof ICPPASTBinaryExpression) {
					getAllToggleFlags((ICPPASTBinaryExpression) iastExpression, accumulator);
				} else if (iastExpression instanceof ICPPASTUnaryExpression) {

					ICPPASTUnaryExpression unaryExpr = (ICPPASTUnaryExpression) iastExpression;
					IASTExpression unaryOperandExpr = unaryExpr.getOperand();

					if (unaryOperandExpr instanceof ICPPASTBinaryExpression) {
						accumulator.addAll(getAllToggleFlags((ICPPASTBinaryExpression) unaryOperandExpr, accumulator));
					} else if (unaryOperandExpr instanceof ICPPASTFunctionCallExpression) {
						accumulator.add(extractToggleFlag((ICPPASTFunctionCallExpression) unaryOperandExpr));
					}
				} else {
					accumulator.add(extractToggleFlagFromOperands(iastExpression));
				}
			}

			return accumulator;
		}

		private String extractToggleFlagFromOperands(IASTExpression operand1) {
			if (operand1 instanceof ICPPASTUnaryExpression) {
				ICPPASTUnaryExpression unaryExpr = (ICPPASTUnaryExpression) operand1;
				IASTExpression unaryOperandExpr = unaryExpr.getOperand();

				if (unaryOperandExpr instanceof ICPPASTFunctionCallExpression) {
					return extractToggleFlag((ICPPASTFunctionCallExpression) unaryOperandExpr);
				} else {
					return null;
				}
			} else if (operand1 instanceof ICPPASTFunctionCallExpression) {
				return extractToggleFlag((ICPPASTFunctionCallExpression) operand1);
			} else {
				return null;
			}
		}

		private String extractToggleFlag(ICPPASTFunctionCallExpression fcExpr) {
			String rs = fcExpr.getRawSignature();
			if (rs != null && !rs.contains(toggleMethodName))
				return null;

			IASTInitializerClause[] arguments = fcExpr.getArguments();

			if (arguments != null && arguments.length > 0) {
				String argName = arguments[0].getRawSignature();
				argName = argName.substring(1, argName.length() - 1);

				if (argName.contains("switches::")) {
					argName = argName.substring(10, argName.length());
				}
				// System.out.println("argName " + argName);
				return argName;
			}

			return null;
		}
	}
}
