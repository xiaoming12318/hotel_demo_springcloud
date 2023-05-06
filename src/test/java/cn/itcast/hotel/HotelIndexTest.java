package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE_CREATE;
import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE_DELETE;

@SpringBootTest
public class HotelIndexTest {


    private RestHighLevelClient client;

    @Test
    void testInit(){
        System.out.println(client);
    }

    //当你需要这个RestHighLevelClient的时候，就不用每次都去创建一个关联的连接
    @BeforeEach
    void setUp() {
        this.client=new RestHighLevelClient(RestClient.builder(
           HttpHost.create("http://192.168.26.131:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    @Test
    void createHotelIndex() throws IOException {
        //1.创建request对象
        CreateIndexRequest request=new CreateIndexRequest("hotel");
        //2.准备请求的参数，DSL语句，类似于spring中的restful语句
        request.source(MAPPING_TEMPLATE_CREATE, XContentType.JSON);
        //3.发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }


    @Test
    void deleteHotelIndex() throws IOException {
        //4.删除之前也要准备请求参数，也就是DSL语句
        DeleteIndexRequest request_del=new DeleteIndexRequest("hotel");
        client.indices().delete(request_del,RequestOptions.DEFAULT);
    }

    @Test
    void exitesHotelIndex() throws IOException {
        //判断是否存在
        GetIndexRequest getIndexRequest=new GetIndexRequest("hotel");
        System.out.println(client.indices().exists(getIndexRequest, RequestOptions.DEFAULT));
    }


    @Test
    void testCreateDoc() throws IOException {

    }
}
