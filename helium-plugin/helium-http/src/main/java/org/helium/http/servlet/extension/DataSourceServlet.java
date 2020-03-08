package org.helium.http.servlet.extension;

import com.feinno.superpojo.SuperPojo;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

import java.util.List;

/**
 * Created by Lei Gao on 7/28/15.
 */
public abstract class DataSourceServlet<E extends SuperPojo> extends HttpServlet {
	@Override
	public void process(HttpServletContext ctx) throws Exception {
		List<E> datas = readData(ctx);
		StringBuilder str = new StringBuilder();
		str.append("[");

		for (int i = 0; i < datas.size(); i++) {
			if (i > 0) {
				str.append(",");
			}
			str.append(datas.get(i).toJsonObject());
		}
		str.append("]");

		ctx.getResponse().getOutputStream().write(str.toString().getBytes());
		ctx.getResponse().getOutputStream().close();
	}

	protected abstract List<E> readData(HttpServletContext context);
}
