package org.helium.uek.es;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.helium.framework.annotations.FieldLoaderType;
import org.helium.uek.es.spi.EsClientLoader;

/**
 * es客户端
 */
@FieldLoaderType(loaderType = EsClientLoader.class)
public interface EsClient {
    /**
     * 创建索引
     *
     * @return
     */
    IndexResponse create(String index, String type, String id, String content);
    /**
     * 查询内容
     *
     * @return
     */
    GetResponse get(String index, String type, String id);

    /**
     * 查询内容
     *
     * @return
     */
    SearchResponse search(QueryBuilder queryBuilder);
	/**
	 * 分页查询1
	 *
	 *@Params:
	 */
	SearchResponse scrollSearch(QueryBuilder queryBuilder);

	/**
	 * 分页查询2
	 *
	 *@Params:
	 */
	SearchResponse scrollSearchPage(SearchResponse scrollResponse);
    /**
     * 删除索引
     *
     * @return
     */
    DeleteResponse delete(String index, String type, String id) ;

    /**
     * 删除索引
     *
     * @return
     */
    long deleteByTime(String index, String type, String time);

    /**
     * 更新文档
     *
     * @return
     */
    UpdateResponse update(String index, String type, String id);

	/**
	 * 获取原生客户端
	 * @return
	 */
	TransportClient getCient();
    /**
     * 关闭客户端
     *
     */
    void close();
}
