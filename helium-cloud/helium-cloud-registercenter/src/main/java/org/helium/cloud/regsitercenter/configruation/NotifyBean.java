package org.helium.cloud.regsitercenter.configruation;

/**
 * 类描述：NotifyBean
 *
 * @author zkailiang
 * @date 2020/4/23
 */
public class NotifyBean {
	private NotifyStat stat;
	private String url;
	private String id;
	private String ifc;

	public String getUrl() {
		return url;
	}

	public NotifyBean setUrl(String url) {
		this.url = url;
		return this;
	}

	public NotifyStat getStat() {
		return stat;
	}

	public NotifyBean setStat(NotifyStat stat) {
		this.stat = stat;
		return this;
	}

	public String getId() {
		return id;
	}

	public NotifyBean setId(String id) {
		this.id = id;
		return this;
	}

	public String getIfc() {
		return ifc;
	}

	public NotifyBean setIfc(String ifc) {
		this.ifc = ifc;
		return this;
	}
}
