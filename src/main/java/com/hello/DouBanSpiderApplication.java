package com.hello;

import com.hello.spider.DouBanTop250Spider;

/**
 * 豆瓣Top250爬虫启动类
 * 进度:
 * 1. 年份解析报错, 但已处理。
 * 2. 得到了Movie类, 但是数据还没存。
 */
public class DouBanSpiderApplication {
    public static void main( String[] args ) throws InterruptedException {
        new DouBanTop250Spider().start();
    }
}
