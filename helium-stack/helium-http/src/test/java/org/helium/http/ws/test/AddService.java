package org.helium.http.ws.test;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name = "AddService")
public class AddService implements AddServiceInterface {
	@Override
	@WebMethod(operationName = "Add")
	public int add(@WebParam(name = "value1") int value1, @WebParam(name = "value2") int value2) {
		return value1 + value2;
	}
}
