package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.search.BooleanQuery;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
public class HotelBoolQueryTest {

    private RestHighLevelClient client;


    @BeforeEach
    void setUp(){
        client=new RestHighLevelClient(RestClient.builder(HttpHost.create("192.168.26.131:9200")));
    }

    @AfterEach
    void tearDown() throws IOException {
        client.close();
    }

    @Test
    void preciseQueryBasedOnTerm() throws IOException {
        //发起请求
        SearchRequest request=new SearchRequest("hotel");
        //编写DSL语句  把用JAVA代码实现DSL语法
        request.source().query(QueryBuilders.termQuery("name","如家"));
        //发送请求,并得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    void preciseQueryBasedOnRange(){
        //发起请求
        SearchRequest request=new SearchRequest("hotel");
        //准备DSL
       //准备BooleanQuery
        BoolQueryBuilder booleanQuery=QueryBuilders.boolQuery();
        //添加term
        booleanQuery.must(QueryBuilders.termQuery("city","杭州"));
        //添加range
        booleanQuery.filter(QueryBuilders.rangeQuery("price").lte(250));

    }

    @Test
    void testPageAndSort() throws IOException{
        int page=2, size=5;
        //准备request
        SearchRequest request = new SearchRequest("hotel");
        //准备DSL
        //query
        request.source().query(QueryBuilders.matchAllQuery());
        //排序sort
        request.source().sort("price", SortOrder.DESC);
        //分页 from size
        request.source().from((page-1)*size).size(5);
        //发送请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        //解析响应
        handleResponse(search);
    }

    @Test
    void testHighlight() throws IOException {
        //准备request请求
        SearchRequest request=new SearchRequest("hotel");
        //DSL
        request.source().query(QueryBuilders.matchQuery("all","如家"));
        //高亮
        request.source().highlighter(new HighlightBuilder()
                .field("name")
                .requireFieldMatch(false)
        );

        //发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);

    }

//    private void handleHighlight(SearchHit handleHighLight,HotelDoc hotelDoc){
//        //得到内部的hit转为JSON
//        HotelDoc dataWithHotelDoc = JSON.parseObject(handleHighLight.getSourceAsString(), HotelDoc.class);
//        //处理高亮
//        Map<String, HighlightField> highlightFieldMap=handleHighLight.getHighlightFields();
//        //判断集合中是否为空
//        if (highlightFieldMap!=null){
//            //获取高亮字段结果
//            HighlightField name = highlightFieldMap.get("name");
//            if (name!=null){
//                String string = name.getFragments()[0].string();
//                hotelDoc.setName(string);
//            }
//        }
//    }



    private void handleResponse(SearchResponse response){
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        for(SearchHit hit:searchHits){
            String searchHitSourceAsString = hit.getSourceAsString();
            //反序列化
            HotelDoc hotelDoc = JSON.parseObject(searchHitSourceAsString, HotelDoc.class);
            //获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //根据字段获取高亮值
            HighlightField name = highlightFields.get("name");
            String string = name.fragments()[0].string();
            hotelDoc.setName(string);
            System.out.println(hotelDoc);
        }
    }

}
