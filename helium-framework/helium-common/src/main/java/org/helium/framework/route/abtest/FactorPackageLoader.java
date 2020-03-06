package org.helium.framework.route.abtest;

import org.helium.framework.annotations.ServiceInterface;

import java.util.List;

/**
 * Created by Coral on 8/6/15.
 */
@ServiceInterface(id = "helium:FactorPackageLoader")
public interface FactorPackageLoader {
	List<String> loadPackage(String value);
}
