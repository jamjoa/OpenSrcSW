package konkuk.jamjoa;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

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
        }

    }
}