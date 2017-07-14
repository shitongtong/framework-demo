package cn.stt.es.estest;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/14.
 */
public class AdministrationAPI {
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


    /**
     * 集群健康状态查询
     */
    @Test
    public void testClusterHealth(){
        ClusterHealthResponse healths = client.admin().cluster().prepareHealth().get();
        String clusterName = healths.getClusterName();
        int numberOfDataNodes = healths.getNumberOfDataNodes();
        int numberOfNodes = healths.getNumberOfNodes();

        for (ClusterIndexHealth health : healths.getIndices().values()) {
            String index = health.getIndex();
            int numberOfShards = health.getNumberOfShards();
            int numberOfReplicas = health.getNumberOfReplicas();
            ClusterHealthStatus status = health.getStatus();
        }
    }
}
