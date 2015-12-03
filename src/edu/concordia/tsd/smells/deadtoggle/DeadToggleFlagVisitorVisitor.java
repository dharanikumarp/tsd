package edu.concordia.tsd.smells.deadtoggle;

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
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class DeadToggleFlagVisitorVisitor extends ASTVisitor {

	// Map of toggle flag versus locations
	private Map<String, Set<Integer>> flagLocations = new HashMap<String, Set<Integer>>();

	// The toggle method name used to refine our search.
	private String toggleMethodName;

	public DeadToggleFlagVisitorVisitor(String toggleMethodName) {
		super(true);
		shouldVisitStatements = true;
		this.toggleMethodName = toggleMethodName;
	}

	@Override
	public int visit(IASTStatement statement) {
		System.out.println("visit(statement)");

		if (statement instanceof ICPPASTIfStatement) {
			final ICPPASTIfStatement cppIFStatement = (ICPPASTIfStatement) statement;

			IASTExpression conditionalExpr = cppIFStatement.getConditionExpression();

			if (conditionalExpr != null && conditionalExpr instanceof IASTFunctionCallExpression
					&& conditionalExpr.getRawSignature().contains(toggleMethodName)) {
				IASTFunctionCallExpression fcexpr = (IASTFunctionCallExpression) conditionalExpr;

				String rawSignature = fcexpr.getRawSignature();
				System.out.println("rawSignature " + rawSignature);

				IASTFileLocation fileLocation = cppIFStatement.getFileLocation();
				System.out.println("starting line number " + fileLocation.getStartingLineNumber());
				
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
			} else if (conditionalExpr instanceof IASTBinaryExpression) {
				// This is the special case where the function call expression
				// is mingled with other conditons.
			}
		}
		return super.visit(statement);
	}
	
	public Map<String, Set<Integer>> getToggleFlagsUsedInThisTU() {
		return flagLocations;
	}
}
