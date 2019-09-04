package com.helium.kafka.test.uk;


import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.helium.uek.es.EsClient;
import org.helium.uek.es.spi.EsClientManager;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by wuhao on 15/05/17.
 */
public class EsClientTest {
    static EsClient esClient = null;
    static {
        String path = "helium-data-elasticsearch/src/test/resources/esclient.properties";
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        esClient = EsClientManager.INSTANCE.getEsClient("esclient", properties);
    }
    public static void main(String[] args) throws Exception {
        doSearchAll();
    }

    public static void doCreate() throws Exception {
        String index = "ott";
        String type = "bill_log";
        String content = "{\n" +
				"    \"total\": 12345678790,\n" +
				"    \"max_score\": null"+
				"  }";
		System.out.println("xxx1");
		String xxxx =UUID.randomUUID().toString();
        esClient.create(index, type , xxxx, content);

		System.out.println("xxx2" + xxxx);
    }



    public static void doSearchAll() throws Exception {

        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("platform", "ott");
        SearchResponse response = esClient.search(queryBuilder);
        System.out.println(response.getHits());
    }
}
