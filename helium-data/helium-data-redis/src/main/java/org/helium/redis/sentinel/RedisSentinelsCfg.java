package org.helium.redis.sentinel;

/**
 * 更改这个表, 去掉了Site 等逻辑, 保留权重
 * RedisSentinelsCfg 的配置表
 * <p>
 * <pre>
 *  表名称：RedisSentinelsCfg <br>
 *     <table  border="0" cellspacing="0" cellpadding="0" width="497">
 *         <tr>
 *             <td width="144">id</td>
 *             <td width="143">int</td>
 *             <td width="211">主键, 自动递增</td>
 *         </tr>
 *         <tr>
 *             <td>  RoleName</td>
 *             <td>  varchar(32)</td>
 *             <td>  业务名称</td>
 *         </tr>
 *         <tr>
 *             <td>  MasterName</td>
 *             <td>  varchar(64) </td>
 *             <td>>sentinel&nbsp;master名称</td>
 *         <tr>
 *             <td>  Policy </td>
 *             <td>  varchar(32)  </td>
 *             <td>  路由策略 Hash  </td>
 *         </tr>
 *         <tr>
 *             <td>  NoderOrder  </td>
 *             <td>  int  </td>
 *             <td>  节点排序 </td>
 *         </tr>
 *         <tr >
 *             <td> Addrs  </td>
 *             <td>  varchar(256)  </td>
 *             <td>  地址列表,多个地址用分号分割.</td>
 *         </tr>
 *         <tr>
 *             <td>  PropertiesExt  </td>
 *             <td>  varchar(512)  </td>'
 *             <td>  扩展配置 , 采用Properties格式,按行分割,区分大小写.&nbsp;</td>
 *         </tr>
 *         <tr >
 *             <td> Weight  </td>
 *             <td>  int  </td>
 *             <td>  权重  </td>
 *         </tr>
 *         <tr>
 *             <td>  Enabled  </td>
 *             <td>  tinyint  </td>
 *             <td> 配置状态 .&nbsp;<br>1：可用 &nbsp;<br>0：不可用 &nbsp;</td>
 *         </tr>
 *     </table>
 * </pre>
 *
 * @author Li.Hongbo <lihongbo@feinno.com>
 */
public class RedisSentinelsCfg implements Comparable {

    private int id;// id, 仅仅是个id. 用来做个主键什么的.

    private String roleName = "";// 角色名称, 用来区分业务

    private String masterName = "";// 同一个业务角色的同一个分组下, master 应该只有1个..

    private String policy = "";

    private int nodeOrder;

    private String addrs = "";

    private String propertiesExt = "";

    private int weight;

    private int enabled;

	private boolean testWhileIdle;

	private boolean testOnBorrow;


	public RedisSentinelsCfg() {

    }

    public int getId() {
        return id;
    }

    public RedisSentinelsCfg setId(int id) {
        this.id = id;
        return this;
    }


    public String getRoleName() {
        return roleName;
    }

    public RedisSentinelsCfg setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    public String getMasterName() {
        return masterName;
    }

    public RedisSentinelsCfg setMasterName(String masterName) {
        this.masterName = masterName;
        return this;
    }

    public String getPolicy() {
        return policy;
    }

    public RedisSentinelsCfg setPolicy(String policy) {
        this.policy = policy;
        return this;
    }

    public int getNodeOrder() {
        return nodeOrder;
    }

    public RedisSentinelsCfg setNodeOrder(int nodeOrder) {
        this.nodeOrder = nodeOrder;
        return this;
    }

    public String getAddrs() {
        return addrs;
    }

    public RedisSentinelsCfg setAddrs(String addrs) {
        this.addrs = addrs;
        return this;
    }

    public String getPropertiesExt() {
        return propertiesExt;
    }

    public RedisSentinelsCfg setPropertiesExt(String propertiesExt) {
        this.propertiesExt = propertiesExt;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public RedisSentinelsCfg setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public int getEnabled() {
        return enabled;
    }

    public RedisSentinelsCfg setEnabled(int enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}

        if (o == null || !(o instanceof RedisSentinelsCfg)){
			return false;
		}


        RedisSentinelsCfg that = (RedisSentinelsCfg) o;

        if (enabled != that.enabled){
			return false;
		}

        if (nodeOrder != that.nodeOrder){
			return false;
		}

        if (id != that.id){
			return false;
		}

        if (weight != that.weight){
			return false;
		}

        if (!policy.equals(that.policy)){
			return false;
		}

        if (!roleName.equals(that.roleName)){
			return false;
		}

        if (!addrs.equals(that.addrs)){
			return false;
		}
        return true;
    }


    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + roleName.hashCode();
        result = 31 * result + policy.hashCode();
        result = 31 * result + nodeOrder;
        result = 31 * result + addrs.hashCode();
        result = 31 * result + weight;
        result = 31 * result + enabled;
        return result;
    }


    @Override
    public int compareTo(Object o) {

        if (o == null || !(o instanceof RedisSentinelsCfg)){
			return 0;
		}
        RedisSentinelsCfg other = (RedisSentinelsCfg) o;
        return this.getNodeOrder() - other.getNodeOrder();
    }


    public boolean isEnabled() {
        return 1 == getEnabled();
    }

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
}
