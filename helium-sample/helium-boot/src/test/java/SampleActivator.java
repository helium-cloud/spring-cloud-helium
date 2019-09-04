import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Created by Coral on 6/6/15.
 */
public class SampleActivator implements BundleActivator {
	public void start(BundleContext context) throws Exception {
		System.out.println("Activator!!");
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye World!!");
	}
}
