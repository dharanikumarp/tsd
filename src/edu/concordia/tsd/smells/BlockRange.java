package edu.concordia.tsd.smells;
import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * Manages the block range of this ASTNode, typically used for IF statements
 * with function call expressions
 * 
 * @author dharani kumar palan (d_palan@encs.concordia.ca)
 *
 */
public class BlockRange {
	private IASTNode astNode;
	private int startl;
	private int endl;

	public BlockRange(IASTNode astNode, int startl, int endl) {
		this.astNode = astNode;
		this.startl = startl;
		this.endl = endl;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.astNode.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public boolean containsASTNode(IASTNode node) {
		int startLN = node.getFileLocation().getStartingLineNumber();
		int endLN = node.getFileLocation().getEndingLineNumber();

		return this.startl < startLN && this.endl > endLN;
	}
}
