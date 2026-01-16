package com.hello.spider;


import cn.hutool.json.JSONUtil;
import com.hello.entity.Movie;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DouBanTop250Spider {
    private static final List<Movie> MOVIE_LIST = new java.util.ArrayList<>();

    int index = 0;

    public void start() throws InterruptedException {
        log.info("开始爬取豆瓣电影TOP250,当前时间 + {}", System.currentTimeMillis());
        int start = 0;
        String baseUrl = "https://movie.douban.com/top250?start=" + start +"&filter=";

        for (int i = 0; i < 10; i++){
            log.info("开始爬取第{}页,当前时间 + {}", i + 1, System.currentTimeMillis());
            spiderCurrentPage(baseUrl);
            start += 25;
            index++;
            Thread.sleep(2000);
        }

        saveMovieListWithBytes();

        log.info("爬取豆瓣电影TOP250结束,当前时间 + {}", System.currentTimeMillis());
    }

    /**
     * 爬取当前页面
     * @param baseUrl 请求地址
     */
    private void spiderCurrentPage(String baseUrl) {
        try {
            // 当前页面
            Document document = Jsoup.connect(baseUrl).get();
            // 获取当前页面的item
            Elements items = document.select(".item");
            for (int i = 0; i < items.size(); i++){
                spiderCurrentItem(items.get(i));
            }
        } catch (IOException e) {
            log.error("爬取豆瓣电影TOP250异常,当前时间 + {}", System.currentTimeMillis());
        }
    }

    /**
     * 爬取当前item
     * @param item 电影栏目
     */
    private void spiderCurrentItem(Element item) {
        Movie movie = new Movie();

        // 排名
        Element ranking = item.select("em").first();
        Optional.ofNullable(ranking).ifPresent(element -> movie.setRanking(25 * index + Integer.parseInt(element.text().trim())));

        // 中文名称
        Element titleChinese = item.select(".title").first();
        Optional.of(titleChinese).ifPresent(element -> movie.setNameChinese(element.text().trim()));

        // 外文名称
        Element titleForeign = item.select(".title").last();
        Optional.of(titleForeign).ifPresent(element -> movie.setNameForeign(element.text().trim()));

        // 其它名称
        Element titleOther = item.select(".other").first();
        Optional.ofNullable(titleOther).ifPresent(element -> movie.setNameOther(element.text().trim()));

        // 导演
        Element directorElement = item.select(".bd p").first();
        String[] directorStar = directorElement.text().split("主演:");
        for (int i = 0; i < directorStar.length; i++){
            if (i == 0){
                movie.setDirector(directorStar[i].trim());
            } else if (i == 1){
                movie.setStar(directorStar[i].trim());
            }
        }

        // 年份、国家、类型
        String[] yearCountryType = directorElement.html().split("\\n");
        if (yearCountryType.length > 1){
            String[] split = yearCountryType[1].split("/");
            for (int i = 0; i < split.length; i++){
                if (i == 0){
                    movie.setYear(split[i].trim());
                } else if (i == 1){
                    movie.setCountry(split[i].trim());
                } else if (i == 2){
                    movie.setType(split[i].trim());
                }
            }
        }

        // 评分
        Element score = item.select(".rating_num").first();
        Optional.ofNullable(score).ifPresent(element -> movie.setScore(element.text().trim()));

        // 评价人数
        Element scoreNumElement = item.select(".bd div span").get(3);
        int scoreNum  = Integer.parseInt(scoreNumElement.text().replaceAll("[^0-9]", ""));
        movie.setScoreNum(scoreNum);

        // 台词
        Element dialogue = item.select(".quote span").first();
        Optional.ofNullable(dialogue).ifPresent(element -> movie.setDialogue(element.text().trim()));

        // 图片地址
        Element img = item.select(".item .pic a img").first();
        Optional.ofNullable(img).ifPresent(element -> movie.setImgUrl(element.attr("src")));

        MOVIE_LIST.add(movie);
    }

    private void saveMovieList() {
        try {
            FileWriter fileWriter = new FileWriter("D:\\webMagic\\DouBan-Spider\\movie" + System.currentTimeMillis() + ".txt");
            String jsonStr = JSONUtil.toJsonStr(MOVIE_LIST);
            fileWriter.write(jsonStr);
        } catch (IOException e) {
            log.error("保存文件异常,当前时间 + {}", System.currentTimeMillis());
        }

    }

    private void saveMovieListWithBytes() {
        String fileName = "D:\\webMagic\\DouBan-Spider\\movie_" +
                System.currentTimeMillis() + ".txt";

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            String jsonStr = JSONUtil.toJsonStr(MOVIE_LIST);
            byte[] bytes = jsonStr.getBytes(StandardCharsets.UTF_8);

            log.info("准备写入 {} 字节", bytes.length);

            // 直接写入字节数组
            fos.write(bytes);
            fos.flush();

            // 检查文件
            File file = new File(fileName);
            log.info("实际写入: {} 字节", file.length());

            if (file.length() != bytes.length) {
                log.error("字节数不匹配！可能写入被中断");
            }

        } catch (Exception e) {
            log.error("保存失败", e);
        }
    }
}
