package konkuk.jamjoa;


import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {

    public static void main(String[] args) throws ParserConfigurationException, TransformerException, SAXException, IOException {

        if(args[0].equals("-c")){
            makeCollection makeCollection = new makeCollection(args[1]);
        }
        if(args[0].equals("-k")){
            makeKeyword makeKeyword=new makeKeyword(args[1]);
        }

    }
}