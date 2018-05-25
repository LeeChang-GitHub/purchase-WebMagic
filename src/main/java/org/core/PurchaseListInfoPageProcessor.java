package org.core;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.util.Arrays;
import java.util.List;

/**
 * 公开采招列表页
 */
public class PurchaseListInfoPageProcessor implements PageProcessor {

    private static final String doMain = "http://jxsggzy\\.cn/";

    private static String URL = "http://jxsggzy.cn//web/jyxx/002001/002001001/1.html";

    private Site site = Site
            .me()
            .setDomain(doMain)
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    @Override
    public void process(Page page) {
        List<String> links = page.getHtml().xpath("//div[@class='paging']/ul/li[@class='nextlink']/a").links().regex("/web/jyxx/002001/002001001/\\d+\\.html").all();
        System.out.println(links);
        page.addTargetRequests(links);
        //URL = page.getTargetRequests().get(0).getUrl();
        System.out.println(page.getTargetRequests().get(0).getUrl());
        //获取当前标签的主题和路径和时间
        page.putField("href", page.getHtml().xpath("//li[@class='ewb-list-node clearfix']/a/@href").all().toString());
        page.putField("name", page.getHtml().xpath("//li[@class='ewb-list-node clearfix']/a/text()").all().toString());
        page.putField("date", page.getHtml().xpath("//li[@class='ewb-list-node clearfix']/span/text()").all().toString());

        List<String> listURL = getResultItemsByKey(page,"href");
        List<String> listName = getResultItemsByKey(page,"name");
        List<String> listDate = getResultItemsByKey(page,"date");

        if (page.getResultItems().get("href")==null){
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //Spider是爬虫的入口类,addurl为入口url
        Spider oschinaSpider = Spider.create(new PurchaseListInfoPageProcessor()).addUrl(URL)
                //Pipeline是结果输出和持久化的接口，这里ConsolePipeline表示结果输出到控制台
                .addPipeline(new ConsolePipeline());
        try {
            //添加到JMT监控中
            SpiderMonitor.instance().register(oschinaSpider);
            //设置线程数
            //oschinaSpider.thread(5);
            oschinaSpider.run();
        } catch (JMException e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装后方便列表取值
     * @param page
     * @param key
     * @return
     */
    private List<String> getResultItemsByKey(Page page,String key){
        return Arrays.asList(page.getResultItems().get(key).toString().split(","));
    }

}