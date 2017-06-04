import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Team 3 - Pooja Shah, Aarti Gorade, Shailesh Vajpayee
 * This file extracting the steps from HTML document and parses it
 */


public class HTMLParser {
    private static final String FILENAME = "src/html-text.txt";
    public static Result result = null;
    
    /**
     * This class extracts the steps from HTML page and parses it
     * @param url 
     */
    public static void parseHTMLPage(String url){
    	if(url.length() < 3){
    		System.out.println("NO VALID URL WAS FOUND FOR YOUR QUERY ON GOOGLE! PLEASE TRY AGAIN");
    		System.exit(0);
    	}
        Document doc;
        PrintWriter printWriter = null;
        try{
            doc = Jsoup.connect(url).get();
            Elements e = doc.select(".whb");
            File file = new File(FILENAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            else{
                file.delete();
                file.createNewFile();
            }
            printWriter = new PrintWriter(new FileOutputStream(FILENAME, true));
            for(Element elem : e){
                printWriter.write(elem.text() + "\r\n");
            }
            printWriter.close();
            
            String parserModel = "models/englishPCFG.ser.gz";
            LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
            
            result = TreeParser.parseTree(lp, FILENAME);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}