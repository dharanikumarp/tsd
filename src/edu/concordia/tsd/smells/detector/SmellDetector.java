package edu.concordia.tsd.smells.detector;

import java.util.Set;
import edu.concordia.tsd.smells.ToggleSmell;

/**
 * @author dharani kumar palani (d_palan@encs.concordia.ca)
 */
public interface SmellDetector {

	public void detectSmells();
	
	public Set<ToggleSmell> getAllDetectedSmells();
}
