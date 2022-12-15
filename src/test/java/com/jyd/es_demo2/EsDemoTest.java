package com.jyd.es_demo2;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.jyd.es_demo2.entitry.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class EsDemoTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 添加文档
     * @throws IOException
     */
    @Test
     public void indexData() throws IOException {
        // IndexRequest
        IndexRequest indexRequest = new IndexRequest("index_user2");
        User user = new User();
        user.setName("张三");
        user.setAge(20);
        user.setSex("男");
        user.setAddress("广州");
        String source = JSONUtil.toJsonStr(user);
        indexRequest.id("1").source(source, XContentType.JSON);
        // 操作ES
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);
    }

    /**
     * 修改文档
     * @throws IOException
     */
    @Test
    public void updateData() throws IOException {
        // UpdateRequest
        UpdateRequest updateRequest = new UpdateRequest("users", "1");
        User user = new User();
        user.setName("李四");
        user.setAge(20);
        user.setSex("女");
        user.setAddress("广州");
        updateRequest.doc(JSONUtil.toJsonStr(user), XContentType.JSON);
        // 操作ES
        restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
    }

    /**
     * 删除文档
     * @throws IOException
     */
    @Test
    public void delete() throws IOException {
        // DeleteRequest
        DeleteRequest deleteRequest = new DeleteRequest("users", "1");
        // 操作ES
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
    }


    /**
     * 查询文档
     * @throws IOException
     */
    @Test
    public void search() throws IOException {
        // SearchRequest
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("users");
        // 构建检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页采用简单的from + size分页，适用数据量小的，了解更多分页方式可自行查阅资料
        //        searchSourceBuilder.from((page - 1) * rows);
        //        searchSourceBuilder.size(rows);
        // 查询所有
        //        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        // 根据字段匹配
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("userName","李四");
        searchSourceBuilder.query(queryBuilder);

        searchRequest.source(searchSourceBuilder);
        // 查询ES
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("查询结果：" + searchResponse.toString());
        SearchHits hits = searchResponse.getHits();
        // 遍历封装列表对象
        List<User> userList = new ArrayList<>();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            userList.add(JSONUtil.toBean(searchHit.getSourceAsString(), User.class));
        }
        System.out.println(userList);
    }

    /**
     * 索引的创建 Request: PUT index_user2
     *
     * @throws IOException
     */
    @Test
    void testCreateIndex() throws IOException {
        // 1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("index_user2");
        // 2.执行创建请求 IndicesClient 请求后获得响应
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(createIndexResponse);// org.elasticsearch.client.indices.CreateIndexResponse@cd831c72
    }

    /**
     * 获取索引 GET
     *
     * @throws IOException
     */
    @Test
    void testExistIndex() throws IOException {
        // 1.获取索引库的请求
        GetIndexRequest request = new GetIndexRequest("index_user2");
        // 2.执行获取索引库的请求
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(exists);// 如果索引库存在，则输出：true，否则输出false
    }

    /**
     * 删除索引 DELETE
     *
     * @throws IOException
     */
    @Test
    void testDeleteIndex() throws IOException {
        // 1.删除索引库的请求
        DeleteIndexRequest request = new DeleteIndexRequest("index_user2");
        // 2.执行删除索引库的请求
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(delete.isAcknowledged());// 删除成功输出true，否则为false
    }

    /**
     * 添加文档
     *
     * @throws IOException
     */
    @Test
    void testAddDocument() throws IOException {
        // 1.创建对象
        User user = new User("李四", "",22,"");
        // 2.创建请求
        IndexRequest request = new IndexRequest("index_user2");

        // 3.构建请求规则：PUT /index_user2/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        //request.timeout("1s");

        // 4.将user对象数据放入请求,json 数据： 需要用到fastjson
        request.source(JSONUtil.toJsonStr(user), XContentType.JSON);

        // 5.客户端发送请求,获取响应的结果
        IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        // 输出结果
        // IndexResponse[index=index_user2,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]
        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());// CREATED：表示创建成功
    }

    /**
     * 获取文档,判断是否存在
     *
     * @throws IOException
     */
    @Test
    void testIsExists() throws IOException {
        // 1.判断文档是否存在的请求
        GetRequest request = new GetRequest("index_user2", "1");

        // 2.执行请求：判断文档是否存在
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(exists);// 存在返回true,否则返回false
    }

    /**
     * 获取文档信息
     *
     * @throws IOException
     */
    @Test
    void testGetDocument() throws IOException {
        // 1.获取文档信息的请求
        GetRequest getRequest = new GetRequest("index_user2", "1");
        // 2.执行获取文档信息的请求
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse);// 返回的全部内容和命令 是一样的：
    }

    /**
     * 更新文档信息
     *
     * @throws IOException
     */
    @Test
    void testUpdateDocument() throws IOException {
        // 1.更新文档请求
        UpdateRequest updateRequest = new UpdateRequest("index_user2", "1");
        // 设置请求超时时间
        updateRequest.timeout("1s");
        // user数据对象封装到json中
        User user = new User("周三", "男",23,"上海");
        updateRequest.doc(JSONUtil.toJsonStr(user), XContentType.JSON);

        // 2.执行更新文档请求
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(updateResponse.status());// OK：表示更新成功
    }

    /**
     * 删除文档记录
     *
     * @throws IOException
     */
    @Test
    void testDeleteDocument() throws IOException {
        // 1.删除文档请求
        DeleteRequest deleteRequest = new DeleteRequest("index_user2", "1");
        deleteRequest.timeout("1s");

        // 2.执行删除文档请求
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(deleteResponse.status());// OK：表示删除成功
    }

    /**
     * 批量插入数据
     *
     * @throws IOException
     */
    @Test
    void testBulkRequest() throws IOException {
        // 1.批处理请求
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        // user数据集合
        ArrayList<User> list = new ArrayList<>();
        list.add(new User("路飞","男", 1,"广州"));
        list.add(new User("索隆","男", 2,"广州"));
        list.add(new User("山治","男", 3,"广州"));
        list.add(new User("娜美","女", 4,"广州"));
        list.add(new User("罗宾","女", 5,"广州"));
        list.add(new User("乔巴","男", 6,"广州"));

        for (int i = 0; i < list.size(); i++) {
            // 批量更新，修改，删除 都是在此进行操作
            bulkRequest.add(
                    new IndexRequest("index_user2")
                            // 批量赋值文档id： 如果不在自己赋值文档id，会默认生成随机的文档id
                            .id("" + (i + 1))
                            // ArrayList转换成json
                            .source(JSONUtil.toJsonStr(list.get(i)), XContentType.JSON)
            );
        }
        // 2.执行批量插入请求
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        // 输出结果
        System.out.println(bulkResponse.status());// OK: 表示批量插入成功！
    }

    /**
     * ES中数据搜索:
     * SearchRequest 搜索请求
     * SearchRequest 搜索请求
     * SearchSourceBuilder 条件构造
     * HighlightBuilder 构建高亮
     * TermQueryBuilder 精确查询
     * XXXQueryBuilder 构建我们需要用到的命令
     *
     * @throws IOException
     */
    @Test
    void testSearch() throws IOException {
        // 1.创建搜索请求
        SearchRequest searchRequest = new SearchRequest("index_user2");
        // 2.构建搜索条件：条件构造器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 高亮结果的条件构造器
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");// 要高亮的字段
        highlightBuilder.requireFieldMatch(false);// 不需要多个字段高亮，如果需要设置为true
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        // 条件构造器，开启搜索结果高亮，并加入高亮结果的条件构造器
        sourceBuilder.highlighter(highlightBuilder);

        /**
         * 查询条件,使用QueryBuilders工具类来实现:
         *              QueryBuilders.termQuery() 精确查询
         *              QueryBuilders.matchAllQuery() 匹配所有
         */
        //TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "csp");// 精确查询
        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();// 搜索所有数据
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "路飞");// 搜索字段name为路飞的数据

        // 查询条件（matchQueryBuilder）放入条件构造器
        sourceBuilder.query(matchQueryBuilder);

        // 条件构造器，开启分页条件: 从第1个数据开始，每页展示5条结果数据
        sourceBuilder.from(0);
        sourceBuilder.size(5);

        // 条件构造器，搜索请求超时时间60s
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 将条件构造器放入搜索请求
        searchRequest.source(sourceBuilder);

        // 执行搜索请求，并获得searchResponse响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 搜索得到的所有结果都封装在hits里面,拿数据从hits里面获取
        SearchHits hits = searchResponse.getHits();
        //System.out.println(JSON.toJSONString(hits));

        // 遍历hits：解析结果，并将结果放入resultList集合
        ArrayList<Map<String, Object>> resultList = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            // 获取高亮的字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("name");// 获取高亮的字段

            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();// 先获取原来未高亮的结果

            // 解析高亮的字段, 将原来未高亮的title字段换成高亮的字段
            if (name != null) {
                Text[] fragments = name.fragments();
                String newName = "";
                for (Text text : fragments) {
                    newName += text;
                }
                // 高亮字段替换原来内容
                sourceAsMap.put("name", newName);
            }
            resultList.add(documentFields.getSourceAsMap());
        }

        // 遍历resultList
        resultList.forEach(item -> {
            System.out.println(item);// {name=<span style='color:red'>路</span><span style='color:red'>飞</span>, age=1}
        });
    }



}
