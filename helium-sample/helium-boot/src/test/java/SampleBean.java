import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Coral on 6/15/15.
 */
@ServiceInterface(id = "sample:SampleBean")
public interface SampleBean {
	String hello(String name);
}
