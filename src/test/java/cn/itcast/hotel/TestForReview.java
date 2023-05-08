package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.assertj.core.data.Index;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE_CREATE_TEST;

@SpringBootTest
public class TestForReview {

    private RestHighLevelClient client;


    @BeforeEach
    void setUp(){
        this.client=new RestHighLevelClient(RestClient.builder(HttpHost.create("192.168.26.131:9200")));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    /*
    * 查看
    * */
    @Test
    void query() throws IOException {
        //准备request对象
        GetRequest request=new GetRequest("test","1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String sourceAsString = response.getSourceAsString();

        System.out.println(sourceAsString);
    }


    /*
    * 添加
    * */
    @Test
    void add() throws IOException{
        //准备request对象
        IndexRequest request=new IndexRequest("test").id("1");
        //设置添加的参数
        request.source(MAPPING_TEMPLATE_CREATE_TEST, XContentType.JSON);
        //发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    /*
    * 修改
    * */
    @Test
    void update() throws IOException {
        //准备request对象
        UpdateRequest request=new UpdateRequest("test","1");
        //设置修改的值
        request.doc(
                "name","王五"
                ,"age",55);
        //发送请求
        client.update(request,RequestOptions.DEFAULT);
    }

    /*
    * 删除
    * */
    @Test
    void delete() throws IOException {
        //准备request对象
        DeleteRequest request=new DeleteRequest("test","1");
        //发送请求
        client.delete(request,RequestOptions.DEFAULT);
    }

    /*
    * 批量修改
    * */
    @Test
    void bulkModify() throws IOException{
        //准备request对象
        BulkRequest request=new BulkRequest();
        //编辑这个request
        request.add(new IndexRequest("test")
            .id("1")
            .source("name","张三","age",77));

        //发送请求
        client.bulk(request,RequestOptions.DEFAULT);
    }



}
