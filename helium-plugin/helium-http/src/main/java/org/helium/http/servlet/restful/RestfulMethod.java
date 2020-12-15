package org.helium.http.servlet.restful;

/**
 * Created by Gao Lei on 1/12/17.
 */
public enum RestfulMethod {
	GET,
	PUT,
	POST,
	DELETE,
	;
	
	public static RestfulMethod fromName(String method) {
		switch (method) {
			case "GET":
				return GET;
			case "PUT":
				return PUT;
			case "POST":
				return POST;
			case "DELETE":
				return DELETE;
		}
		return null; 
	} 
}
