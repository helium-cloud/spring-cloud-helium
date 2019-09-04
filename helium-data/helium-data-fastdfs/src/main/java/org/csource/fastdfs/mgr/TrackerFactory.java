package org.csource.fastdfs.mgr;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.csource.fastdfs.TrackerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackerFactory extends BasePoolableObjectFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackerFactory.class);

    private final String host;
    private final int port;
    private final int timeout;

    public TrackerFactory(String host, int port, int timeout) {
        super();
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public Object makeObject() throws Exception {
        LOGGER.info("makeObject()...");
        TrackerClient trackerClient = new TrackerClient(host, port, timeout);
        trackerClient.connect();
        return trackerClient;
    }

    @Override
    public void destroyObject(Object obj) throws Exception {
        LOGGER.info("destroyObject()...");
        if (obj instanceof TrackerClient) {
            final TrackerClient trackerClient = (TrackerClient) obj;
            if (trackerClient.getConnection().isConnected()) {
                try {
                    trackerClient.getConnection().close();
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public boolean validateObject(Object obj) {
        LOGGER.info("validateObject()...");
        if (obj instanceof TrackerClient) {
            final TrackerClient trackerClient = (TrackerClient) obj;
            try {
                boolean tag1 = trackerClient.getConnection().isConnected();
                if (!tag1) {
                    LOGGER.error("isConnected() false");
                    return false;
                }
                boolean tag2 = trackerClient.getConnection().ping();
                if (!tag2) {
                    LOGGER.error("ping() false");
                    return false;
                }
                return true;
            } catch (Exception e) {
                LOGGER.error("validate tracker error.", e);
                return false;
            }

        } else {
            LOGGER.error("obj is not TrackerClient");
            return false;
        }
    }
}
