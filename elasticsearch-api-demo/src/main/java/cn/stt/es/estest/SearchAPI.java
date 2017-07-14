package cn.stt.es.estest;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/14.
 */
public class SearchAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentAPIs.class);
    private TransportClient client;

    @Before
    public void startUp() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")  //设置连接的集群名
                .put("client.transport.sniff", true)    //开启嗅探
                .put("client.transport.ignore_cluster_name", true) // 忽略集群名字验证, 打开后集群名字不对也能连接上
                .build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.182"), 9300));
    }

    /**
     * 关闭连接
     */
    @After
    public void shutdown() {
        client.close();
    }

    @Test
    public void testSearch() {
        SearchResponse response = client.prepareSearch("twitter", "system_log_index")
                .setTypes("tweet", "")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(termQuery("client_ip", "116.226.37.176"))                 // Query
                .setPostFilter(QueryBuilders.rangeQuery("id").from(12).to(18))     // Filter
                .setFrom(0).setSize(60).setExplain(true)
                .get();
        LOGGER.info(response.toString());
    }

    @Test
    public void testScrollSearch() {
        QueryBuilder qb = termQuery("client_ip", "116.226.37.176");
        SearchResponse scrollResp = client.prepareSearch("system_log_index")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(100).get(); //max of 100 hits will be returned for each scroll
        //Scroll until no hits are returned
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                //Handle the hit...
                LOGGER.info(hit.toString());
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            LOGGER.info(scrollResp.toString());
        }
        while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
    }

    @Test
    public void testMultiSearch() {
        SearchRequestBuilder srb1 = client
                .prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
        SearchRequestBuilder srb2 = client
                .prepareSearch().setQuery(QueryBuilders.matchQuery("name", "kimchy")).setSize(1);

        MultiSearchResponse sr = client.prepareMultiSearch()
                .add(srb1)
                .add(srb2)
                .get();

        // You will get all individual responses from MultiSearchResponse#getResponses()
        long nbHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            nbHits += response.getHits().getTotalHits();
            LOGGER.info("response={}", response);
        }
        LOGGER.info("nbHits={}", nbHits);
    }

    /**
     * 聚合
     */
    @Test
    public void testAggregation() {
        SearchResponse sr = client.prepareSearch()
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(
                        AggregationBuilders.terms("agg1").field("field")
                )
                .addAggregation(
                        AggregationBuilders.dateHistogram("agg2")
                                .field("birth")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                )
                .get();

        // Get your facet results
        Terms agg1 = sr.getAggregations().get("agg1");
        Aggregation agg21 = sr.getAggregations().get("agg2");
//        DateHistogram agg2 = sr.getAggregations().get("agg2");

        LOGGER.info("agg1={},agg2={}", agg1, agg21);
    }


    /**
     * 搜索过程中的操作
     */
    @Test
    public void testTerminateAfter() {
        SearchResponse sr = client.prepareSearch("leads_index")
                .setTerminateAfter(1000)    //查询1000个文档之后
                .get();
        if (sr.isTerminatedEarly()) {
            // We finished early
            LOGGER.info("sr.isTerminatedEarly()={},", sr.isTerminatedEarly());
            LOGGER.info("SearchResponse={},", sr);
        }
        LOGGER.info("SearchResponse={},", sr);
    }

    /**
     * 模板搜索
     */
    @Test
    public void testSearchTemplate(){
        Map<String, Object> template_params = new HashMap<>();
        template_params.put("param_gender", "male");
        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("template_gender")
                .setScriptType(ScriptType.FILE)
                .setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();

        /*
        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("template_gender")
                .setScriptType(ScriptType.STORED)
                .setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();
        */

        /*
        sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "        \"query\" : {\n" +
                        "            \"match\" : {\n" +
                        "                \"gender\" : \"{{param_gender}}\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();
        */
    }


    /**
     * 聚合
     */
    @Test
    public void testAggregation2(){
        SearchResponse sr = client.prepareSearch()
                .addAggregation(
                        AggregationBuilders.terms("by_country").field("country")
                                .subAggregation(AggregationBuilders.dateHistogram("by_year")
                                        .field("dateOfBirth")
                                        .dateHistogramInterval(DateHistogramInterval.YEAR)
                                        .subAggregation(AggregationBuilders.avg("avg_children").field("children"))
                                )
                )
                .execute().actionGet();
    }
}
