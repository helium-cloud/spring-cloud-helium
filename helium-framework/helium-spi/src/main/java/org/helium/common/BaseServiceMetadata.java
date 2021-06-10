
package org.helium.common;

import org.helium.common.utils.SPIStringUtils;
import org.helium.common.constants.CommonConstants;

/**
 * 2019-10-10
 */
public class BaseServiceMetadata {
    public static final char COLON_SEPARATOR = ':';

    protected String serviceKey;
    protected String serviceInterfaceName;
    protected String version;
    protected volatile String group;

    public static String buildServiceKey(String path, String group, String version) {
        StringBuilder buf = new StringBuilder();
        if (group != null && group.length() > 0) {
            buf.append(group).append("/");
        }
        buf.append(path);
        if (version != null && version.length() > 0) {
            buf.append(":").append(version);
        }
        return buf.toString();
    }

    public static String versionFromServiceKey(String serviceKey) {
        int index = serviceKey.indexOf(":");
        if (index == -1) {
            return CommonConstants.DEFAULT_VERSION;
        }
        return serviceKey.substring(index + 1);
    }

    public static String groupFromServiceKey(String serviceKey) {
        int index = serviceKey.indexOf("/");
        if (index == -1) {
            return null;
        }
        return serviceKey.substring(0, index);
    }

    public static String interfaceFromServiceKey(String serviceKey) {
        int groupIndex = serviceKey.indexOf("/");
        int versionIndex = serviceKey.indexOf(":");
        groupIndex = (groupIndex == -1) ? 0 : groupIndex + 1;
        versionIndex = (versionIndex == -1) ? serviceKey.length() : versionIndex;
        return serviceKey.substring(groupIndex, versionIndex);
    }

    /**
     * Format : interface:version
     *
     * @return
     */
    public String getDisplayServiceKey() {
        StringBuilder serviceNameBuilder = new StringBuilder();
        serviceNameBuilder.append(serviceInterfaceName);
        serviceNameBuilder.append(COLON_SEPARATOR).append(version);
        return serviceNameBuilder.toString();
    }

    /**
     * revert of org.helium.common.ServiceDescriptor#getDisplayServiceKey()
     *
     * @param displayKey
     * @return
     */
    public static BaseServiceMetadata revertDisplayServiceKey(String displayKey) {
        String[] eles = SPIStringUtils.split(displayKey, COLON_SEPARATOR);
        if (eles == null || eles.length < 1 || eles.length > 2) {
            return new BaseServiceMetadata();
        }
        BaseServiceMetadata serviceDescriptor = new BaseServiceMetadata();
        serviceDescriptor.setServiceInterfaceName(eles[0]);
        if (eles.length == 2) {
            serviceDescriptor.setVersion(eles[1]);
        }
        return serviceDescriptor;
    }

    public String getServiceKey() {
        return serviceKey;
    }

    public void generateServiceKey() {
        this.serviceKey = buildServiceKey(serviceInterfaceName, group, version);
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public void setServiceInterfaceName(String serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
