package org.sanchain.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by A
 * since 14-10-11.
 */
public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);
    Properties config = new Properties();
    private static Config instance = new Config();
    private static String xml;
    private static String language;



    public Config() {
        synchronized (Config.class) {
            loadProperties();
            loadXML();
            loadLanguageFile();
        }
    }

    private void loadLanguageFile(){
        logger.debug("init language file..");
        try{
            String ws = "/language.json";
            InputStream in = getClass().getResourceAsStream(ws);
            if(in == null){
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
            language = "";
            String l;
            while ((l = reader.readLine())!=null){
                language+=l;
            }
        }catch(Exception e){
            logger.info("Read language file error, message is ", e);
        }
    }

    private void loadProperties() {
        String[] fileNames = new String[]{"config.properties"};
        for (String fileName : fileNames) {
            InputStream in;
            in = getClass().getResourceAsStream("/" + fileName);
            try {
                if (in != null)
                    config.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadXML(){
        logger.debug("init xml config file..");
        try{
            String ws = "/websocket.xml";
            InputStream in = getClass().getResourceAsStream(ws);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"));
            xml = "";
            String l;
            while ((l = reader.readLine())!=null){
                xml+=l;
            }
        }catch(Exception e){
            logger.info("Read exclude xml file error, message is ", e);
        }
    }

    public static Config getInstance() {
        return instance;
    }

    public String getProperty(String key) {
        return config.getProperty(key);
    }

    public JSONObject getLanguage(){
        return new JSONObject(language);
    }

    public String getServer(String type) {
        try{
            Document d = DocumentHelper.parseText(xml);
            Element root = d.getRootElement();
            Element auth = root.element(type);
            if(auth != null){
                return auth.getText();
            }else
                return null;
        }catch(Exception e){
            logger.error("get excludes tag error. message is "+e.getMessage());
            return null;
        }
    }


}
