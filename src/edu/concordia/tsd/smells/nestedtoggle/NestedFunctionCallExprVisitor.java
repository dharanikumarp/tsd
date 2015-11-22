package edu.concordia.tsd.smells.nestedtoggle;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.ToggleSmellType;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class NestedFunctionCallExprVisitor extends ASTVisitor {

	private ITranslationUnit tu;
	private String toggleMethodName;
	private Set<IASTNode> ifStatementASTNodes = new HashSet<IASTNode>();

	private Set<ToggleSmell> toggleSmells = new HashSet<ToggleSmell>();

	public NestedFunctionCallExprVisitor(final ITranslationUnit tu, final String toggleMethodName) {
		super(true);
		//System.out.println("NestedFunctionCallExprVisitor.NestedFunctionCallExprVisitor() " + tu.getElementName());
		this.tu = tu;
		this.toggleMethodName = toggleMethodName;
		shouldVisitStatements = true;
		//shouldVisitExpressions = true;
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
						for (IASTNode iastNode : ifStatementASTNodes) {
							if (iastNode.contains(cppIFStatement.getOriginalNode())) {
								ToggleSmell toggleSmell = new ToggleSmell(ToggleSmellType.NESTED_TOGGLE,
										tu.getFile().getName());
								toggleSmell.addLocation(iastNode.getFileLocation().getStartingLineNumber());
								toggleSmell.addLocation(
										cppIFStatement.getOriginalNode().getFileLocation().getStartingLineNumber());

								toggleSmells.add(toggleSmell);
							}
						}

						ifStatementASTNodes.add(cppIFStatement.getOriginalNode());
					}
				}
			} else {
				//System.err.println("conditionalExpression is null for a statement ");
			}

		}

		return PROCESS_CONTINUE;
	}

	public Set<ToggleSmell> getSmells() {
		return this.toggleSmells;
	}
}
