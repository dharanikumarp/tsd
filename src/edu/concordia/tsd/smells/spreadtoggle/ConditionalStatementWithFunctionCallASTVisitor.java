package edu.concordia.tsd.smells.spreadtoggle;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class ConditionalStatementWithFunctionCallASTVisitor extends ASTVisitor {

	private Set<IASTNode> allIFStatementsWithToggleCheck = new HashSet<IASTNode>();
	// The toggle method name used to refine our search.
	private String toggleMethodName;

	public ConditionalStatementWithFunctionCallASTVisitor(String toggleMethodName) {
		super(true);
		shouldVisitStatements = true;
		// shouldVisitExpressions = true;
		this.toggleMethodName = toggleMethodName;
	}

	@Override
	public int visit(IASTStatement statement) {
		System.out.println("visit(statement)");

		if (statement instanceof ICPPASTIfStatement) {
			System.out.println("statement is an if statement");
			final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;

			IASTExpression conditionalExpr = cppIFStatement.getConditionExpression();
			System.out.println("conditionExpr " + conditionalExpr.getRawSignature());

			if (conditionalExpr instanceof IASTFunctionCallExpression
					&& conditionalExpr.getRawSignature().contains(toggleMethodName)) {
				IASTFunctionCallExpression fcexpr = (IASTFunctionCallExpression) conditionalExpr;

				String rawSignature = fcexpr.getRawSignature();
				System.out.println("rawSignature " + rawSignature);

				IASTFileLocation fileLocation = cppIFStatement.getFileLocation();
				System.out.println("starting line number " + fileLocation.getStartingLineNumber());

			} else if (conditionalExpr instanceof IASTBinaryExpression) {
				// This is the special case where the function call expression
				// is mingled with other conditons.
				System.out.println("conditionalExpr is a binary expression.");

			}
		}
		return super.visit(statement);
	}
}
