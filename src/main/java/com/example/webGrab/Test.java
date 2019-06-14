package com.example.webGrab;

import com.example.util.CsvExportUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by dugq on 2019-05-13.
 */
public class Test {

    public Set<String> getDetailUrlList(Integer pageIndex) throws IOException {
        String url = "http://app.hicloud.com/search/%E4%BF%9D%E9%99%A9/"+pageIndex;
        Document document = Jsoup.connect(url).timeout(3000).get();
        Elements elements = document.select("a");
        return  elements.stream().filter(element -> {
            String href = element.attr("href");
            return StringUtils.isNotBlank(href) && href.startsWith("/app");
        }).map(element -> element.attr("href")).collect(Collectors.toSet());
    }


    public TestClass  getDetail(String uri){
        TestClass testClass = new TestClass();
        String url = "http://app.hicloud.com"+uri;
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(3000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String appName = document.getElementById("appName").attr("value");
        testClass.setAppName(appName);
        Elements elementsByClass = document.getElementsByClass("ul-li-detail");
        String date = elementsByClass.get(1).selectFirst("span").text();
        String developer = elementsByClass.get(2).selectFirst("span").attr("title");
        Element descElement = document.getElementById("app_strdesc");
        String html = descElement.html();
        html = html.replace("[\\s]+", "");
        html = html.replace("\n", "");
        html = html.replace("<br>", "");
        String desc = html.length()>100?html.substring(0,100):html;
        testClass.setDate(date);
        testClass.setDesc(desc);
        testClass.setDeveloper(developer);
        return testClass;
    }

    @org.junit.Test
    public void test() throws IOException {
        List<TestClass> list = new LinkedList<>();
        for (int i = 0 ; i < 6; i++){
            Set<String> detailUrlList = getDetailUrlList(1);
            List<TestClass> collect = detailUrlList.stream().map(s -> getDetail(s)).collect(Collectors.toList());
            list.addAll(collect);
        }
        File file = new File("/Users/duguoqing/test/app.csv");
        OutputStream out = new FileOutputStream(file);
        CsvExportUtil.export(out,list,TestClass.class);
    }


}
