package org.helium.uek.es.spi;


import com.feinno.superpojo.util.StringUtils;
import com.floragunn.searchguard.SearchGuardPlugin;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.helium.uek.es.EsClient;
import org.helium.uek.es.ext.ExtDeleteByQueryRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Created by wuhao on 2017/5/13.
 */
public class EsClientImpl implements EsClient {
	private TransportClient client;
	private static final Logger LOGGER = LoggerFactory.getLogger(EsClientImpl.class);

	public EsClientImpl(Properties props) {
		try {

			//设置
			Settings settings = null;
			Settings.Builder builder= null;
			Set<String> configValues = props.stringPropertyNames();
			Iterator iterator = configValues.iterator();
			if (iterator.hasNext()){
				builder = Settings.builder();
			}
			boolean searchguard_enable = false;
			while (iterator.hasNext()){
				String name = (String) iterator.next();
				String value = props.getProperty(name, "");
				LOGGER.info("es config key:{}:{}", name, value);
				if (name.equals("server")){
					//设置配置直接跳过
					continue;
				}
				//是否开启searchguard_enable
				if (name.equals("searchguard.ssl.transport.enabled")){
					if (!StringUtils.isNullOrEmpty(value)){
						searchguard_enable = Boolean.parseBoolean(value);
					}
				}
				if (!StringUtils.isNullOrEmpty(value)){
					builder.put(name, value);
				}
			}
			if (builder == null){
				settings = Settings.EMPTY;
			} else {

				settings = builder.build();
			}
			//开启searchguard
			if (searchguard_enable){
				client = new PreBuiltTransportClient(settings, SearchGuardPlugin.class);
			} else {
				client = new PreBuiltTransportClient(settings);
			}
			//设置server地址
			String serverStr = props.getProperty("server", "127.0.0.1:9300");
			LOGGER.info("init es start [{}]", serverStr);
			String[] serverArray = serverStr.split(",");
			for (String serverItem : serverArray) {
				String[] serverItemArray = serverItem.split(":");
				String ip = serverItemArray[0];
				int port = Integer.parseInt(serverItemArray[1]);
				TransportAddress addr = new TransportAddress(InetAddress.getByName(ip), port);
				client.addTransportAddress(addr);
			}


			LOGGER.info("init es end [{}]", serverStr);
		} catch (Exception e) {
			LOGGER.error("es init exception", e);
		}

	}

	/**
	 * 创建索引
	 *
	 * @return
	 */
	@Override
	public IndexResponse create(String index, String type, String id, String content) {
		IndexResponse response = null;
		try {
			LOGGER.info("create start.index:{} type:{} id:{}", index, type, id);
			response = client.prepareIndex(index, type, id).setSource(content, XContentType.JSON).get();
			LOGGER.info("create end.index:{} type:{} id:{}", index, type, id);
		} catch (Exception e) {
			LOGGER.error("es create exception:id{}", id, e);
		}
		return response;
	}

	/**
	 * 查询内容
	 *
	 * @return
	 */
	@Override
	public GetResponse get(String index, String type, String id) {
		GetResponse response = null;
		try {
			LOGGER.info("get start.index:{} type:{} id:{}", index, type, id);
			response = client.prepareGet(index, type, id)
					.setOperationThreaded(false)
					.get();
			LOGGER.info("get end.index:{} type:{} id:{}", index, type, id);
		} catch (Exception e) {
			LOGGER.error("es get exception:id{}", id, e);
		}
		return response;
	}

