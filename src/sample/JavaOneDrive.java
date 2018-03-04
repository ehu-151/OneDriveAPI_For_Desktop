package sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class JavaOneDrive extends JFrame implements ActionListener {
    //設定はconfig.jsonに記載してください。setConfig()参照
    private static String clientID = null;
    private static String scope = null;
    private static final String SIGN_ENDPOINT = "login.live.com/oauth20_authorize.srf";
    private static final String SIGN_OUT_ENDPOINT = "login.live.com/oauth20_logout.srf";
    private static String RESPONSE_TYPE_SING = "code";
    private static String REDIRECT_URL = "https://login.live.com/oauth20_desktop.srf";


    public static void main(String[] args) {
        //設定の読み込み
        setConfig();

        //手順1：サインインして、認証コードを受け取る
        System.out.println("サインイン用URLです。下のURLをブラウザで検索にかけてください。code=の部分が認証コードです。\nconfig.jsonにご記入ください");
        System.out.println(getSignUrl());

        //サインアウトしたい時はこちら
        System.out.println("サインアウト用URLです。");
        System.out.println(getSignOutUrl());

        //手順2：

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
        //出力
        System.out.println("clientID:" + clientID);//クライアントID(アプリケーションID)
        System.out.println("scope:" + scope);//スコープ
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
