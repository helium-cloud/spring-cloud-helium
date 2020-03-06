package org.helium.redis.cluster;


public class RedisClusterItem implements Comparable {
	private int routeId;

	private String roleName = "";

	private String policy = "";

	private String siteName = "";

	private int nodeOrder;

	private String routeValue;

	private int weight;

	private int enabled;

	public boolean isEnabled() {
		return this.getEnabled() == 1;
	}

	public int getRouteId() {
		return this.routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getEnabled() {
		return this.enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}


	public int getNodeOrder() {
		return this.nodeOrder;
	}

	public void setNodeOrder(int nodeOrder) {
		this.nodeOrder = nodeOrder;
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getPolicy() {
		return this.policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRouteValue() {
		return routeValue;
	}

	public void setRouteValue(String routeValue) {
		this.routeValue = routeValue;
	}

	public RedisClusterItem() {
	}

	public RedisClusterItem(String zkkey, String zkvalue) {
	}

	public void paserJson(String zkkey, String zkValue) {
	}

	public RedisClusterItem(int routeId, String roleName, String policy, String siteName, int nodeOrder, String routeValue, int weight, int enabled) {
		this.routeId = routeId;
		this.roleName = roleName;
		this.policy = policy;
		this.siteName = siteName;
		this.nodeOrder = nodeOrder;
		this.routeValue = routeValue;
		this.weight = weight;
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && o instanceof RedisClusterItem) {
			RedisClusterItem that = (RedisClusterItem) o;
			return this.enabled != that.enabled ? false : (this.nodeOrder != that.nodeOrder ? false : (this.routeId != that.routeId ? false : (this.weight != that.weight ? false : (!this.policy.equals(that.policy) ? false : (!this.roleName.equals(that.roleName) ? false : (!this.routeValue.equals(that.routeValue) ? false : this.siteName.equals(that.siteName)))))));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = this.routeId;
		result = 31 * result + this.roleName.hashCode();
		result = 31 * result + this.policy.hashCode();
		result = 31 * result + this.siteName.hashCode();
		result = 31 * result + this.nodeOrder;
		result = 31 * result + this.routeValue.hashCode();
		result = 31 * result + this.weight;
		result = 31 * result + this.enabled;
		return result;
	}

	@Override
	public int compareTo(Object o) {
		if (o != null && o instanceof RedisClusterItem) {
			RedisClusterItem that = (RedisClusterItem) o;
			return this.getNodeOrder() - that.getNodeOrder();
		} else {
			return 0;
		}
	}
}
