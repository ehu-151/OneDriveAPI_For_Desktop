package sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class JavaOneDrive extends JFrame implements ActionListener {
    //設定はconfig.jsonに記載してください。setConfig()参照
    private static String CLIENT_ID = null;
    static String json = "";

    public static void main(String[] args) {
        json = setConfig();
        System.out.println(CLIENT_ID);
        String path = new File(".").getAbsoluteFile().getParent();
        System.out.println(path);
        //クライアントID(アプリケーションID)

    }

    public static String setConfig() {
        //読み込みにはObjectMapperを使う
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;   //jsonを格納する変数
        try {
            //config.jsonを読み込む
            jsonNode = objectMapper.readTree(new File(".\\src\\sample\\config.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("パスが間違っているか、ファイルが存在しません。");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("入出力が失敗しました。もう一度やり直してください。");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("入出力が失敗しました。もう一度やり直してください。");
        }
        //フィールドに格納
        CLIENT_ID = jsonNode.get("CLIENT_ID").toString();
        return "";
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
