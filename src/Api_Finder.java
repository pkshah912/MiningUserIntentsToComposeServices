
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Team 3 - Shailesh Vajpayee, Pooja Shah, Aarti Gorade
 * This class uses lucene libraries to search the database and give the most appropriate API.
 */
public class Api_Finder {

	// index
	StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
	IndexWriter writer;
	ArrayList<File> Q = new ArrayList<File>();
	String index_location;
	Scanner s = new Scanner(System.in);
	FSDirectory file_directory;
	IndexWriterConfig index_writer_config;
	String files_location;
	FileReader file_reader = null;
	// search
	IndexReader index_reader = null;
	IndexSearcher index_searcher = null;
	TopScoreDocCollector index_collector = null;
	Query query = null;

	public Api_Finder() {

	}

	/**
	 * This function adds files to the queue
	 *
	 * @param file
	 */
	public void Files_to_queue(File file) {
		if (file.isDirectory()) {
			for (File files : file.listFiles()) {
				Files_to_queue(files);
			}
		} else {
			if (file.getName().toLowerCase().endsWith(".txt")) {
				Q.add(file);
			}
		}
	}

	/**
	 * This function is used to create an index for the queue of files
	 */
	public void create_index_for_files() {
		Files_to_queue(new File(files_location));
		for (File file : Q) {
			try {
				Document doc = new Document();
				file_reader = new FileReader(file);
				BufferedReader br = new BufferedReader(file_reader);
				String current_line = "";
				StringBuffer sb = new StringBuffer();
				while ((current_line = br.readLine()) != null) {
					if (!current_line.contains("null")) {
						sb.append(current_line + "\n");
					}
				}
				// System.out.println(sb.toString());
				doc.add(new TextField("contents", sb.toString(), Field.Store.YES));
				doc.add(new StringField("filepath", file.getAbsolutePath(), Field.Store.YES));
				writer.addDocument(doc);
			} catch (Exception e) {
				System.out.println("Error while adding " + file);
			}
		}
	}

	/**
	 * This function is used to query the database
	 *
	 * @param query_string
	 *            The string to be queried
	 * @return The API
	 * @throws Exception
	 */
	public String query_index(String query_string) throws Exception {
		index_reader = DirectoryReader.open(FSDirectory.open(new File(index_location)));
		index_searcher = new IndexSearcher(index_reader);
		index_collector = TopScoreDocCollector.create(1, true);
		query = new QueryParser(Version.LUCENE_40, "contents", analyzer).parse(query_string);
		index_searcher.search(query, index_collector);
		ScoreDoc[] matches = index_collector.topDocs().scoreDocs;
		return get_api(matches);
	}

	/**
	 * This function gets the API.
	 *
	 * @param matches
	 *            The matches array
	 * @return
	 * @throws Exception
	 */
	public String get_api(ScoreDoc[] matches) throws Exception {
		if (matches.length == 0) {
//			System.out.println("No significant match found!");
		} else {
			Document doc = index_searcher.doc(matches[0].doc);
			String filepath = "";
			int ind = 0;
			for (int i = 0; i < matches.length; i++) {
				int docId = matches[i].doc;
				Document d = index_searcher.doc(docId);
				if (doc.get("contents") != null) {
					filepath = doc.get("filepath");
					ind = filepath.lastIndexOf("/");
//					System.out.println("API matched: " + filepath.substring(ind + 1) + "\nscore: " + matches[0].score);
				}
			}
			return filepath.substring(ind + 1) + " " + matches[0].score;
		}
		return "";

	}
	
	/**
	 * This function controls the indexing process and calls query functions
	 * @param index The index location
	 * @param file The files location
	 * @param category Category of the APIs
	 * @param statement The Nouns and verbs
	 * @return The appropriate API info
	 * @throws Exception
	 */
	public String indexer(String index, String file, String category, List<String> statement) throws Exception{
		index_location = index;
		file_directory = FSDirectory.open(new File(index_location));
		index_writer_config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		writer = new IndexWriter(file_directory, index_writer_config);
		files_location = file;
		create_index_for_files();
//		System.out.println("Created index at : " + index);
		writer.close();
		String query = "";
		for (int i = 0; i < statement.size(); i++) {
			if (!statement.get(i).contains("/")) {
				if (statement.size() > 1) {
					if (i != (statement.size() - 1)) {
						query += statement.get(i) + " or ";
					} else {
						query += statement.get(i) + " ";
					}
				} else {
					query += statement.get(i);
				}
			}
		}
//		System.out.println("Query input: " + query);
//		System.out.println("Searching...");
		String api_file = query_index(query);
		if(api_file.length() == 0){
			return "";
		}
//		System.out.println("File is at " + file + api_file);
		try{
			FileReader result = new FileReader(new File(file + api_file.split(" ")[0]));
			BufferedReader result_br = new BufferedReader(result);
			String API_link = result_br.readLine();
			String API_title = result_br.readLine();
			String API_name = result_br.readLine();
			return API_name+"[#]"+API_link + "[#]" + api_file.split(" ")[1] + "[#]" + api_file.split(" ")[0];
		}
		catch (Exception e) {
//			System.out.println("FILE NOT FOUND!!!!!!!!!!!");
//			e.printStackTrace();
		}
		return "";
	}
	

	/**
	 * The API_getter is the function which calls functions to create index and
	 * query the database
	 *
	 * @param statement
	 * @return
	 * @throws Exception
	 */
	public String API_getter(List<String> statement, String index_l, String file_l, String category) throws Exception {
		String result = "";
		result += "\n" + indexer(index_l, file_l, category, statement);
		return result;
	}

	/**
	 * The main function of this class
	 *
	 * @param args
	 * @throws Exception
	 */
//	public static void main(String[] args) throws Exception {
//		ArrayList<String> query = new ArrayList<>();
//		query.add("server");
//	}

}
