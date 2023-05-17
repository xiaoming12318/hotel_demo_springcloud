package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class HotelAggregationTest {

//    @Autowired
//    private RestHighLevelClient client;
    private RestHighLevelClient client;

    @BeforeEach
    void startUp(){
        client=new RestHighLevelClient(RestClient
                .builder(HttpHost.create("192.168.26.131:9200")));
    }


    @Test
    void setup() throws IOException {
        //准备请求
        SearchRequest request=new SearchRequest("hotel");
        //DSL语句
        //设置size，返回结果没有hits
        request.source().size(0);
        //聚合
        request.source().aggregation(AggregationBuilders
                .terms("my_aggs")
                .field("brand")
                .size(20)
        );

        //发出请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        //解析聚合结果
        Aggregations aggregations = response.getAggregations();
        //根据名称获取聚合结果
        Terms myAggs = aggregations.get("my_aggs");
        //获取桶
        List<? extends Terms.Bucket> buckets = myAggs.getBuckets();
        //遍历
        for (Terms.Bucket bucket : buckets) {
            //获取key，也就是品牌信息
            String brandName = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.out.println(brandName);
            System.out.println(docCount);
            System.out.println("===================================");

        }















        /*
        GET /hotel/_search
        {
            "size":0;
            "aggs":{
                "my_aggs":{
                    "terms":{
                        "field":"brand",
                        "size":20,
                    }
                }
            }
        }

      */

    }
}
