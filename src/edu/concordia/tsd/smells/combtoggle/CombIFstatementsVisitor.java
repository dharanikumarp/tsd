package edu.concordia.tsd.smells.combtoggle;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.concordia.tsd.smells.ToggleContext;
import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;

/**
 * An AST visitor which checks only for the
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class CombIFstatementsVisitor extends ASTVisitor {

	private ITranslationUnit tu;
	private String toggleMethodName;

	private Set<ToggleSmell> toggleSmells = new HashSet<ToggleSmell>();

	public CombIFstatementsVisitor(final ITranslationUnit tu, final String toggleMethodName) {
		super(true);
		this.tu = tu;
		this.toggleMethodName = toggleMethodName;
		shouldVisitStatements = true;
	}

	@Override
	public int visit(IASTStatement statement) {

		IASTNode originalNode = statement.getOriginalNode();

		if (!originalNode.getFileLocation().getFileName().contains(tu.getElementName())) {
			return PROCESS_CONTINUE;
		}

		if (statement instanceof ICPPASTIfStatement) {
			final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;
			IASTExpression conditionalExpression = cppIFStatement.getConditionExpression();

			if (conditionalExpression != null && conditionalExpression instanceof ICPPASTBinaryExpression) {
				ICPPASTBinaryExpression cppASTBinaryExpression = (ICPPASTBinaryExpression) conditionalExpression;

				Set<String> allToggleFlagsUsed = getAllToggleFlags(cppASTBinaryExpression, new HashSet<String>());
				allToggleFlagsUsed.remove(null);

				if (allToggleFlagsUsed.size() >= 2) {
					Set<ToggleContext> tcs = new HashSet<ToggleContext>();

					for (String toggleFlagName : allToggleFlagsUsed) {
						ToggleContext tc = new ToggleContext(toggleFlagName);
						tc.addFileAndLocation(originalNode.getContainingFilename(),
								originalNode.getFileLocation().getStartingLineNumber());
						tcs.add(tc);
					}

					toggleSmells.add(new ToggleSmell(ToggleSmellType.COMBINATORIAL_TOGGLE, tcs));
				}
			}
		}

		return PROCESS_CONTINUE;
	}

	public Set<ToggleSmell> getSmells() {
		return this.toggleSmells;
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

		if (arguments != null && arguments.length > 0)
			return arguments[0].getRawSignature();

		return null;
	}
}
