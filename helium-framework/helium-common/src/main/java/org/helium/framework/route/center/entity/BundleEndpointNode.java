package org.helium.framework.route.center.entity;


import org.helium.superpojo.SuperPojo;
import org.helium.framework.route.ServerEndpoint;

/**
 * Created by Coral on 8/4/15.
 */
public class BundleEndpointNode extends SuperPojo {

	private String bundleName;

	private String bundleVersion;

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

	public String getEndpointPath() {
		return bundleName + "#" + bundleVersion;
	}

	public ServerEndpoint getServerEndpoint() {
		return serverEndpoint;
	}

	public BundleEndpointNode setServerEndpoint(ServerEndpoint serverEndpoint) {
		this.serverEndpoint = serverEndpoint;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		BundleEndpointNode that = (BundleEndpointNode) o;

		if (bundleName != null ? !bundleName.equals(that.bundleName) : that.bundleName != null) {
			return false;
		}
		if (bundleVersion != null ? !bundleVersion.equals(that.bundleVersion) : that.bundleVersion != null) {
			return false;
		}
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
