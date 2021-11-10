package com.mao.community;

import com.mao.community.dao.DiscussPostMapper;
import com.mao.community.dao.elasticsearch.DiscussPostRepository;
import com.mao.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void insertTest(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(280));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(281));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(286));
    }

    @Test
    public void insertListTest(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100,0));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100));
    }

    @Test
    public void updateTest(){
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("中原工学院666");
        discussPostRepository.save(post);
    }

    @Test
    public void deleteTest(){
        //discussPostRepository.deleteById(231);根据ID删除单条数据
        discussPostRepository.deleteAll();//删除所有数据
    }

    @Test
    public void searchByRepositoryTest(){
        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))//查询的条件 以及 查询的范围
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").postTags("<em>").postTags("</em>")
                ).build();

        //      底层调用  eelasticsearchTemplate.queryForPage(searchQuery,class,SearchResultMapper)方法来查询数据
        //底层获取了高亮显示的值但是没有返回.
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalElements());//一共有多少条数据
        System.out.println(page.getTotalPages());//分页页数
        System.out.println(page.getNumber());//当前页数
        System.out.println(page.getSize());//每页多少条数据
        for (DiscussPost post:page){
            System.out.println(post);
        }
    }

    @Test
    public void searchByTemplateTest(){
        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬","title","content"))//查询的条件 以及 查询的范围
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").postTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> page=elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if (hits.getTotalHits()<=0){
                    return null;
                }

                List<DiscussPost> list=new ArrayList<>();
                for (SearchHit hit:hits){
                    DiscussPost post=new DiscussPost();

                    String id=hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId=hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title=hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content=hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status=hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime=hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount=hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    //处理高亮显示的结果
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField!=null){
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    HighlightField contentField = hit.getHighlightFields().get("title");
                    if (contentField!=null){
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }

                return new AggregatedPageImpl(list,pageable,
                hits.getTotalHits(),searchResponse.getAggregations(),searchResponse.getScrollId(),hits.getMaxScore());
            }
        });

        System.out.println(page.getTotalElements());//一共有多少条数据
        System.out.println(page.getTotalPages());//分页页数
        System.out.println(page.getNumber());//当前页数
        System.out.println(page.getSize());//每页多少条数据
        for (DiscussPost post:page){
            System.out.println(post);
        }

    }


}
