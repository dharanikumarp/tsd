package tsd.handlers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.deadtoggle.DeadToggleSmellDetector;
import edu.concordia.tsd.smells.detector.IToggleSmellDetector;
import edu.concordia.tsd.smells.nestedtoggle.NestedToggleSmellDetector;
import edu.concordia.tsd.smells.spreadtoggle.ToggleSpreadSmellDetector;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class TSDHandler extends AbstractHandler {

	private static final String TOGGLE_METHOD_NAME = "HasSwitch";
	private static final int LINE_LENGTH = 120;

	/**
	 * The constructor.
	 */
	public TSDHandler() {
		System.out.println("TSDHandler.TSDHandler()");
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
		Object selectedProject = null;

		if (currentSelection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) currentSelection;
			Object[] selectedProjects = (Object[]) ss.toArray();
			if (selectedProjects == null || selectedProjects.length == 0) {
				System.err.println("No projects are selected");
				return null;
			}

			// Select only the first one.
			selectedProject = selectedProjects[0];
			System.out.println("selectedProject " + selectedProject);
		}

		ICProject[] allProjects;
		try {

			Set<String> deadToggleFlags = loadDeadToggleFlags();

			allProjects = CoreModel.getDefault().getCModel().getCProjects();
			IToggleSmellDetector[] sds = new IToggleSmellDetector[] { new NestedToggleSmellDetector(),
					new ToggleSpreadSmellDetector(), new DeadToggleSmellDetector() };

			for (ICProject icProject : allProjects) {
				System.out.println("icProject " + icProject.getElementName());

				if (selectedProject.toString().contains(icProject.getElementName())) {

					for (IToggleSmellDetector smellDetector : sds) {

						long startTime = System.currentTimeMillis();

						Set<ToggleSmell> allDetectedSmells = smellDetector.getToggleSmells(icProject,
								TOGGLE_METHOD_NAME, deadToggleFlags);
						System.out.println("Smell Detector type " + smellDetector.getDetectorType());
						for (ToggleSmell toggleSmell : allDetectedSmells) {
							System.out.println(toggleSmell);
						}

						System.out.println("Time taken " + (System.currentTimeMillis() - startTime) + "ms");

					}
				}
			}
		} catch (CModelException e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

	private void outputReferences(IIndex index, IBinding b) throws CoreException {
		System.out.println("SampleHandler.outputReferences()");

		IIndexName[] names = index.findReferences(b);

		System.out.println("names " + names.length);

		for (IIndexName n : names) {

			outputReference(index, n);
		}
	}

	private void outputReference(IIndex index, IIndexName n) throws CoreException {
		IASTFileLocation fileLoc = n.getFileLocation();
		System.out.println(fileLoc.getFileName() + " at offset " + fileLoc.getNodeOffset() + ", "
				+ fileLoc.getStartingLineNumber());
	}

	private void printLines() {
		for (int i = 0; i < LINE_LENGTH; i++) {
			System.out.print("=");
		}
	}

	private Set<String> loadDeadToggleFlags() {
		Set<String> deadToggles = new HashSet<String>();
		deadToggles.add("--no-message-box");
		deadToggles.add("--enable-threaded-compositing");
		deadToggles.add("--disable-genius-app");
		deadToggles.add("--safebrowsing-disable-auto-update");
		deadToggles.add("--password-store");
		deadToggles.add("--start-maximized");

		return deadToggles;
	}

}
