package konkuk.jamjoa;


import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class makeKeyword {
    public makeKeyword(String xmldir) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        //최종 결과 파일이 될 Document타입의 document 만들어줌
        Document outputdoc = docBuilder.newDocument();
        //인자로 들어온 directory의 파싱할 인풋 파일 열어줌
        Document inputdoc = docBuilder.parse(xmldir);

        //전체 태그 추가 -> <docs>
        Element docs = outputdoc.createElement("docs");
        outputdoc.appendChild(docs);

        //doc태그들 리스트
        NodeList docList = inputdoc.getElementsByTagName("doc");
        //index가 곧 doc 태그 id
        for (int index = 0; index < docList.getLength(); index++) {
            //doc태그
            Element curDoc = (Element) docList.item(index);

            //output에 파일 전체 태그 <doc> 추가
            Element doc = outputdoc.createElement("doc");
            docs.appendChild(doc);
            //doc 태그 아이디 설정-> index 변수로
            doc.setAttribute("id", Integer.toString(index));

            //doc에 자식 태그 title 추가
            Element title = outputdoc.createElement("title");
            doc.appendChild(title);
            //title 태그 내용 가져옴
            String tText = curDoc.getElementsByTagName("title").item(0).getTextContent();
            //가져온 내용으로 title 태그의 TextNode 생성
            title.appendChild(outputdoc.createTextNode(tText));
            //doc에 자식 태그 body추가
            Element body = outputdoc.createElement("body");
            doc.appendChild(body);

            //현재 doc 자식 태그 body태그의 내용 가져옴
            String curBody = curDoc.getElementsByTagName("body").item(0).getTextContent();
            KeywordExtractor ke = new KeywordExtractor();
            KeywordList kl = ke.extractKeyword(curBody, true);
            String result = "";
            for (Keyword k : kl)
                result = result.concat(k.getString() + ":" + k.getCnt() + "#");//형태소:출현횟수#
            body.appendChild(outputdoc.createTextNode(result));
        }

        //Document 타입 변수로 xml파일 생성
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource source = new DOMSource(outputdoc);
        StreamResult result = new StreamResult(new FileOutputStream(new File("index.xml")));
        transformer.transform(source, result);
    }
}
