package com.example.kcb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GeenFox on 2018/12/8.
 */

public class PostUtils extends AppCompatActivity{
    private static Map<String,String> cookies = null;
    public static String LOGIN_URL = "http://219.244.71.113/loginAction.do";
    public static Map<String, String> LoginByPost(String studentName, String studentPwd, String code, Map<String, String> cookies)
    {
        String c = null;
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            c=entry.getValue();
            System.out.println(entry.getKey() + "-" + entry.getValue());
        }
        Connection connection = Jsoup.connect(LOGIN_URL);
        connection.timeout(15*1000);
        connection.cookies(cookies);
        connection.header("Accept","image/webp,image/apng,image/*,*/*;q=0.8");
        connection.header("Accept-Encoding","gzip, deflate");
        connection.header("Accept-Language","zh-CN,zh;q=0.9");
        connection.header("Connection","keep-alive");
        connection.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        connection.header("Referer","http://219.244.71.113/loginAction.do");
        connection.header("Host","219.244.71.113");
        connection.header("Cookie:",c);

        System.out.println("**********************************************Cookies: ");

        try{
            //这里请参数，避免被反爬虫故为空值的参数也加上
            connection.data("zjh1","");
            connection.data("tips","");
            connection.data("lx","");
            connection.data("evalue","");
            connection.data("eflag","");
            connection.data("fs","");
            connection.data("zjh",studentName);
            connection.data("mm",studentPwd);
            connection.data("v_yzm",code);
            Connection.Response resLogin = connection.ignoreContentType(true).method(Connection.Method.POST).execute();
            //登录后得到有权限访问后续网站的cookies
            cookies = resLogin.cookies();
            String body = resLogin.body();
            System.out.println(body);
        }catch(Exception e){e.printStackTrace();}
        return cookies;
    }
}
