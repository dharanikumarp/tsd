package edu.concordia.tsd.smells.spreadtoggle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class IFStatementAndFunctionVisitor extends ASTVisitor {

	// Map of toggle flag versus locations
	/**
	 * @uml.property  name="flagLocations"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Integer" qualifier="argName:java.lang.String java.util.Set"
	 */
	private Map<String, Set<Integer>> flagLocations = new HashMap<String, Set<Integer>>();

	// The toggle method name used to refine our search.
	/**
	 * @uml.property  name="toggleMethodName"
	 */
	private String toggleMethodName;

	public IFStatementAndFunctionVisitor(String toggleMethodName) {
		super(true);
		shouldVisitStatements = true;
		this.toggleMethodName = toggleMethodName;
	}

	@Override
	public int visit(IASTStatement statement) {
		
		if (statement instanceof ICPPASTIfStatement) {
			final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;

			IASTExpression conditionalExpr = cppIFStatement.getConditionExpression();

			if (conditionalExpr != null && conditionalExpr instanceof IASTFunctionCallExpression
					&& conditionalExpr.getRawSignature().contains(toggleMethodName)) {
				IASTFunctionCallExpression fcexpr = (IASTFunctionCallExpression) conditionalExpr;

				String rawSignature = fcexpr.getRawSignature();
				insertToggleFlags(fcexpr);
				
			} else if (conditionalExpr instanceof IASTBinaryExpression) {
				// This is the case where the function call expression
				// is mingled with other conditions.
			} else if(conditionalExpr instanceof ICPPASTUnaryExpression) {
				//For negated checks inside the IF statement.
				ICPPASTUnaryExpression unaryExpr = (ICPPASTUnaryExpression) conditionalExpr;
				
				IASTExpression expr = unaryExpr.getOperand();
				
				if(expr.getRawSignature().contains(toggleMethodName)) {
					insertToggleFlags((ICPPASTFunctionCallExpression) expr);
				}
			}
		}
		return super.visit(statement);
	}
	
	public Map<String, Set<Integer>> getToggleFlagsUsedInThisTU() {
		return flagLocations;
	}
	
	private void insertToggleFlags(IASTFunctionCallExpression fcexpr) {
		IASTInitializerClause[] arguments = fcexpr.getArguments();
		String argName = arguments[0].getRawSignature();
		Set<Integer> locations = flagLocations.get(argName);
		
		if(locations != null) {
			locations.add(fcexpr.getFileLocation().getStartingLineNumber());
		} else {
			locations = new HashSet<Integer>();
			locations.add(fcexpr.getFileLocation().getStartingLineNumber());
			flagLocations.put(argName, locations);
		}
	}
}
