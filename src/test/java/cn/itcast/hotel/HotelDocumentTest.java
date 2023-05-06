package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import cn.itcast.hotel.service.impl.HotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateAction;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE_CREATE;

@SpringBootTest
public class HotelDocumentTest {


    private RestHighLevelClient client;

    @Autowired
    private IHotelService hotelService;

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

    /*
    * 新增
    * */
    @Test
    void createHotelIndex() throws IOException {
        //从数据库中获取酒店数据
        Hotel hotel = hotelService.getById(61083);
        //转换为文档类型
        HotelDoc hotelDoc = new HotelDoc(hotel);
        //1.准备Request对象
        IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());
        //2.准备JSON文档
        request.source(JSON.toJSONString(hotelDoc),XContentType.JSON);
        //3.发送请求
        client.index(request,RequestOptions.DEFAULT);
    }

    /*
    * 查询
    * 根据id查询到的文档数据是json，需要反序列化为java对象
    * */
    @Test
    void queryHotelIndex() throws IOException {
        //1.准备request对象
        GetRequest request=new GetRequest("hotel").id("61083");
        //2.发出请求得到结果
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        //3.解析结果
        String sourceAsString = response.getSourceAsString();
        HotelDoc hotelDoc = JSON.parseObject(sourceAsString, HotelDoc.class);
        System.out.println(hotelDoc);

    }

    /*
    * 修改文档
    * */
    @Test
    void updateDoc() throws IOException {

        //1.准备request对象
        UpdateRequest request = new UpdateRequest("hotel","61083");
        //2.准备更新参数
        request.doc(
                "price",183,
                "city","Washington"
        );
        //3.更新文档
        client.update(request,RequestOptions.DEFAULT);
    }

    /*
    * 删除文档
    * */
    @Test
    void deleteDoc() throws IOException{
        //1.准备request对象
        DeleteRequest request=new DeleteRequest("hotel","61083");

        //3.发送请求
        client.delete(request,RequestOptions.DEFAULT);
    }


    /*
    * 文档批处理
    * */
    @Test
    void batchDocument(){
        //1.创建Bulk请求

        //2.将查询到的数据转化为文档类型数据

        //3.利用JavaRestClient中的Bulk批处理，实现批量新增文档
    }



}
