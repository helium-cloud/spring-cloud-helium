package org.helium.http.servlet.extension;

import org.helium.util.Outer;
import org.helium.util.StringUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.helium.framework.tag.Initializer;
import org.helium.http.servlet.HttpServlet;
import org.helium.http.servlet.HttpServletContext;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Created by Coral on 7/28/15.
 */
public abstract class TemplateServlet extends HttpServlet {
	private Configuration configuration;
	private Template template;

	@Initializer
	public void initialize() throws IOException {
		configuration = new Configuration(Configuration.VERSION_2_3_22);
		TemplateHtml a = this.getClass().getAnnotation(TemplateHtml.class);
		if (a == null) {
			throw new IllegalArgumentException("Need @TemplateHtml for:" + this.getClass().getName());
		}
		Outer<String> directory = new Outer<>();
		Outer<String> file = new Outer<>();
		StringUtils.splitWithLast(a.value(), "/",  directory, file);

		if (a.fromClassResource()) {
			configuration.setClassForTemplateLoading(this.getClass(), directory.value());
		} else {
			configuration.setDirectoryForTemplateLoading(new File(directory.value()));
		}
		template = configuration.getTemplate(file.value());
	}

	@Override
	public void process(HttpServletContext ctx) throws Exception {
		OutputStreamWriter writer = new OutputStreamWriter(ctx.getResponse().getOutputStream());
		template.process(getValues(), writer);
		ctx.getResponse().getOutputStream().close();
	}

	public abstract  Map<String, String> getValues();
}
