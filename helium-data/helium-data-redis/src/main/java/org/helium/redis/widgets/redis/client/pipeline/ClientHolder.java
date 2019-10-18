package org.helium.redis.widgets.redis.client.pipeline;

import redis.clients.jedis.Client;

public class ClientHolder {

    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
