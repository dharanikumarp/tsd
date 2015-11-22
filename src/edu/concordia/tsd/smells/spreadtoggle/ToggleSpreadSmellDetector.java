package edu.concordia.tsd.smells.spreadtoggle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import edu.concordia.tsd.smells.ToggleSmellType;
import edu.concordia.tsd.smells.detector.AbstractSmellDetector;

/**
 * This smell detector class will detects spread of toggles among classes.
 * 
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 *
 */
public class ToggleSpreadSmellDetector extends AbstractSmellDetector {

	// Map of string toggle names or toggle reference constants versus of List
	// of ITranslationUnits
	private Map<String, List<ITranslationUnit>> toggleSpreadMap = new HashMap<String, List<ITranslationUnit>>();

	// Toggle Flag is the flag that is used in several files.
	private Map<String, Map<String, Set<Integer>>> flagSpread = new HashMap<String, Map<String, Set<Integer>>>();

	@Override
	public ToggleSmellType getDetectorType() {
		return ToggleSmellType.SPREAD_TOGGLE;
	}

	/**
	 * Method to add a toggle flag that is spread across several files.
	 * 
	 * @param flagName
	 * @param fileName
	 * @param lineNumbers
	 */
	private void addToggleFlag(final String flagName, final String fileName, Set<Integer> lineNumbers) {
		Map<String, Set<Integer>> map = flagSpread.get(flagName);

		if (map != null) {
			Set<Integer> locations = map.get(fileName);

			if (locations != null) {
				locations.addAll(lineNumbers);
			} else {
				locations = new HashSet<Integer>();
				locations.addAll(lineNumbers);
				map.put(fileName, locations);
			}
		} else {
			Map<String, Set<Integer>> innerMap = new HashMap<String, Set<Integer>>();
			Set<Integer> location = new HashSet<Integer>();
			location.addAll(lineNumbers);
			innerMap.put(fileName, location);

			flagSpread.put(flagName, innerMap);
		}
	}

	@Override
	protected void scanTranslationUnit(ITranslationUnit tu) {
		try {
			IASTTranslationUnit astTU = tu.getAST();

			IFStatementAndFunctionVisitor visitor = new IFStatementAndFunctionVisitor(
					toggleMethodName);
			astTU.accept(visitor);
			Map<String, Set<Integer>> map = visitor.getToggleFlagsUsedInThisTU();

			if (!map.isEmpty()) {
				Set<String> keySet = map.keySet();

				for (String string : keySet) {
					addToggleFlag(string, astTU.getFileLocation().getFileName(), map.get(string));
				}
			}
		} catch (CoreException e) {

		}
		
		
		
	}
}
