package org.helium.http.test;

import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

/**
 * Created by Coral on 7/28/15.
 */
@ServletImplementation(id = "sample:HttpJson")
@HttpMappings(contextPath = "/sample", urlPattern="/beans.json")
public class HttpDataJsonServlet extends HttpServlet {
	@Override
	public void process(HttpServletContext ctx) throws Exception {
		StringBuilder str = new StringBuilder();
		str.append("[");

		int i = 0;
		for (BeanContext bc: BeanContext.getContextService().getBeans()) {
			if (i > 0) {
				str.append(",");
			}
			i++;
			BeanJson json = new BeanJson();
			json.setId(bc.getId().toString());
			json.setType(bc.getType().toString());
			json.setState(bc.getState().toString());
			str.append(json.toString());

		}
		str.append("]");

		ctx.getResponse().getOutputStream().write(str.toString().getBytes());
		ctx.getResponse().getOutputStream().close();
	}

}