	/**
	 * 查询内容
	 *
	 * @return
	 */
	@Override
	public SearchResponse search(QueryBuilder queryBuilder) {
		SearchResponse response = null;
		try {
			LOGGER.info("create start.content:{}", queryBuilder);
			response = client.prepareSearch().setQuery(queryBuilder).get();

			LOGGER.info("create end.content:{}", queryBuilder);
		} catch (Exception e) {
			LOGGER.error("es create exception:id{}", queryBuilder, e);
		}
		return response;
	}
	/*
	* 分页查询
	* 注意！ 第一次不返回数据（谨慎使用，否则会丢前十）
			*@Params:
			*/
	@Override
	public SearchResponse scrollSearch(QueryBuilder queryBuilder) {
		SearchResponse scrollResponse = null;
		try {
			LOGGER.info("scrollSearch start.content:{}", queryBuilder);
			scrollResponse = client.prepareSearch().setQuery(queryBuilder)
					.setSearchType(SearchType.DEFAULT).setSize(10).setScroll(TimeValue.timeValueMinutes(1))
					.execute().actionGet();
			LOGGER.info("scrollSearch end.content:{}", queryBuilder);
		} catch (Exception e) {
			LOGGER.error("scrollSearch error exception:id{}", queryBuilder, e);
		}
		return scrollResponse;
	}

	/**
	 * 进行每页查询
	 * 并返回数据
	 *@Params:
	 */
	@Override
	public SearchResponse scrollSearchPage(SearchResponse scrollResponse) {
		try {
			LOGGER.info("scrollSearchPage start");
			scrollResponse = client.prepareSearchScroll(scrollResponse.getScrollId())
					.setScroll(new TimeValue(20000))
					.execute().actionGet();
			LOGGER.info("scrollSearchPage end");
		} catch (Exception e) {
			LOGGER.error("scrollSearchPage error exception:{}", e);
		}
		return scrollResponse;
	}
	/**
	 * 删除索引
	 *
	 * @return
	 */
	@Override
	public DeleteResponse delete(String index, String type, String id) {
		DeleteResponse response = null;
		try {
			LOGGER.info("delete start.index:{} type:{} id:{}", index, type, id);
			response = client.prepareDelete(index, type, id).get();
			LOGGER.info("delete end.index:{} type:{} id:{}", index, type, id);
		} catch (Exception e) {
			LOGGER.error("es delete exception:id{}", id, e);
		}
		return response;
	}


	/**
	 * 删除索引
	 *
	 * @return
	 */
	@Override
	public long deleteByTime(String index, String type, String time) {
		try {
			//gt :: 大于
			//gte:: 大于等于
			//lt :: 小于
			//lte:: 小于等于
			LOGGER.info("delete start. time:{}", time);

			QueryBuilder queryBuilder = QueryBuilders.rangeQuery("time").lt(time);

			//支持最小index type 按时间删除
			ExtDeleteByQueryRequestBuilder builder = new ExtDeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE);
			builder.setType(type);
			builder.source(index);
			builder.filter(queryBuilder);
			BulkByScrollResponse response = builder.get();
			long deleted = response.getDeleted();

			//支持index按日期删除
//            DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
//                    .filter(queryBuilder)
//                    .source(index);
//
//            BulkByScrollResponse response = builder.get();
//            long deleted = response.getDeleted();


			LOGGER.info("delete end. time:{}->{}", time, deleted);
			return deleted;
		} catch (Exception e) {
			LOGGER.error("es delete exception. time:{}", time, e);
		}
		return 0;
	}

	/**
	 * 更新文档
	 *
	 * @return
	 */
	@Override
	public UpdateResponse update(String index, String type, String id) {
		UpdateResponse response = null;
		try {
			LOGGER.info("delete start.index:{} type:{} id:{}", index, type, id);
			response = client.prepareUpdate(index, type, id).get();
			LOGGER.info("delete end.index:{} type:{} id:{}", index, type, id);
		} catch (Exception e) {
			LOGGER.error("es update exception:{}", id, e);
		}
		return response;
	}

	@Override
	public TransportClient getCient() {
		return client;
	}

	/**
	 * 关闭客户端
	 */
	@Override
	public void close() {
		try {
			client.close();
		} catch (Exception e) {
			LOGGER.error("close es client exception", e);
		}

	}



}
