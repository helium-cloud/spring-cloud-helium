package org.helium.uek.es.ext;

import org.elasticsearch.action.Action;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;

/**
 * Created by wuhao on 15/05/17.
 */
public class ExtDeleteByQueryRequestBuilder extends DeleteByQueryRequestBuilder{
    public ExtDeleteByQueryRequestBuilder(ElasticsearchClient client, Action<DeleteByQueryRequest, BulkByScrollResponse, DeleteByQueryRequestBuilder> action) {
        super(client, action);
    }
    public DeleteByQueryRequestBuilder setType(String type){
        request.types(type);
        return this;
    }

}
