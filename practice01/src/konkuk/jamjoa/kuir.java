package konkuk.jamjoa;


import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;


public class kuir {

    public static void main(String[] args) throws ParserConfigurationException, TransformerException, SAXException, IOException, ClassNotFoundException {

        if(args[0].equals("-c")){
            makeCollection makeCollection = new makeCollection(args[1]);
        }
        if(args[0].equals("-k")){
            makeKeyword makeKeyword=new makeKeyword(args[1]);
        }if(args[0].equals("-i")){
            indexer indexer=new indexer(args[1]);
            //index.post 파일 확인
            //indexer.show();
        }if(args[0].equals("-s")){
            searcher searcher=new searcher(args[1]);

            if(args[2].equals("-q")){
                Double[] similarities = searcher.calcSimilarity(args[3]);
                boolean[] check=new boolean[similarities.length];
//                for(int i=0;i<similarities.length;i++){
//                    System.out.println(similarities[i]);
//                }
                Double[] sorted= Arrays.copyOf(similarities, similarities.length);
                Arrays.sort(sorted, Collections.reverseOrder());

//                for(int i=0;i<3;i++){
//                    System.out.println(sorted[i]);
//                }

                String collectionDir=args[1].replace("index.post","collection.xml");//문서 몇개인지
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document inputDoc = docBuilder.parse(collectionDir);
                NodeList docList = inputDoc.getElementsByTagName("doc");

                for(int j=0;j<3;j++){
                    for(int i=0;i<similarities.length;i++) {
                        if (similarities[i].equals(sorted[j]) && !check[i]) {
                            String tText = ((Element) docList.item(i)).getElementsByTagName("title").item(0).getTextContent();
                            System.out.println(tText);
                            check[i] = true;
                        }
                    }
                }

            }
        }

    }
}