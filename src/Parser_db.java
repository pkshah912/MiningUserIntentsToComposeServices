
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Team 3 - Shailesh Vajpayee, Pooja Shah, Aarti Gorade
 * This file is used to Parse the database.
 *
 */
public class Parser_db {

    private static int api_count;
    public static HashSet<String> categories;

    public Parser_db() {
    }

    /**
     * The parameterized constructor which calls the FileProcessor
     *
     * @param filename
     */
    public Parser_db(String filename) {
    	categories = new HashSet<>();
        try {
            File_Processor(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This function parses the lines
     *
     * @param line
     */
    public static void parse_line(String line) {
        String[] l = line.split("\\$\\#\\$");
        File_Writer(l);
    }

    /**
     * The File Writer
     *
     * @param line
     */
    public static void File_Writer(String[] line) {
		try {
			api_count++;
            int count = 0;
            String category = "";
            for(String s:line){
            	count++;
            	if(count == 19){
            		category = s;
            		break;
            	}
            }
            categories.add(category);
            String path = "Categorized/"+category+"/";
    		File dir = new File(path);
    		dir.mkdirs();
    		File file = new File(path+"api"+api_count+".txt");
    		try {
    			file.createNewFile();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            for(String s:line){
            	if(s.length() == 0||s.equals(" "))
            		bw.write("null\n");
            	else
            		bw.write(s.toString() + "\n");
            }
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * File processor
     *
     * @param filename the file name
     * @return data read from file
     * @throws Exception
     */
    public static void File_Processor(String filename) throws Exception {
        api_count = 0;
        FileReader file = new FileReader(filename);
        BufferedReader br = new BufferedReader(file);
        String line = "";
        while ((line = br.readLine()) != null) { // for all apis
//        for(int i = 0; i < 10; i++){ // testing for 10 apis
            line = br.readLine();
            parse_line(line);
        }
        br.close();
    }

//    public static void main(String[] args) throws Exception {
//        File_Processor("api.txt");
//    }
}
