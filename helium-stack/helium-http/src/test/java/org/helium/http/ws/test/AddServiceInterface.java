package org.helium.http.ws.test;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by Coral on 8/19/15.
 */
@WebService(name = "AddService")
public interface AddServiceInterface {
	@WebMethod(operationName = "Add")
	int add(@WebParam(name = "value1") int value1, @WebParam(name = "value2") int value2);
}
