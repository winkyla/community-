package com.mao.community.util;

public class RedisKeyUtil {
    private static final String SPLIT=":";//分隔符
    private static final String PREFIX_ENTITY_LIKE="like:entity";//点赞实体前缀
    private static final String PREFIX_USER_LIKE="like:user";//用户点赞前缀
    private static final String PREFIX_FOLLOWEE="followee";//被关注的目标
    private static final String PREFIX_FOLLOWER="follower";//粉丝
    private static final String PREFIX_KAPTCHA="kaptcha";//验证码
    private static final String PREFIX_TICKET="ticket";//登陆凭证
    private static final String PREFIX_USER="user";//登陆凭证
    private static final String PREFIX_UV="uv";//独立用户，根据用户的ip
    private static final String PREFIX_DAU="dau";//每日活跃用户，根据用户的id
    private static final String PREFIX_POST="post";





    //某个实体的赞
    // 形式 like:entity:entityType:entityId
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //某个用户的赞
    // like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    //某个用户关注的实体
    //followee:userId:entityType -> zset (entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId ->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //登录验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }

    //登陆的凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }

    //单日UV
    public static String getUVKey(String date){
        return PREFIX_UV+SPLIT+date;
    }

    //区间UV
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV+SPLIT+startDate+SPLIT+endDate;
    }

    //单日活跃用户
    public static String getDAUKey(String date){
        return PREFIX_DAU+SPLIT+date;
    }

    //区间活跃用户
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU+SPLIT+startDate+SPLIT+endDate;
    }

    //帖子分数
    public static String getPostScoreKey(){
        return PREFIX_POST+SPLIT+"score";
    }

}
