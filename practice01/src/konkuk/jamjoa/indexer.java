package konkuk.jamjoa;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class indexer {

    public void show() throws IOException, ClassNotFoundException {
        FileInputStream fileStream=new FileInputStream("index.post");
        ObjectInputStream objectInputStream=new ObjectInputStream(fileStream);
        Object object = objectInputStream.readObject();

        System.out.println("일어온 객체의 type -> "+object.getClass());

        HashMap<String, List<String>> wgtMap = (HashMap<String, List<String>>)object;

        for (String key : wgtMap.keySet()) {
            System.out.print(key + " -> ");
            List<String> values = wgtMap.get(key);
            System.out.println(values);
        }

        objectInputStream.close();
    }

    public indexer(String path) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        //인자로 들어온 인풋 파일 열어줌
        Document inputdoc = docBuilder.parse(path);

        NodeList docList = inputdoc.getElementsByTagName("doc");

        //전체 문서에서 몇번 나오는지 세기 위한 hashmap
        HashMap<String, Integer> DocMap = new HashMap<>();
        int N = docList.getLength();
        for (int index = 0; index < N; index++) {
            Element curDoc = (Element) docList.item(index);
            //바디 태그 내용 가져옴
            String docBody = curDoc.getElementsByTagName("body").item(0).getTextContent();
            //:와 #으로 구분하므로 0번,2번,4번,... 에 단어가 저장되어 있음
            String[] slices = docBody.split("[:#]");//떡,16,멥쌀,4,...
            for (int i = 0; i < slices.length; i += 2) {
                int value = 1;
                if (DocMap.containsKey(slices[i])) {
                    value = DocMap.get(slices[i]) + 1;
                }
                DocMap.put(slices[i], value);
            }
        }
        //전체 문서에서 각 단어 몇 번 나오는지 셌으니 (dfx) 가중치 계산
        HashMap<String, List<String>> wgtMap = new HashMap<>();
        for (int index = 0; index < N; index++) {
            Element curDoc = (Element) docList.item(index);
            //바디 태그 내용 가져옴
            String docBody = curDoc.getElementsByTagName("body").item(0).getTextContent();
            //:와 #으로 구분하므로 0번,2번,4번,... 에 단어가 저장되어 있음
            String[] slices = docBody.split("[:#]");
            for (int i = 0; i < slices.length; i += 2) {
                int count = DocMap.get(slices[i]);
                double w = Integer.parseInt(slices[i+1]) * Math.log(N / (double)count);
                w=Math.round(w*100)/100.0;
                List<String> wList =wgtMap.get(slices[i]);
                if (wList == null) {
                    wList = new ArrayList<>();
                    wList.add(Integer.toString(index));
                    wList.add(Double.toString(w));
                    wgtMap.put(slices[i],wList);
                }else{
                    wList.add(Integer.toString(index));
                    wList.add(Double.toString(w));
                }

            }
        }

        FileOutputStream fileStream=new FileOutputStream("index.post");
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileStream);
        objectOutputStream.writeObject(wgtMap);
        objectOutputStream.close();
    }
}
