package org.helium.framework.route.center.entity;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Field;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.BundleConfiguration;
import org.helium.framework.entitys.FactorGroupNode;
import org.helium.framework.route.ServerEndpoint;

import java.util.List;

/**
 * Created by Coral on 8/4/15.
 */
public class GrayBundleNode extends SuperPojo {
	@Field(id = 1)
	private String bundleName;

	@Field(id = 2)
	private String bundleVersion;

	@Field(id = 3)
	private BundleConfiguration configuration;

	@Field(id = 4)
	private FactorGroupNode grayFactors;

	@Childs(id = 11)
	private List<BeanConfiguration> beans;

	@Childs(id = 12)
	private ServerEndpoint serverEndpoint;

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getBundleVersion() {
		return bundleVersion;
	}

	public void setBundleVersion(String bundleVersion) {
		this.bundleVersion = bundleVersion;
	}

	public BundleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(BundleConfiguration configuration) {
		this.configuration = configuration;
	}

	public FactorGroupNode getGrayFactors() {
		return grayFactors;
	}

	public void setGrayFactors(FactorGroupNode grayFactors) {
		this.grayFactors = grayFactors;
	}

	public List<BeanConfiguration> getBeans() {
		return beans;
	}

	public void setBeans(List<BeanConfiguration> beans) {
		this.beans = beans;
	}

	public String getPath(String serverId) {
		return bundleName + "#" + bundleVersion + "#" + serverId;
	}

	public ServerEndpoint getServerEndpoint() {
		return serverEndpoint;
	}

	public GrayBundleNode setServerEndpoint(ServerEndpoint serverEndpoint) {
		this.serverEndpoint = serverEndpoint;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GrayBundleNode that = (GrayBundleNode) o;

		if (bundleName != null ? !bundleName.equals(that.bundleName) : that.bundleName != null) return false;
		if (bundleVersion != null ? !bundleVersion.equals(that.bundleVersion) : that.bundleVersion != null)
			return false;
		return !(serverEndpoint != null ? !serverEndpoint.equals(that.serverEndpoint) : that.serverEndpoint != null);

	}

	@Override
	public int hashCode() {
		int result = bundleName != null ? bundleName.hashCode() : 0;
		result = 31 * result + (bundleVersion != null ? bundleVersion.hashCode() : 0);
		result = 31 * result + (serverEndpoint != null ? serverEndpoint.hashCode() : 0);
		return result;
	}
}
