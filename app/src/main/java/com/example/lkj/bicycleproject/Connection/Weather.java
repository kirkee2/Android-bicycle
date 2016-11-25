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

            /*
            Element root = xmlDoc.getDocumentElement();

            NodeList datas = xmlDoc.getElementsByTagName("data");

            new WebHook().execute("ㅁㅇㄴㅁㅇㅁㄴㅇㄴㅁㅁㄴㄴ" + "Aa",null,null);


            for (int i = 0; i < datas.getLength(); i++) {
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

            NodeList datas = xmlDoc.getElementsByTagName("data");

            for (int idx = 0; idx < datas.getLength(); idx++) {
                //필요한 정보들을 담을 변수 생성
                String day = "";
                String hour = "";
                String sky = "";
                String temp = "";
                Node node = datas.item(idx);//data 태그 추출
                int childLength = node.getChildNodes().getLength();
                //자식태그 목록 수정
                NodeList childNodes = node.getChildNodes();
                for (int childIdx = 0; childIdx < childLength; childIdx++) {
                    Node childNode = childNodes.item(childIdx);
                    int count = 0;
                    if(childNode.getNodeType() == Node.ELEMENT_NODE){
                        count ++;
                        //태그인 경우만 처리
                        //금일,내일,모레 구분(시간정보 포함)
                        if(childNode.getNodeName().equals("day")){
                            int su = Integer.parseInt(childNode.getFirstChild().getNodeValue());
                            switch(su){
                                case 0 : day = "금일"; break;
                                case 1 : day = "내일"; break;
                                case 2 : day = "모레"; break;
                            }
                        }else if(childNode.getNodeName().equals("hour")){
                            hour = childNode.getFirstChild().getNodeValue();
                            //하늘상태코드 분석
                        }else if(childNode.getNodeName().equals("wfKor")){
                            sky = childNode.getFirstChild().getNodeValue();
                        }else if(childNode.getNodeName().equals("temp")){
                            temp = childNode.getFirstChild().getNodeValue();
                        }
                    }
                }//end 안쪽 for문
                //result += day+" "+hour+"시 ("+sky+","+temp+"도)\n";
            }//end 바깥쪽 for문

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
