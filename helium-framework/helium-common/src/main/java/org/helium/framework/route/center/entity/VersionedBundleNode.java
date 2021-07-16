package org.helium.framework.route.center.entity;


import org.helium.superpojo.SuperPojo;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.route.ServerEndpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * $ROOT/{BundleId#VersionId}
 * equals方法并不比对Beans
 * Created by coral
 */
public class VersionedBundleNode extends SuperPojo {

	private String bundleName;

	private String bundleVersion;

	private List<BeanConfiguration> beans = new ArrayList<>();

	private ServerEndpoint serverEndpoint;

	public void addBean(BeanConfiguration bc) {
		beans.add(bc);
	}

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

	public List<BeanConfiguration> getBeans() {
		return beans;
	}

	public void setBeans(List<BeanConfiguration> beans) {
		this.beans = beans;
	}

	public ServerEndpoint getServerEndpoint() {
		return serverEndpoint;
	}

	public void setServerEndpoint(ServerEndpoint serverEndpoint) {
		this.serverEndpoint = serverEndpoint;
	}

	public String getEndpointPath() {
		return bundleName + "#" + bundleVersion;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VersionedBundleNode that = (VersionedBundleNode) o;

		if (bundleName != null ? !bundleName.equals(that.bundleName) : that.bundleName != null) return false;
		return !(bundleVersion != null ? !bundleVersion.equals(that.bundleVersion) : that.bundleVersion != null);

	}

	@Override
	public int hashCode() {
		int result = bundleName != null ? bundleName.hashCode() : 0;
		result = 31 * result + (bundleVersion != null ? bundleVersion.hashCode() : 0);
		return result;
	}
}
