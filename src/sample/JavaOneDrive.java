package sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaOneDrive extends JFrame implements ActionListener {
    //設定はconfig.jsonに記載してください。setConfig()参照
    private static String clientID = null;
    private static String scope = null;
    private static final String SIGN_ENDPOINT = "login.live.com/oauth20_authorize.srf";
    private static final String SIGN_OUT_ENDPOINT = "login.live.com/oauth20_logout.srf";
    private static String RESPONSE_TYPE_SING = "code";
    private static String REDIRECT_URL = "https://login.live.com/oauth20_desktop.srf";
    private static String code;


    public static void main(String[] args) {
        //設定の読み込み
        setConfig();

        //手順1：サインインして、認証コードを受け取る
        System.out.println("サインイン用URLです。下のURLをブラウザで検索にかけてください。code=の部分が認証コードです。\nconfig.jsonにご記入ください");
        System.out.println(getSignUrl());

        //サインアウトしたい時はこちら
        System.out.println("サインアウト用URLです。");
        System.out.println(getSignOutUrl());

        //手順2：認証コードからリフレッシュトークンとアクセストークンを取得
        String json =requestAccesstokenByCode();

    }

    private static String requestAccesstokenByCode() {
        //参照：https://docs.microsoft.com/ja-jp/onedrive/developer/rest-api/getting-started/msa-oauth#code-flow
        //リクエストURL生成：httpPostに格納、CloseableHttpClient#execute(httpPost)で実行
        HttpPost httpPost = new HttpPost("https://login.live.com/oauth20_token.srf");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("client_id", clientID));
        nvps.add(new BasicNameValuePair("redirect_uri", REDIRECT_URL));
//        nvps.add(new BasicNameValuePair("client_secret", ""));
        nvps.add(new BasicNameValuePair("code", code));
        nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));

        try {
            //パラメータのセット
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("このパラメータ・文字のエンコーディングはサポートされません。");
        }
        //クライアント立ち上げ：デフォルト設定
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response2 = null;
        try {
            //POST実行とレスポンスの取得
            response2 = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("入出力が失敗しました。もう一度やり直してください。");
        }
        try {
            assert response2 != null;   //nullならエラーを出す。ぬるぽ
            System.out.println(response2.getStatusLine());  //ステータスコードの出力
            System.out.println(Arrays.toString(response2.getAllHeaders()));//ヘッダーの出力

            //Bodyの出力
            HttpEntity entity = response2.getEntity();
            String json = EntityUtils.toString(entity);
            System.out.println(json);

            //メモリリーク対策でメモリを解放
            EntityUtils.consume(entity);

            return json;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("入出力が失敗しました。もう一度やり直してください。");
        } finally {
            try {
                assert response2 != null;//nullならエラーを出す。ぬるぽ
                response2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getSignUrl() {
        //参照：https://docs.microsoft.com/ja-jp/onedrive/developer/rest-api/getting-started/msa-oauth#code-flow
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme("https")
                .setHost(SIGN_ENDPOINT)
                .setParameter("client_id", clientID)
                .setParameter("scope", scope)
                .setParameter("response_type", RESPONSE_TYPE_SING)
                .setParameter("redirect_uri", REDIRECT_URL);
        return uriBuilder.toString();
    }

    private static String getSignOutUrl() {
        //参照：https://docs.microsoft.com/ja-jp/onedrive/developer/rest-api/getting-started/graph-oauth#code-flow
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme("https")
                .setHost(SIGN_OUT_ENDPOINT)
                .setParameter("client_id", clientID)
                .setParameter("redirect_uri", REDIRECT_URL);
        return uriBuilder.toString();
    }

    private static void setConfig() {
        //読み込みにはObjectMapperを使う
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;   //jsonを格納する変数
        try {
            //config.jsonを読み込む
            jsonNode = objectMapper.readTree(new File(".\\src\\sample\\config.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("パスが間違っているか、ファイルが存在しません。");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("入出力が失敗しました。もう一度やり直してください。");
        }
        //フィールドに格納　　""を削除
        clientID = jsonNode.get("client_id").toString().replace("\"", "");
        scope = jsonNode.get("scope").toString().replace("\"", "");
        code = jsonNode.get("code").toString().replace("\"", "");
        //出力
        System.out.println("clientID:" + clientID);//クライアントID(アプリケーションID)
        System.out.println("scope:" + scope);//スコープ
        System.out.println("scope:" + code);//認証コード
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
