package cn.stt.es.estest;

import cn.stt.es.util.JsonDataGeneratorUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * 使用java API操作elasticSearch    来自官网
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/13.
 */
public class DocumentAPIs {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentAPIs.class);

    private TransportClient client;

    /**
     * 建立连接一
     * 默认的连接集群名是：“elasticsearch”
     */
//    @Before
    public void startUp1() throws UnknownHostException {
        client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.182"), 9300));
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));//集群

    }

    /**
     * 建立连接二
     * 设置连接的集群名
     */
    @Before
    public void startUp2() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "my-application")  //设置连接的集群名
                .put("client.transport.sniff", true)    //开启嗅探
                .put("client.transport.ignore_cluster_name", true) // 忽略集群名字验证, 打开后集群名字不对也能连接上
                //等待来自某个节点的ping响应的时间。默认为5s
                //java.lang.IllegalArgumentException: failed to parse setting [client.transport.ping_timeout] with value [5] as a time value: unit is missing or unrecognized
//                .put("client.transport.ping_timeout", 5)
                //在连接的节点列表上进行采样的频率。默认为5s
                // 异常了：failed to parse setting [client.transport.nodes_sampler_interval] with value [5] as a time value: unit is missing or unrecognized
//                .put("client.transport.nodes_sampler_interval", 5)
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

    /**
     * 创建一个索引
     */
    @Test
    public void addDocument() {
//        String json = JsonDataGeneratorUtil.generatorByManually();
//        IndexResponse indexResponse = client.prepareIndex("twitter", "tweet").setSource(json).get();

        XContentBuilder xContentBuilder = JsonDataGeneratorUtil.generatorByESHelpers();
        IndexResponse indexResponse = client.prepareIndex("twitter", "tweet", "1").setSource(xContentBuilder).get();
        // Index name
        String _index = indexResponse.getIndex();
        // Type name
        String _type = indexResponse.getType();
        // Document ID (generated or not)
        String _id = indexResponse.getId();
        // Version (if it's the first time you index this document, you will get: 1)
        long _version = indexResponse.getVersion();
        // status has stored current instance statement.
        RestStatus status = indexResponse.status();
        int statusStatus = status.getStatus();
        String statusName = status.name();
        LOGGER.info("_index={},_type={},_id={},_version={},status={}|statusStatus={},statusName={}", _index, _type, _id, _version, status, statusStatus, statusName);
        //_index=twitter,_type=tweet,_id=1,_version=2,status=OK|statusStatus=200,statusName=OK
    }

    @Test
    public void batchAddDocument() {

        for (int i = 0; i < 5; i++) {
            Map<String, Object> json = new HashMap<String, Object>();
            json.put("user", "kimchy" + i);
            json.put("postDate", new Date());
            json.put("message", "trying out Elasticsearch");
            IndexResponse indexResponse = client.prepareIndex("twitter", "tweet", i + "").setSource(json).get();
            LOGGER.info(indexResponse.toString());
        }
//        IndexResponse indexResponse = indexRequestBuilder.get();
//        LOGGER.info(indexResponse.toString());
    }

    /**
     * 从索引中获取文档内容
     */
    @Test
    public void getDocument() {
        GetResponse response = client.prepareGet("twitter", "tweet", "1")
                .setOperationThreaded(false)
                .get();
        LOGGER.info(response.toString());
        //{"_index":"twitter","_type":"tweet","_id":"1","_version":2,"found":true,"_source":{"user":"kimchy","postDate":"2017-07-13T07:51:05.066Z","message":"trying out Elasticsearch"}}
    }

    /**
     * 删除指定文档
     */
    @Test
    public void deleteDocument() {
        DeleteResponse response = client.prepareDelete("twitter", "tweet", "1").get();
        LOGGER.info(response.toString());
        //DeleteResponse[index=twitter,type=tweet,id=1,version=3,result=deleted,shards=ShardInfo{total=2, successful=1, failures=[]}]
    }

    /**
     * 基于查询结果删除给定的一组文档(同步)
     */
    @Test
    public void deleteDocumentByQuery() {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchQuery("user", "kimchy1"))
                        .source("twitter")
                        .get();
        long deleted = response.getDeleted();
        LOGGER.info(response.toString());
        //BulkIndexByScrollResponse[took=165.3ms,timed_out=false,sliceId=null,updated=0,created=0,deleted=1,batches=1,versionConflicts=0,
        // noops=0,retries=0,throttledUntil=0s,bulk_failures=[],search_failures=[]]
    }

    /**
     * 基于查询结果删除给定的一组文档(异步)
     */
    @Test
    public void deleteDocumentByQuery2() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("user", "kimchy2"))
                .source("twitter")
                .execute(new ActionListener<BulkByScrollResponse>() {
                    public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                        System.out.println("0000000");
                        LOGGER.info("删除成功,返回信息：{}", bulkByScrollResponse);
                    }

                    public void onFailure(Exception e) {
                        System.out.println("dsds");
                        LOGGER.error("删除失败，异常：{}", e);
                    }

                });
        //删除成功，但是无打印信息
    }


    /**
     * 使用updateRequest更新文档
     */
    @Test
    public void updateDocument1() {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("twitter");
        updateRequest.type("tweet");
        updateRequest.id("1");
        try {
            updateRequest.doc(jsonBuilder()
                    .startObject()
                    .field("user", "qq")
                    .endObject());
            UpdateResponse updateResponse = client.update(updateRequest).get();
            LOGGER.info(updateResponse.toString());
            //UpdateResponse[index=twitter,type=tweet,id=1,version=2,result=updated,shards=ShardInfo{total=2, successful=1, failures=[]}]
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用prepareUpdate更新
     */
    @Test
    public void updateDocument2() {
        UpdateResponse updateResponse1 = client.prepareUpdate("twitter", "tweet", "1")
//                .setScript(new Script("ctx._source.gender = \"male\""  , ScriptService.ScriptType.INLINE, null, null))
                .setScript(new Script("ctx._source.user = \"kkkk\""))
                .get();
        LOGGER.info(updateResponse1.toString());
        //UpdateResponse[index=twitter,type=tweet,id=1,version=4,result=updated,shards=ShardInfo{total=2, successful=1, failures=[]}]
        /*
        try {
            UpdateResponse updateResponse = client.prepareUpdate("twitter", "tweet", "1")
                    .setDoc(jsonBuilder()
                            .startObject()
                            .field("user", "好好好")
                            .endObject())
                    .get();
            LOGGER.info(updateResponse.toString());
            //UpdateResponse[index=twitter,type=tweet,id=1,version=3,result=updated,shards=ShardInfo{total=2, successful=1, failures=[]}]
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * 更新的文档不存在则插入
     */
    @Test
    public void upsertDocument() {
        try {
            //插入
            IndexRequest indexRequest = new IndexRequest("upsert_index", "type", "1")
                    .source(jsonBuilder()
                            .startObject()
                            .field("name", "Joe Smith")
                            .field("gender", "male")
                            .endObject());
//            IndexResponse indexResponse = client.index(indexRequest).get();
//            LOGGER.info(indexResponse.toString());

            //查看
//            GetResponse response = client.prepareGet("upsert_index", "type", "1")
//                    .setOperationThreaded(false)
//                    .get();
//            LOGGER.info(response.toString());

            //更新
            UpdateRequest updateRequest = new UpdateRequest("upsert_index", "type", "1")
                    .doc(jsonBuilder()
                            .startObject()
                            .field("name", "upsert_qq")
                            .endObject())
                    .upsert(indexRequest);
            UpdateResponse updateResponse = client.update(updateRequest).get();
            LOGGER.info(updateResponse.toString());

            //再次查看
            GetResponse response1 = client.prepareGet("upsert_index", "type", "1")
                    .setOperationThreaded(false)
                    .get();
            LOGGER.info(response1.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取多个document
     */
    @Test
    public void multiGetDocument() {
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("twitter", "tweet", "1")
                .add("twitter", "tweet", "2", "3", "4")
                .add("another", "type", "foo")
                .get();

        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                String json = response.getSourceAsString();
                LOGGER.info(response.toString());
                //{"_index":"twitter","_type":"tweet","_id":"1","_version":4,"found":true,"_source":{"user":"kkkk","postDate":"2017-07-13T09:17:24.280Z","message":"trying out Elasticsearch"}}
            }
        }
    }

    /**
     * 批量操作
     */
    @Test
    public void bulkDocument() {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        try {
            // either use client#prepare, or use Requests# to directly build index/delete requests
            bulkRequest.add(client.prepareIndex("twitter", "tweet", "10")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "qq10")
                            .field("postDate", new Date())
                            .field("message", "trying out Elasticsearch")
                            .endObject()
                    )
            );

            bulkRequest.add(client.prepareIndex("twitter", "tweet", "20")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "qq20")
                            .field("postDate", new Date())
                            .field("message", "another post")
                            .endObject()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
            LOGGER.info("-=-=-=-=-=-=-=-=-=-");
        }
        LOGGER.info(bulkResponse.toString());
        //org.elasticsearch.action.bulk.BulkResponse@67e13bd0
    }

    /**
     * 批量处理器
     */
    @Test
    public void bulkProcessor() throws IOException, InterruptedException {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {

            public void beforeBulk(long l, BulkRequest bulkRequest) {
                LOGGER.info(bulkRequest.numberOfActions()+"");//2
                LOGGER.info("bulkRequest:{}",bulkRequest);//bulkRequest:org.elasticsearch.action.bulk.BulkRequest@40e37b06
            }

            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                boolean hasFailures = bulkResponse.hasFailures();
                LOGGER.info("hasFailures:{}",hasFailures);//hasFailures:false
            }

            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                throwable.printStackTrace();
                LOGGER.info("throwable:{}",throwable.getMessage());
            }
        })
                .setBulkActions(10000)  //我们希望每10,000个请求都执行一次
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        bulkProcessor.add(new IndexRequest("twitter", "tweet", "33").source(jsonBuilder()
                .startObject()
                .field("name", "Joe Smith")
                .field("gender", "male")
                .endObject()));
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));
        bulkProcessor.flush();
        bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        RefreshResponse refreshResponse = client.admin().indices().prepareRefresh().get();
        SearchResponse searchResponse = client.prepareSearch().get();
        LOGGER.info("refreshResponse:{}",refreshResponse);//refreshResponse:org.elasticsearch.action.admin.indices.refresh.RefreshResponse@36480b2d
        LOGGER.info("searchResponse:{}",searchResponse);
        //searchResponse:{"took":29,"timed_out":false,"_shards":{"total":36,"successful":36,"failed":0},"hits":{"total":358986,"max_score":1.0,
        // "hits":[{"_index":".kibana","_type":"config","_id":"5.5.0","_score":1.0,"_source":{"buildNum":15382}},{"_index":"leads_index","_type":"tstype","_id":"F41A9178-F3CE-48FD-AC8D-59DE75A498E8","_score":1.0,
        // "_source":{"connect_status":"create","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"F41A9178-F3CE-48FD-AC8D-59DE75A498E8","sale_uuid":null,
        // "cc_uuid":"B13CC1D3-5CBE-4720-BA59-E94253866F59","password":null,"signup_date":"2017-02-10T09:14:04.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":1,
        // "@version":"1","recommend_cc_name":null,"id":65732,"create_date":"2017-02-10T09:14:04.000Z","channel_uuid":"FCKODVSXN5","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-05-18T08:54:16.000Z",
        // "is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.266Z","phone":"15735072960","is_old_data":false,"grade":"","is_recommend":false,
        // "name":"左宏杰","distribute_status":0,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype",
        // "_id":"5927CD57-AF12-4C09-BCCA-A66392D37653","_score":1.0,"_source":{"connect_status":"noconnectwait","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"5927CD57-AF12-4C09-BCCA-A66392D37653","sale_uuid":"F6B5D832-1655-4689-A50C-1D26D636A44B","cc_uuid":"F6B5D832-1655-4689-A50C-1D26D636A44B","password":null,"signup_date":"2017-02-10T09:15:38.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":1,"@version":"1","recommend_cc_name":null,"id":65736,"create_date":"2017-02-10T09:15:38.000Z","channel_uuid":"MEPQUYE5GH","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-07-04T10:03:14.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.267Z","phone":"13131771395","is_old_data":false,"grade":"高一","is_recommend":false,"name":"迟海青","distribute_status":1,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype","_id":"E77A9190-9F40-4CF7-AD1C-A518F6012827","_score":1.0,"_source":{"connect_status":"create","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"E77A9190-9F40-4CF7-AD1C-A518F6012827","sale_uuid":null,"cc_uuid":"B13CC1D3-5CBE-4720-BA59-E94253866F59","password":null,"signup_date":"2017-02-10T09:17:29.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":1,"@version":"1","recommend_cc_name":null,"id":65738,"create_date":"2017-02-10T09:17:29.000Z","channel_uuid":"FCKODVSXN5","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-05-18T08:54:16.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.267Z","phone":"15299029875","is_old_data":false,"grade":"","is_recommend":false,"name":"李春","distribute_status":0,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype","_id":"91322DE6-BDDF-492C-AE76-CB41154EB4EF","_score":1.0,"_source":{"connect_status":"create","introducer":null,"subject":"数学","cr_uuid":null,"key_num":"","type":"tstype","uuid":"91322DE6-BDDF-492C-AE76-CB41154EB4EF","sale_uuid":null,"cc_uuid":"B13CC1D3-5CBE-4720-BA59-E94253866F59","password":null,"signup_date":"2017-02-10T09:18:18.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":1,"@version":"1","recommend_cc_name":null,"id":65744,"create_date":"2017-02-10T09:18:18.000Z","channel_uuid":"AMBY26NQIR","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-05-25T05:14:21.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.267Z","phone":"13355350881","is_old_data":false,"grade":"小五","is_recommend":false,"name":"温景然","distribute_status":0,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype","_id":"D47572A9-80E7-44A0-8F41-25750B7DF4D1","_score":1.0,"_source":{"connect_status":"create","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"D47572A9-80E7-44A0-8F41-25750B7DF4D1","sale_uuid":null,"cc_uuid":"51F4D9ED-1386-49F0-8557-456267705B05","password":null,"signup_date":"2017-02-10T09:22:49.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":1,"student_status":1,"@version":"1","recommend_cc_name":null,"id":65750,"create_date":"2017-02-10T09:22:51.000Z","channel_uuid":"MEPQUYE5GH","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-07-04T09:46:09.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.268Z","phone":"15031914021","is_old_data":false,"grade":"初三","is_recommend":false,"name":"赵立丁","distribute_status":0,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype","_id":"407E9B36-B484-4560-AAFD-4E4F0BF20EC7","_score":1.0,"_source":{"connect_status":"create","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"407E9B36-B484-4560-AAFD-4E4F0BF20EC7","sale_uuid":null,"cc_uuid":"DF2ADA28-01AA-4067-98C2-A81C10518610","password":null,"signup_date":"2017-02-10T09:25:20.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":4,"@version":"1","recommend_cc_name":null,"id":65756,"create_date":"2017-02-10T09:25:20.000Z","channel_uuid":"FCKODVSXN5","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-06-07T12:49:37.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.268Z","phone":"13654794158","is_old_data":false,"grade":"","is_recommend":false,"name":"孙哲","distribute_status":0,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype","_id":"924A2FDD-AB69-4BAA-BB41-C5FDC3621299","_score":1.0,"_source":{"connect_status":"create","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"924A2FDD-AB69-4BAA-BB41-C5FDC3621299","sale_uuid":null,"cc_uuid":"51F4D9ED-1386-49F0-8557-456267705B05","password":null,"signup_date":"2017-02-10T09:26:37.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":1,"@version":"1","recommend_cc_name":null,"id":65760,"create_date":"2017-02-10T09:26:37.000Z","channel_uuid":"FCKODVSXN5","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-05-18T11:44:04.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.269Z","phone":"15137337116","is_old_data":false,"grade":"","is_recommend":false,"name":"朱慧","distribute_status":0,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype","_id":"C7D7F717-7EB2-492D-A48E-4C304789E989","_score":1.0,"_source":{"connect_status":"successwait","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"C7D7F717-7EB2-492D-A48E-4C304789E989","sale_uuid":"F6B5D832-1655-4689-A50C-1D26D636A44B","cc_uuid":"F6B5D832-1655-4689-A50C-1D26D636A44B","password":null,"signup_date":"2017-02-17T10:23:43.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":1,"@version":"1","recommend_cc_name":null,"id":65790,"create_date":"2017-02-10T09:41:48.000Z","channel_uuid":"7CASR61KFP","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-07-04T10:03:14.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.271Z","phone":"13038284712","is_old_data":false,"grade":"初二","is_recommend":false,"name":"赵小月","distribute_status":1,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}},{"_index":"leads_index","_type":"tstype","_id":"A915B0D7-8213-41D7-AB9C-C8F8E9E4C466","_score":1.0,"_source":{"connect_status":"create","introducer":null,"subject":null,"cr_uuid":null,"key_num":"","type":"tstype","uuid":"A915B0D7-8213-41D7-AB9C-C8F8E9E4C466","sale_uuid":null,"cc_uuid":"51F4D9ED-1386-49F0-8557-456267705B05","password":null,"signup_date":"2017-02-10T09:44:21.000Z","dy":"","recommend_cc_uuid":null,"sign_status":false,"is_manager_course":null,"student_status":1,"@version":"1","recommend_cc_name":null,"id":65808,"create_date":"2017-02-10T09:44:21.000Z","channel_uuid":"FCKODVSXN5","exam_area":null,"contact_time":null,"sex":null,"update_date":"2017-06-02T14:02:27.000Z","is_sign":false,"sign_time":null,"recommend_cr_name":null,"recommend_cr_uuid":null,"@timestamp":"2017-07-12T11:41:04.272Z","phone":"18246577288","is_old_data":false,"grade":"","is_recommend":false,"name":"高旭洋","distribute_status":0,"stu_no":null,"is_introduced":false,"connect_des":null,"is_pay":null,"jh":"","age":null,"status":true}}]}}
    }

}
