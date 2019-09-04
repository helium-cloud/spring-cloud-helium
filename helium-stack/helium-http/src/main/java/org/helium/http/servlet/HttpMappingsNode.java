package org.helium.http.servlet;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import org.helium.util.XmlUtils;
import org.helium.framework.entitys.ServletMappingsNode;

/**
 * Created by Coral on 5/11/15.
 * TODO: mapping & more xmlentity
 */
@Entity(name = "httpMappings")
public class HttpMappingsNode extends SuperPojo {
	@Field(id = 1, name = "contextPath", type = NodeType.NODE)
	private String contextPath;

	@Field(id = 2, name = "urlPattern", type = NodeType.NODE)
	private String urlPattern;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public static ServletMappingsNode createMappingsNode(String contextPath, String urlPattern) {
		StringBuilder xml = new StringBuilder();
		xml.append("<" + ServletMappingsNode.NAME + " protocol=\"http\">");
		xml.append("<httpMappings>");
		xml.append("<contextPath>");
		xml.append(XmlUtils.encode(contextPath));
		xml.append("</contextPath><urlPattern>");
		xml.append(XmlUtils.encode(urlPattern));
		xml.append("</urlPattern>");
		xml.append("</httpMappings>");
		xml.append("</" + ServletMappingsNode.NAME + ">");

		String xmlText = xml.toString();
		ServletMappingsNode node = new ServletMappingsNode();
		node.parseXmlFrom(xmlText);
		return node;
	}
}
