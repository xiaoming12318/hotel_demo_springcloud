package cn.itcast.hotel;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class HotelSearchTest {

    private RestHighLevelClient client;

    @BeforeEach
    void setUp(){
        client=new RestHighLevelClient(RestClient.builder(HttpHost.create("http://192.168.26.131:9200")));
    }

    @AfterEach
    void tearDown() throws IOException {
        client.close();
    }


    @Test
    void testMatchAll() throws IOException {
        //准备request
        SearchRequest request=new SearchRequest("hotel");
        //准备DSL语句
        request.source().query(QueryBuilders.matchAllQuery());
        //准备DSL语句作单表查询
        request.source().query(QueryBuilders.multiMatchQuery("如家","business","name"));
        //发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

     handleResponse(response);
    }



    @Test
    void testMultiMatchQuery() throws IOException {
        //准备request
        SearchRequest request=new SearchRequest("hotel");
        //准备DSL语句作多条件匹配查询
        request.source().query(QueryBuilders.multiMatchQuery("如家","business","name"));
        //发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        handleResponse(response);
    }


    @Test
    void testMatchQuery() throws IOException {
        //准备request
        SearchRequest request=new SearchRequest("hotel");
        //准备DSL语句作多条件匹配查询
        request.source().query(QueryBuilders.matchQuery("name","如家"));
        //发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);

    }

    private void handleResponse(SearchResponse response) {
        //解析结果
        SearchHits searchHits = response.getHits();
        //查询的总条数
        TotalHits totalHits = searchHits.getTotalHits();
        //查询的结果数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            //得到source
            String json = hit.getSourceAsString();
            System.out.println(json);

        }
        System.out.println(response);
    }

    private static final String testData="{\n" +
            "  \"took\" : 133,\n" +
            "  \"timed_out\" : false,\n" +
            "  \"_shards\" : {\n" +
            "    \"total\" : 1,\n" +
            "    \"successful\" : 1,\n" +
            "    \"skipped\" : 0,\n" +
            "    \"failed\" : 0\n" +
            "  },\n" +
            "  \"hits\" : {\n" +
            "    \"total\" : {\n" +
            "      \"value\" : 30,\n" +
            "      \"relation\" : \"eq\"\n" +
            "    },\n" +
            "    \"max_score\" : 2.0645075,\n" +
            "    \"hits\" : [\n" +
            "      {\n" +
            "        \"_index\" : \"hotel\",\n" +
            "        \"_type\" : \"_doc\",\n" +
            "        \"_id\" : \"339952837\",\n" +
            "        \"_score\" : 2.0645075,\n" +
            "        \"_source\" : {\n" +
            "          \"address\" : \"良乡西路7号\",\n" +
            "          \"brand\" : \"如家\",\n" +
            "          \"business\" : \"房山风景区\",\n" +
            "          \"city\" : \"北京\",\n" +
            "          \"id\" : 339952837,\n" +
            "          \"location\" : \"39.73167, 116.132482\",\n" +
            "          \"name\" : \"如家酒店(北京良乡西路店)\",\n" +
            "          \"pic\" : \"https://m.tuniucdn.com/fb3/s1/2n9c/3Dpgf5RTTzrxpeN5y3RLnRVtxMEA_w200_h200_c1_t0.jpg\",\n" +
            "          \"price\" : 159,\n" +
            "          \"score\" : 46,\n" +
            "          \"starName\" : \"二钻\"\n" +
            "        },\n" +
            "        \"highlight\" : {\n" +
            "          \"name\" : [\n" +
            "            \"<em>如家</em>酒店(北京良乡西路店)\"\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      {\n" +
            "        \"_index\" : \"hotel\",\n" +
            "        \"_type\" : \"_doc\",\n" +
            "        \"_id\" : \"2359697\",\n" +
            "        \"_score\" : 1.9649367,\n" +
            "        \"_source\" : {\n" +
            "          \"address\" : \"清河小营安宁庄东路18号20号楼\",\n" +
            "          \"brand\" : \"如家\",\n" +
            "          \"business\" : \"上地产业园/西三旗\",\n" +
            "          \"city\" : \"北京\",\n" +
            "          \"id\" : 2359697,\n" +
            "          \"location\" : \"40.041322, 116.333316\",\n" +
            "          \"name\" : \"如家酒店(北京上地安宁庄东路店)\",\n" +
            "          \"pic\" : \"https://m.tuniucdn.com/fb3/s1/2n9c/2wj2f8mo9WZQCmzm51cwkZ9zvyp8_w200_h200_c1_t0.jpg\",\n" +
            "          \"price\" : 420,\n" +
            "          \"score\" : 46,\n" +
            "          \"starName\" : \"二钻\"\n" +
            "        },\n" +
            "        \"highlight\" : {\n" +
            "          \"name\" : [\n" +
            "            \"<em>如家</em>酒店(北京上地安宁庄东路店)\"\n" +
            "          ]\n" +
            "        }\n" +
            "      },";


    @Test
    void aaaa() throws IOException{
        //发起请求
        SearchRequest request=new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchAllQuery());
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //第一层
        SearchHits hits = response.getHits();
        //第二层
        SearchHit[] searchHits = hits.getHits();

        //遍历
        for (SearchHit searchHit : searchHits) {
            System.out.println(searchHit.getSourceAsString());
            System.out.println();
            System.out.println("===============================================================");
            System.out.println(JSON.parseObject(searchHit.getSourceAsString()));
        }


    }



}
