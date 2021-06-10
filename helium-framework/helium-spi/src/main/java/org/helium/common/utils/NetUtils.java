
package org.helium.common.utils;

import org.helium.common.constants.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

/**
 * IP and Port Helper for RPC
 */
public class NetUtils {

    private static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    // returned port range is [30000, 39999]
    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;

    private static final int MAX_PORT = 65535;

    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static volatile InetAddress LOCAL_ADDRESS = null;

    private static final String SPLIT_IPV6_CHARACTER = ":";

    public static int getRandomPort() {
        return RND_PORT_START + ThreadLocalRandom.current().nextInt(RND_PORT_RANGE);
    }

    public static boolean isLocalHost(String host) {
        return host != null
                && (LOCAL_IP_PATTERN.matcher(host).matches()
                || host.equalsIgnoreCase(CommonConstants.LOCALHOST_KEY));
    }

    public static boolean isInvalidLocalHost(String host) {
        return host == null
                || host.length() == 0
                || host.equalsIgnoreCase(CommonConstants.LOCALHOST_KEY)
                || host.equals(CommonConstants.ANYHOST_VALUE)
                || host.startsWith("127.");
    }

    static boolean isValidV4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null
                && IP_PATTERN.matcher(name).matches()
                && !CommonConstants.ANYHOST_VALUE.equals(name)
                && !CommonConstants.LOCALHOST_VALUE.equals(name));
    }

    /**
     * Check if an ipv6 address
     *
     * @return true if it is reachable
     */
    static boolean isPreferIPV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    /**
     * normalize the ipv6 Address, convert scope name to scope id.
     * e.g.
     * convert
     * fe80:0:0:0:894:aeec:f37d:23e1%en0
     * to
     * fe80:0:0:0:894:aeec:f37d:23e1%5
     * <p>
     * The %5 after ipv6 address is called scope id.
     * see java doc of {@link Inet6Address} for more details.
     *
     * @param address the input address
     * @return the normalized address, with scope id converted to int
     */
    static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                // ignore
                logger.debug("Unknown IPV6 address: ", e);
            }
        }
        return address;
    }

    private static volatile String HOST_ADDRESS;

    /**
     * Find first valid IP from local network card
     *
     * @return first valid local IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static Optional<InetAddress> toValidAddress(InetAddress address) {
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            if (isPreferIPV6Address()) {
                return Optional.ofNullable(normalizeV6Address(v6Address));
            }
        }
        if (isValidV4Address(address)) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;

        // @since 2.7.6, choose the {@link NetworkInterface} first
        try {
            NetworkInterface networkInterface = findNetworkInterface();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                if (addressOp.isPresent()) {
                    try {
                        if (addressOp.get().isReachable(100)) {
                            return addressOp.get();
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("getLocalAddress0", e);
        }

        try {
            localAddress = InetAddress.getLocalHost();
            Optional<InetAddress> addressOp = toValidAddress(localAddress);
            if (addressOp.isPresent()) {
                return addressOp.get();
            }
        } catch (Throwable e) {
            logger.warn("getLocalAddress0", e);
        }


        return localAddress;
    }

    /**
     * @param networkInterface {@link NetworkInterface}
     * @return if the specified {@link NetworkInterface} should be ignored, return <code>true</code>
     * @throws SocketException SocketException if an I/O error occurs.
     * @since 2.7.6
     */
    private static boolean ignoreNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        return networkInterface == null
                || networkInterface.isLoopback()
                || networkInterface.isVirtual()
                || !networkInterface.isUp();
    }

    /**
     * Get the valid {@link NetworkInterface network interfaces}
     *
     * @return non-null
     * @throws SocketException SocketException if an I/O error occurs.
     * @since 2.7.6
     */
    private static List<NetworkInterface> getValidNetworkInterfaces() throws SocketException {
        List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (ignoreNetworkInterface(networkInterface)) { // ignore
                continue;
            }
            validNetworkInterfaces.add(networkInterface);
        }
        return validNetworkInterfaces;
    }

    /**
     * Is preferred {@link NetworkInterface} or not
     *
     * @param networkInterface {@link NetworkInterface}
     * @return if the name of the specified {@link NetworkInterface} matches
     * the property value from {@link CommonConstants#helium_PREFERRED_NETWORK_INTERFACE}, return <code>true</code>,
     * or <code>false</code>
     */
    public static boolean isPreferredNetworkInterface(NetworkInterface networkInterface) {
        String preferredNetworkInterface = System.getProperty(CommonConstants.helium_PREFERRED_NETWORK_INTERFACE);
        return Objects.equals(networkInterface.getDisplayName(), preferredNetworkInterface);
    }

    /**
     * Get the suitable {@link NetworkInterface}
     *
     * @return If no {@link NetworkInterface} is available , return <code>null</code>
     * @since 2.7.6
     */
    public static NetworkInterface findNetworkInterface() {

        List<NetworkInterface> validNetworkInterfaces = emptyList();
        try {
            validNetworkInterfaces = getValidNetworkInterfaces();
        } catch (Throwable e) {
            logger.warn("getLocalAddress0", e);
        }

        NetworkInterface result = null;

        // Try to find the preferred one
        for (NetworkInterface networkInterface : validNetworkInterfaces) {
            if (isPreferredNetworkInterface(networkInterface)) {
                result = networkInterface;
                break;
            }
        }

        if (result == null) { // If not found, try to get the first one
            for (NetworkInterface networkInterface : validNetworkInterfaces) {
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                    if (addressOp.isPresent()) {
                        try {
                            if (addressOp.get().isReachable(100)) {
                                result = networkInterface;
                                break;
                            }
                        } catch (IOException e) {
                            // ignore
                        }
                    }
                }
            }
        }

        if (result == null) {
            result = CollectionUtils.first(validNetworkInterfaces);
        }

        return result;
    }

    /**
     * @param hostName
     * @return ip address or hostName if UnknownHostException
     */
    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    public static void setInterface(MulticastSocket multicastSocket, boolean preferIpv6) throws IOException {
        boolean interfaceSet = false;
        for (NetworkInterface networkInterface : getValidNetworkInterfaces()) {
            Enumeration addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = (InetAddress) addresses.nextElement();
                if (preferIpv6 && address instanceof Inet6Address) {
                    try {
                        if (address.isReachable(100)) {
                            multicastSocket.setInterface(address);
                            interfaceSet = true;
                            break;
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                } else if (!preferIpv6 && address instanceof Inet4Address) {
                    try {
                        if (address.isReachable(100)) {
                            multicastSocket.setInterface(address);
                            interfaceSet = true;
                            break;
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
            if (interfaceSet) {
                break;
            }
        }
    }

//    public static boolean matchIpExpression(String pattern, String host, int port) throws UnknownHostException {
//
//        // if the pattern is subnet format, it will not be allowed to config port param in pattern.
//        if (pattern.contains("/")) {
//            CIDRUtils utils = new CIDRUtils(pattern);
//            return utils.isInRange(host);
//        }
//
//
//        return matchIpRange(pattern, host, port);
//    }

    private static boolean ipPatternContainExpression(String pattern) {
        return pattern.contains("*") || pattern.contains("-");
    }

}
