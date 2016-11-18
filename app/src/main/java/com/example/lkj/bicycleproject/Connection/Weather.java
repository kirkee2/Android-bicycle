package com.example.lkj.bicycleproject.Connection;

import com.example.lkj.bicycleproject.Structure.WeatherStructure;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by leegunjoon on 2016. 11. 18..
 */

public class Weather {
    private double latitude;
    private double longitude;
    private ArrayList<WeatherStructure> weatherStructure = new ArrayList<WeatherStructure>();

    public Weather(double latitude,double longitude) {
        try {
            new WebHook().execute("들어옴",null,null);
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = f.newDocumentBuilder();

            Document xmlDoc = null;
            String url = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx="+latitude+"&gridy="+longitude;
            xmlDoc = parser.parse(url);

            Element root = xmlDoc.getDocumentElement();

            Node xmlNode1 = root.getElementsByTagName("header").item(0);
            new WebHook().execute("ㅁㅇㄴㅁㅇㅁㄴㅇㄴㅁㅁㄴㄴ" + "Aa",null,null);

            /*
            for (int i = 0; i < temp.length; i++) {
                Node xmlNode1 = root.getElementsByTagName("data").item(i);

                Node xmlNode21 = ((Element) xmlNode1).getElementsByTagName(
                        "temp").item(0);
                Node xmlNode22 = ((Element) xmlNode1).getElementsByTagName(
                        "wfEn").item(0);
                Node xmlNode23 = ((Element) xmlNode1).getElementsByTagName(
                        "hour").item(0);

                temp[i] = xmlNode21.getTextContent();
                wfEn[i] = xmlNode22.getTextContent();
                hour1[i] = "기준시각 : " + xmlNode23.getTextContent() + "시";
            }
            */

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            new WebHook().execute("에러",null,null);
        }
    }

    public ArrayList<WeatherStructure> getWeatherStructure() {
        return weatherStructure;
    }
}
