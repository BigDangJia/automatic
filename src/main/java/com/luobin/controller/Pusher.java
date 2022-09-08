package com.luobin.controller;

import com.luobin.pojo.Weather;
import com.luobin.utils.CaiHongPiUtils;
import com.luobin.utils.JiNianRiUtils;
import com.luobin.utils.WeatherUtils;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Pusher {

    public static void main(String[] args) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        URL url = Pusher.class.getClassLoader().getResource("maven.yml");
        if (url == null) {
            throw new NullPointerException("yaml获取失败");
        }
        if (url != null) {
            //获取test.yaml文件中的配置数据，然后转换为obj，
            Object obj = yaml.load(new FileInputStream(url.getFile()));
            System.out.println(obj);
            //也可以将值转换为Map
            Map map = (Map) yaml.load(new FileInputStream(url.getFile()));
            System.out.println(map);
            Map jobs = (Map) map.get("jobs");
            System.out.println(jobs);
            Map build = (Map) jobs.get("build");
            System.out.println(build);
            List<Map> steps = (List<Map>) build.get("steps");
            Map env = (Map) steps.get(2).get("with");
            System.out.println(env.get("username"));
            //通过map我们取值就可以了.
        }

        //push();
    }
    private static String appId = "这里改";
    private static String secret = "这里改";



    public static void push(){
        //1，配置
        WxMpInMemoryConfigStorage wxStorage = new WxMpInMemoryConfigStorage();
        wxStorage.setAppId(appId);
        wxStorage.setSecret(secret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxStorage);
        // 推送消息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser("这里改")
                .templateId("这里改")
                .build();
        // 配置你的信息
        Weather weather = WeatherUtils.getWeather();
        templateMessage.addData(new WxMpTemplateData("riqi",weather.getDate() + "  "+ weather.getWeek(),"#00BFFF"));
        templateMessage.addData(new WxMpTemplateData("tianqi",weather.getText_now(),"#00FFFF"));
        templateMessage.addData(new WxMpTemplateData("low",weather.getLow() + "","#173177"));
        templateMessage.addData(new WxMpTemplateData("temp",weather.getTemp() + "","#EE212D"));
        templateMessage.addData(new WxMpTemplateData("high",weather.getHigh()+ "","#FF6347" ));
        templateMessage.addData(new WxMpTemplateData("caihongpi", CaiHongPiUtils.getCaiHongPi(),"#FF69B4"));
        templateMessage.addData(new WxMpTemplateData("lianai", JiNianRiUtils.getLianAi()+"","#FF1493"));
        templateMessage.addData(new WxMpTemplateData("shengri",JiNianRiUtils.getBirthday_Jo()+"","#FFA500"));

        String beizhu = "❤";
        if(JiNianRiUtils.getLianAi() % 365 == 0){
            beizhu = "今天是恋爱" + (JiNianRiUtils.getLianAi() / 365) + "周年纪念日！";
        }
        if(JiNianRiUtils.getBirthday_Jo()  == 0){
            beizhu = "今天是生日，生日快乐呀！";
        }
        templateMessage.addData(new WxMpTemplateData("beizhu",beizhu,"#FF0000"));

        try {
            System.out.println(templateMessage.toJson());
            System.out.println(wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage));
        } catch (Exception e) {
            System.out.println("推送失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
