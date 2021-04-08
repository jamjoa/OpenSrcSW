package konkuk.jamjoa;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

public class searcher {
    String dir;
    public searcher(String dir) {
        this.dir=dir;
    }

    public Double[] InnerProduct(String query) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException {

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);

        String[][] keywords=new String[kl.size()][2];

        int count=0;
        for (Keyword k : kl){
            keywords[count][0]=k.getString();
            keywords[count][1]=Integer.toString(1);
            count++;
        }

        String collectionDir=dir.replace("index.post","collection.xml");//문서 몇개인지
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document inputdoc = docBuilder.parse(collectionDir);
        NodeList docList = inputdoc.getElementsByTagName("doc");

        double[][] weights=new double[keywords.length][docList.getLength()]; //모두 0

        FileInputStream fileStream = new FileInputStream(dir);
        ObjectInputStream objectInputStream=new ObjectInputStream(fileStream);
        Object object = objectInputStream.readObject();
        HashMap<String, List<String>> wgtMap = (HashMap<String, List<String>>)object;

        for(int i=0;i< keywords.length;i++){
            List<String> values=wgtMap.get(keywords[i][0]);
            for(int j=0;j<values.size();j+=2){
                weights[i][Integer.parseInt(values.get(j))]=Double.parseDouble(values.get(j+1));
            }
        }
        objectInputStream.close();

        Double[] similarities=new Double[docList.getLength()];

        for(int i=0;i< docList.getLength();i++){
            double similarity=0;
            for(int j=0;j<keywords.length;j++){
                similarity+=Integer.parseInt(keywords[j][1])*weights[j][i];
            }
            similarity=Math.round(similarity*100)/100.0;
            similarities[i]=similarity;

        }
        return similarities;

    }
}
