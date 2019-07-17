package cn.hkxj.platform.spider;

import cn.hkxj.platform.exceptions.UrpRequestException;
import cn.hkxj.platform.exceptions.UrpTimeoutException;
import cn.hkxj.platform.spider.model.VerifyCode;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.util.Arrays;
import java.util.Scanner;

@Slf4j
public class NewUrpSpider {
    private static final String ROOT = "http://222.171.146.55";
    private static final String CAPTCHA = ROOT + "/img/captcha.jpg";
    private static final String CHECK = ROOT + "/j_spring_security_check";
    private static final String INDEX = ROOT + "/index.jsp";
    private static final String LOGIN = ROOT + "/login";
    private static final String STUDENT_INFO = ROOT + "/student/rollManagement/rollInfo/index";
    private static final CookieManager manager = new CookieManager();
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new UrpCookieJar())
            .retryOnConnectionFailure(true)
            .addInterceptor(new RetryInterceptor(5))
            .build();

    private final static Headers HEADERS = new Headers.Builder()
            .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
            .add("Host", "xsurp.usth.edu.cn")
            .add("Connection", "keep-alive")
            .add("Accept-Encoding", "gzip, deflate")
            .add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
            .add("Upgrade-Insecure-Requests", "1")
            .add("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            .add("Cache-Control", "max-age=0")
            .build();

    public VerifyCode getCaptcha() {
        Request request = new Request.Builder()
                .url(CAPTCHA)
                .header("Referer", LOGIN)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
                .get()
                .build();

        return new VerifyCode(execute(request));

    }


    public void studentCheck(String account, String password, String captcha){
        FormBody.Builder params = new FormBody.Builder();
        FormBody body = params.add("j_username", account)
                .add("j_password", password)
                .add("j_captcha", captcha)
                .build();

        Request request = new Request.Builder()
                .url(CHECK)
                .post(body)
                .build();

        new String(execute(request));
        log.info("success");
    }


    private void loginPage(){
        Request request = new Request.Builder()
                .url(LOGIN)
                .get()
                .headers(HEADERS)
                .build();

        log.info(Arrays.toString(execute(request)));
    }


    public void login(){
        Request request = new Request.Builder()
                .url(STUDENT_INFO)
                .get()
                .build();

        log.info(new String(execute(request)));
    }

    private void paseUserInfo(){
        File file = new File("userinfo.html");

    }



    private byte[] execute(Request request){
        try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful() || response.body() == null){
                throw new UrpRequestException("url: "+ request.url().toString()+ " code: "+response.code()+" cause: "+ response.message());
            }
            return response.body().bytes();
        } catch (IOException e) {
            throw new UrpTimeoutException(request.url().toString(), e);
        }
    }


    public static void main(String[] args) {
        MDC.put("cookieTrace", "trace");
        NewUrpSpider spider = new NewUrpSpider();
        VerifyCode captcha = spider.getCaptcha();
        captcha.write("pic.jpg");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();
        System.out.println(code);
        spider.studentCheck("2014025838", "1", code);
        spider.login();
    }
}
