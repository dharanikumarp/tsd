package edu.concordia.tsd.smells.nestedtoggle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.concordia.tsd.smells.ToggleContext;
import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class NestedFunctionCallExprVisitor extends ASTVisitor {

	/**
	 * @uml.property  name="tu"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ITranslationUnit tu;
	/**
	 * @uml.property  name="toggleMethodName"
	 */
	private String toggleMethodName;

	/**
	 * @uml.property  name="ifStatementASTNodesVsflag"
	 */
	private Map<IASTNode, String> ifStatementASTNodesVsflag = new HashMap<IASTNode, String>();

	/**
	 * @uml.property  name="toggleSmells"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="edu.concordia.tsd.smells.ToggleSmell"
	 */
	private Set<ToggleSmell> toggleSmells = new HashSet<ToggleSmell>();

	public NestedFunctionCallExprVisitor(final ITranslationUnit tu, final String toggleMethodName) {
		super(true);
		// System.out.println("NestedFunctionCallExprVisitor.NestedFunctionCallExprVisitor()
		// " + tu.getElementName());
		this.tu = tu;
		this.toggleMethodName = toggleMethodName;
		shouldVisitStatements = true;
		// shouldVisitExpressions = true;
	}

	@Override
	public int visit(IASTStatement statement) {
		if (statement instanceof ICPPASTIfStatement) {
			final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;
			IASTExpression conditionalExpression = cppIFStatement.getConditionExpression();
			if (conditionalExpression != null) {

				// Identifies the single function call of toggleMethod in an if
				// condition.
				if (conditionalExpression instanceof ICPPASTFunctionCallExpression) {
					ICPPASTFunctionCallExpression fcexpr = (ICPPASTFunctionCallExpression) conditionalExpression;
					if (fcexpr.getRawSignature().contains(toggleMethodName)) {
						checkParentIFASTNodes(cppIFStatement, fcexpr);
					}
				}
			} else if (conditionalExpression instanceof ICPPASTUnaryExpression) {
				ICPPASTUnaryExpression unaryExpr = (ICPPASTUnaryExpression) conditionalExpression;
				IASTExpression expr = unaryExpr.getOperand();

				if (expr.getRawSignature().contains(toggleMethodName)) {
					checkParentIFASTNodes(cppIFStatement, (ICPPASTFunctionCallExpression) expr);
				}
			}
		}
		return PROCESS_CONTINUE;
	}

	public Set<ToggleSmell> getSmells() {
		return this.toggleSmells;
	}

	private void checkParentIFASTNodes(ICPPASTIfStatement cppIFStatement, ICPPASTFunctionCallExpression fcexpr) {

		IASTInitializerClause[] arguments = fcexpr.getArguments();
		Set<IASTNode> keySet = ifStatementASTNodesVsflag.keySet();

		for (IASTNode iastNode : keySet) {
			if (iastNode.contains(cppIFStatement.getOriginalNode())) {

				ToggleContext tc = new ToggleContext(arguments[0].getRawSignature());
				tc.addFileAndLocation(iastNode.getFileLocation().getFileName(),
						iastNode.getFileLocation().getStartingLineNumber());

				Set<ToggleContext> tcs = new HashSet<ToggleContext>();
				tcs.add(tc);

				ToggleSmell toggleSmell = new ToggleSmell(ToggleSmellType.NESTED_TOGGLE, tcs);
				toggleSmells.add(toggleSmell);
			}
		}

		ifStatementASTNodesVsflag.put(cppIFStatement.getOriginalNode(), arguments[0].getRawSignature());
	}
}
