package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageResult search(RequestParams params) {
        try {
            //1.准备Reuqest
            SearchRequest request=new SearchRequest("hotel");
            //2.准备DSL
            //2.1.query
            String key = params.getKey();
            if (key==null || "".equals(key)){
                //key为空代表没有搜索的关键字，所以是查询全部
                request.source().query(QueryBuilders.matchAllQuery());
            }else{
                //当key不为空的时候就是根据key的值来进行匹配字段值的查询
                request.source().query(QueryBuilders.matchQuery("all",key));
            }
            //2.2.分页
            //多少页
            Integer page = params.getPage();
            //每页大小
            Integer size = params.getSize();
            request.source().from((page-1)*size).size(size);
            //3.发送请求，得到响应
            SearchResponse search = client.search(request, RequestOptions.DEFAULT);
            //4.解析响应
            return handleResponse(search);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public PageResult filter(RequestParams params) {
        //1。准备request请求

        //2.准备DSL语句
        //2.1.普通的list搜索调用
        //2.2.filter搜索

        //3.发送请求，搜到反馈消息

        //4.处理返回消息，返回到前台
        return new PageResult();
    }

    public PageResult handleResponse(SearchResponse search){
        //解析结果
        SearchHits searchHits = search.getHits();
        //查询的总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到"+total+"条数据");
        //文档数组
        SearchHit[] hits=searchHits.getHits();
        //遍历
        List<HotelDoc> hotels=new ArrayList<>();
        for (SearchHit hit : hits) {
            //获取文档source
            String json = hit.getSourceAsString();
            //反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            hotels.add(hotelDoc);
//            //获取高亮结果
//            Map<String, HighlightField> highlightFieldMap=hit.getHighlightFields();
//            if (!CollectionUtils.isEmpty(highlightFieldMap)){
//                //字段名获取高亮结果
//                HighlightField highlightField = highlightFieldMap.get("name");
//                if (highlightField!=null){
//                    //获取高亮值
//                    String name = highlightField.getFragments()[0].string();
//                    hotelDoc.setName(name);
//                }
//            }
        }


        return new PageResult(total,hotels);
    }
}
