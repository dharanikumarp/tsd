package edu.concordia.tsd.util;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public final class IndexReferenceSearcher {

	public static void outputReferences(String functionName, ICProject icProject)
			throws CoreException, InterruptedException {
		// Access index
		IIndex index = CCorePlugin.getIndexManager().getIndex(icProject);
		index.acquireReadLock(); // we need a read-lock on the index
		try {
			// find bindings for name
			IIndexBinding[] bindings = index.findBindings(functionName.toCharArray(), IndexFilter.ALL_DECLARED,
					new NullProgressMonitor());
			// find references for each binding
			for (IIndexBinding b : bindings) {
				if (b instanceof IFunction) {
					outputReferences(index, b);
				}
			}
		} finally {
			index.releaseReadLock();
		}
	}

	public static void outputReferences(IIndex index, IBinding b) throws CoreException {
		IIndexName[] names = index.findReferences(b);
		for (IIndexName n : names) {
			outputReference(index, n);
		}
	}

	public static void outputReference(IIndex index, IIndexName n) throws CoreException {
		IASTFileLocation fileLoc = n.getFileLocation();
		System.out.println(fileLoc.getFileName() + " at startingLocation " + fileLoc.getStartingLineNumber()
				+ ", endingLineNumber " + fileLoc.getEndingLineNumber());
	}
}
