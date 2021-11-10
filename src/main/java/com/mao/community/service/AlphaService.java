package com.mao.community.service;

import com.mao.community.dao.AlphaDao;
import com.mao.community.dao.DiscussPostMapper;
import com.mao.community.dao.UserMapper;
import com.mao.community.entity.DiscussPost;
import com.mao.community.entity.User;
import com.mao.community.util.CommunityUtil;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
//@Scope("prototype")//每次实例化对象都新建一个实例，默认括号内的内容是singleton，一般都是单例的
public class AlphaService {

    private static final Logger logger=LoggerFactory.getLogger(AlphaService.class);

    @Autowired
   // @Qualifier("alphaHibernate") //可以指定注入的Dao依赖的实现类。
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    public AlphaService(){//构造器
        System.out.println("实例化AlphaService");
    }

    @PostConstruct//初始化方法会在构造器之后调用
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy//在销毁对象之前调用他
    public void destory(){//在销毁对象之前调用他，可以释放资源
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }

    // REQUIRED:支持当前事务（外部事物），如果不存在则创建新事务。
    // REQUIRES_NEW:创建一个新事务，并且暂停当前事务（外部事物）。
    // NESTED:如果当前存在事务（外部事物），则嵌套在该事务中执行（有独立的提交和回滚），否则就会和REQUIRED一样。
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation= Propagation.REQUIRED)
    public Object save1(){
        //新增用户
        User user=new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("https://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //新增帖子
        DiscussPost post=new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello Alpha");
        post.setContent("新人报到!");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "OK";
    }

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                //新增用户
                User user=new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("https://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                //新增帖子
                DiscussPost post=new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("Hello Beta");
                post.setContent("我是新人!");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");

                return "OK";
            }
        });

    }

    //让该方法在多线程环境下，被异步的调用
    @Async
    public void execute1(){
        logger.debug("execute1");
    }

   // @Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2(){
        logger.debug("execute2");
    }










}
