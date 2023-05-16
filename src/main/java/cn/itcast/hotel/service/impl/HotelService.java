package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            buildBasicQuery(params,request);
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



    private void buildBasicQuery(RequestParams params,SearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //must关键字搜索
        //2.1.普通的list搜索调用
        String key = params.getKey();
        if (key==null || "".equals(key)){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else {
            boolQuery.must(QueryBuilders.matchQuery("all",key));
        }

        //2.2.filter搜索
        //2.2.1.城市条件
        if (params.getCity()!=null && !params.equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        //2.2.2.品牌条件
        if (params.getBrand()!=null && !params.getBrand().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        //2.2.3.星级条件
        if (params.getStarName()!=null && !params.getStarName().equals("")){
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        //2.2.4.价格条件maxPrice
        if (params.getMaxPrice()!=null && params.getMinPrice()!=null){
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .gte(params.getMinPrice())
                    .lte(params.getMaxPrice()));

        }
//        //2.3.排序功能，定位条件
        String location = params.getLocation();
        System.out.println(location);
        if (location!=null && location.equals("")){
            request.source().sort(SortBuilders
                    .geoDistanceSort("location",new GeoPoint(location))
                    .order(SortOrder.DESC)
                    .unit(DistanceUnit.KILOMETERS));
        }

        //算分控制
        FunctionScoreQueryBuilder functionScoreQueryBuilder =
                QueryBuilders.functionScoreQuery(
                        //原始数据，相关性算分的查询
                        boolQuery,
                        //function score的数组
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                //其中的一个function score元素
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        //过滤条件
                                        QueryBuilders.termQuery("isAD","true"),
                                        //算分函数
                                        ScoreFunctionBuilders.weightFactorFunction(10)
                                )
                        });

        request.source().query(functionScoreQueryBuilder);
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

            //获取排序值
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length>0){
                Object sortValue = sortValues[0];
                hotelDoc.setDistance(sortValue);
            }
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
