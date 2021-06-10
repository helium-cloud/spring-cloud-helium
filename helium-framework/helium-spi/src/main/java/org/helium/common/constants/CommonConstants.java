

package org.helium.common.constants;


import java.net.NetworkInterface;
import java.util.Properties;
import java.util.regex.Pattern;

public interface CommonConstants {

    String helium_PROPERTIES_KEY = "helium.properties.file";

    String DEFAULT_helium_PROPERTIES = "helium.properties";

    /**
     * @since 2.7.8
     */
    char COMMA_SEPARATOR_CHAR = ',';

    String COMMA_SEPARATOR = ",";

    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");


    String PROTOCOL_KEY = "protocol";

    String TIMEOUT_KEY = "timeout";

    String REMOVE_VALUE_PREFIX = "-";

    String UNDERLINE_SEPARATOR = "_";

    String SEPARATOR_REGEX = "_|-";

    String HIDDEN_KEY_PREFIX = ".";

    String DOT_REGEX = "\\.";

    String DEFAULT_KEY_PREFIX = "default.";

    String DEFAULT_KEY = "default";

    String ANYHOST_KEY = "anyhost";

    String ANYHOST_VALUE = "0.0.0.0";

    String LOCALHOST_KEY = "localhost";

    String LOCALHOST_VALUE = "127.0.0.1";

    String METHODS_KEY = "methods";

    String TIMESTAMP_KEY = "timestamp";

    String GROUP_KEY = "group";

    String PATH_KEY = "path";

    String INTERFACE_KEY = "interface";

    String VERSION_KEY = "version";

    /**
     * The composite metadata storage type includes {@link #DEFAULT_METADATA_STORAGE_TYPE "local"} and {@link
     * #REMOTE_METADATA_STORAGE_TYPE "remote"}.
     *
     * @since 2.7.8
     */
    String COMPOSITE_METADATA_STORAGE_TYPE = "composite";

    String USERNAME_KEY = "username";
    String PASSWORD_KEY = "password";
    String HOST_KEY = "host";
    String PORT_KEY = "port";

    /**
     * The property name for {@link NetworkInterface#getDisplayName() the name of network interface} that the helium
     * application prefers
     *
     * @since 2.7.6
     */
    String helium_PREFERRED_NETWORK_INTERFACE = "helium.network.interface.preferred";

    @Deprecated
    String SHUTDOWN_WAIT_SECONDS_KEY = "helium.service.shutdown.wait.seconds";

    /**
     * The parameter key for the class path of the ServiceNameMapping {@link Properties} file
     *
     * @since 2.7.8
     */
    String SERVICE_NAME_MAPPING_PROPERTIES_FILE_KEY = "service-name-mapping.properties-path";

    /**
     * The default class path of the ServiceNameMapping {@link Properties} file
     *
     * @since 2.7.8
     */
    String DEFAULT_SERVICE_NAME_MAPPING_PROPERTIES_PATH = "META-INF/helium/service-name-mapping.properties";

    String DEFAULT_VERSION = "0.0.0";


}
