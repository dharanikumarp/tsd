package tsd.handlers;

import java.util.Set;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.concordia.tsd.smells.ToggleSmell;
import edu.concordia.tsd.smells.combtoggle.CombinatorialToggleSmellDetector;
import edu.concordia.tsd.smells.deadtoggle.DeadToggleSmellDetector;
import edu.concordia.tsd.smells.detector.IToggleSmellDetector;
import edu.concordia.tsd.smells.nestedtoggle.NestedToggleSmellDetector;
import edu.concordia.tsd.smells.spreadtoggle.ToggleSpreadSmellDetector;

/**
 * Toggle Smell Detector handler extends AbstractHandler, an IHandler base
 * class.
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

			allProjects = CoreModel.getDefault().getCModel().getCProjects();
			IToggleSmellDetector[] sds = new IToggleSmellDetector[] {

					new ToggleSpreadSmellDetector(),

					new NestedToggleSmellDetector(),

					new CombinatorialToggleSmellDetector(),

					new DeadToggleSmellDetector() };

			for (ICProject icProject : allProjects) {
				System.out.println("icProject " + icProject.getElementName());

				if (selectedProject.toString().contains(icProject.getElementName())) {

					for (IToggleSmellDetector smellDetector : sds) {

						long startTime = System.currentTimeMillis();

						Set<ToggleSmell> allDetectedSmells = smellDetector.getToggleSmells(icProject,
								TOGGLE_METHOD_NAME);
						System.out.println("Smell Detector type " + smellDetector.getDetectorType());
						System.out.println("Number of smells detected " + allDetectedSmells.size());
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
}
