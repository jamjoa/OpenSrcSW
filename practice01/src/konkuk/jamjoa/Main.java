package konkuk.jamjoa;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        // write your code here

        //최종 결과 파일이 될 Document타입의 document 만들어줌
        DocumentBuilderFactory docFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
        Document document=docBuilder.newDocument();

        //전체 태그 -> <docs>
        Element docs=document.createElement("docs");
        document.appendChild(docs);

        //파일 id태그
        int index=0;

        //html파일들 있는 폴더(폴더명 2주차 실습 html)에서 파일들 모두 가져오기
        File folder=new File("./2주차 실습 html");
        File[] fileArray=folder.listFiles();

        //파일 개수만큼 반복
        for(File file:fileArray){
            //Jsoup으로 파싱하기위해 org.jsoup.nodes.Document타입으로 파일 따로 열어줌
            org.jsoup.nodes.Document forParsing= Jsoup.parse(file,"utf-8");
            //파일 전체 태그 <doc>
            Element doc=document.createElement("doc");
            docs.appendChild(doc);
            //doc 태그 아이디 설정-> index 변수로
            doc.setAttribute("id",Integer.toString(index));

            //doc 자식 태그 title
            Element title=document.createElement("title");
            doc.appendChild(title);
            //파싱으로 파일의 title태그 내용 가져옴
            String tText=forParsing.title();
            //가져온 내용으로 title 태그의 TextNode 생성
            title.appendChild(document.createTextNode(tText));

            //doc 자식 태그 body
            Element body=document.createElement("body");
            doc.appendChild(body);

            //파일에 있는 p태그 내용만 모두 가져옴
            String allP=forParsing.getElementsByTag("p").text();
            //가져온 내용으로 p태그의 TextNode 생성
            body.appendChild(document.createTextNode(allP));

            //파일 인덱스 갱신
            index++;
        }

        //Document 타입 변수로 xml파일 생성
        TransformerFactory transformerFactory=TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        DOMSource source=new DOMSource(document);
        StreamResult result=new StreamResult(new FileOutputStream(new File("collection.xml")));
        transformer.transform(source,result);

    }
}