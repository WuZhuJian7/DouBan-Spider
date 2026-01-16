package com.hello.entity;

import lombok.Data;

@Data
public class Movie {
    /**
     * 排名
     */
    private Integer ranking;

    /**
     * 中文名称
     */
    private String nameChinese;

    /**
     * 外文名称
     */
    private String nameForeign;

    /**
     * 其它名称
     */
    private String nameOther;

    /**
     * 导演
     */
    private String director;

    /**
     * 主演
     */
    private String star;

    /**
     * 年份
     */
    private String year;

    /**
     * 国家
     */
    private String country;

    /**
     * 类型
     */
    private String type;

    /**
     * 评分
     */
    private String score;

    /**
     * 评价人数
     */
    private int scoreNum;

    /**
     * 台词
     */
    private String dialogue;

    /**
     * 图片地址
     */
    private String imgUrl;
}
