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
    int docCnt=0;
    public searcher(String dir) {
        this.dir=dir;
    }

    public String[][] getKeywordsList(String query){
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);

        String[][] keywords=new String[kl.size()][2];

        int count=0;
        for (Keyword k : kl){
            keywords[count][0]=k.getString();
            keywords[count][1]=Integer.toString(1);
            count++;
        }
        return keywords;
    }

    public double[][] getWeight(String[][] keywords) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException{
        String collectionDir=dir.replace("index.post","collection.xml");//문서 몇개인지
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document inputdoc = docBuilder.parse(collectionDir);
        NodeList docList = inputdoc.getElementsByTagName("doc");
        this.docCnt=docList.getLength();
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
        return weights;
    }

    public void printWeight(double[][] weights){
        System.out.println("===============================");
        System.out.print(" "+"\t");
        for(int i=0;i<weights[0].length;i++){
            System.out.print(i+"\t");
        }
        System.out.println();
        for(int i=0;i< weights.length;i++){
            System.out.print(i+"\t");
            for(int j=0;j< weights[i].length;j++){
                System.out.print(weights[i][j]+"\t");
            }
            System.out.println();
        }
        System.out.println("===============================");
    }


    public Double[] calcSimilarity(String query) throws ClassNotFoundException, ParserConfigurationException, SAXException, IOException {

        Double[] similarities=this.InnerProduct(query);
        String[][] keywords=this.getKeywordsList(query);
        double[][] weights=this.getWeight(keywords);
        //printWeight(weights);

        double queryMag=0;//쿼리의 크기
        for(int i=0;i<keywords.length;i++){
            int w=Integer.parseInt(keywords[i][1]);
            queryMag+=w*w;
        }
        queryMag=Math.sqrt(queryMag);

        for(int i=0;i<docCnt;i++){
            double docMag=0;
            for(int j=0;j< keywords.length;j++){
                double w=weights[j][i];
                docMag+=w*w;
            }
            docMag=Math.sqrt(docMag);
            if(similarities[i]!=0) {
                similarities[i] = similarities[i] / (queryMag * docMag);
                similarities[i]=Math.round(similarities[i]*100)/100.0;
            }
        }
//        for(double d : similarities){
//            System.out.println(d);
//        }
        return similarities;
    }

    public Double[] InnerProduct(String query) throws ClassNotFoundException, ParserConfigurationException, SAXException, IOException {

        String[][] keywords=this.getKeywordsList(query);
        double[][] weights=this.getWeight(keywords);

        Double[] similarities=new Double[docCnt];
        for(int i=0;i< docCnt;i++){
            double similarity=0;
            for(int j=0;j<keywords.length;j++){
                similarity+=Integer.parseInt(keywords[j][1])*weights[j][i];
            }
            similarity=Math.round(similarity*100)/100.0;
            similarities[i]=similarity;
        }
//        System.out.println("=====inner Product 결과=====");
//        for(double d : similarities){
//            System.out.println(d);
//        }
//        System.out.println("===========================");
        return similarities;

    }
}
