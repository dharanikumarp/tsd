package edu.concordia.tsd.smells.spreadtoggle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

import edu.concordia.tsd.smells.ToggleContext;
import edu.concordia.tsd.smells.ToggleSmell;
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
	/**
	 * @uml.property  name="toggleSpreadMap"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="org.eclipse.cdt.core.model.ITranslationUnit" qualifier="string:java.lang.String java.util.Set"
	 */
	private Map<String, Set<ITranslationUnit>> toggleSpreadMap = new HashMap<String, Set<ITranslationUnit>>();

	// Toggle Flag is the flag that is used in several files.
	/**
	 * @uml.property  name="flagSpread"
	 * @uml.associationEnd  qualifier="string1:java.lang.String java.util.Set"
	 */
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
		//System.out.println("ToggleSpreadSmellDetector.addToggleFlag(), flagName " + flagName + ", fileName " + fileName
		//		+ ", lineNumbers " + lineNumbers);
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

		Set<String> keySetFromToggleSpread = flagSpread.keySet();

		for (String string : keySetFromToggleSpread) {
			System.out.println(string + " <-> " + flagSpread.get(string));
		}
	}

	@Override
	protected void scanTranslationUnit(ITranslationUnit tu) {
		try {
			IASTTranslationUnit astTU = tu.getAST();

			IFStatementAndFunctionVisitor visitor = new IFStatementAndFunctionVisitor(toggleMethodName);
			astTU.accept(visitor);
			Map<String, Set<Integer>> map = visitor.getToggleFlagsUsedInThisTU();

			if (!map.isEmpty())
				//System.out.println("Toggles locations in tu " + map);

			if (!map.isEmpty()) {
				Set<String> keySet = map.keySet();

				for (String string : keySet) {
					addToggleFlag(string, tu.getPath().toString(), map.get(string));

					Set<ITranslationUnit> setTu = this.toggleSpreadMap.get(string);

					if (setTu == null) {
						setTu = new HashSet<ITranslationUnit>();
						setTu.add(tu);
						toggleSpreadMap.put(string, setTu);
					} else {
						setTu.add(tu);
					}
				}
			}
		} catch (CoreException e) {
		}
	}

	@Override
	public void detectSmells() {
		super.detectSmells();
		Set<String> keySet = flagSpread.keySet();
		
		Set<ToggleContext> allToggleContext = new HashSet<ToggleContext>();

		for (String string : keySet) {
			Map<String, Set<Integer>> spreadFiles = flagSpread.get(string);
			
			if(spreadFiles.size() > 1) {
				
				Set<String> fileNames = spreadFiles.keySet();
				ToggleContext tc = new ToggleContext(string);
				
				for (String string1 : fileNames) {
					tc.setLocationForFile(string1, spreadFiles.get(string1));
				}
				allToggleContext.add(tc);
			}
		}
		toggleSmells.add(new ToggleSmell(ToggleSmellType.SPREAD_TOGGLE, allToggleContext));
	}

}
