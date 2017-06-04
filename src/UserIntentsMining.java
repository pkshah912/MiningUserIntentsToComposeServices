import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

/**
 *
 * @author Team 3 - Shailesh Vajpayee, Pooja Shah, Aarti Gorade This is a file
 *         that includes main class
 */
public class UserIntentsMining {

	/**
	 * This function acts as a controller for API_finder. It generates the results.
	 * @param nouns
	 * @throws Exception
	 */
	public static void lucene(List<String> nouns) throws Exception {
		System.out.println("\nParsing Database...");
		Parser_db parse_db = new Parser_db("api.txt");
		HashSet<String> categories = parse_db.categories;
		System.out.println("Database parsed! Total API categories: " + categories.size()+"\n");
		ArrayList<API> result = new ArrayList<>();
		int count = 0;
		int percent= 0;
		System.out.print("Creating indexes and searching for APIs...");
		for (String i : categories) {
			count++;
			if(count%13 == 0){
				percent += 20;
				System.out.print(percent + "%..");
			}
			Api_Finder api_finder = new Api_Finder();
			String index_path = "Index/" + i + "/";
			File dir = new File(index_path);
			dir.mkdirs();
//			System.out.println("category: " + i);
			String file_path = "Categorized/" + i + "/";
			String output = api_finder.API_getter(nouns, index_path, file_path, i);
			if (output.length() > 3) {
				String[] res = output.split("\\[#\\]");
				API api = new API(res[0], res[1], res[2], i, res[3]);
				result.add(api);
				// System.out.println("till now :" + result);
//				System.out.println();
			}
			// Thread.sleep(10);
		}
		// System.out.println(result);
//		System.out.println(result.size());
		System.out.println("Done\n");
		Collections.sort(result, new Comparator<API>() {
			public int compare(API a1, API a2) {
				return a1.score.compareTo(a2.score);
			}
		});
		System.out.println("Top 10 APIs for you are: -");
		for (int i = result.size() - 1; i > result.size() - 11; i--) {
			System.out.println((result.size() - i) + ") " +result.get(i));
		}
	}

	/**
	 * The main function of the class
	 * @param args Command line arguments ignored
	 */
	@SuppressWarnings(value = { "all" })
	public static void main(String args[]) {
		try {
			PrintWriter printWriter = null;
			HashSet<String> nouns = new HashSet<String>();
			String fileName = "src/input-text.txt";
			Scanner sc = new Scanner(System.in);
			System.out.println("Please enter your query (eg:book a flight for travel): ");
			String userInputGoal = sc.nextLine();
			sc.close();
			userInputGoal.toLowerCase();
			userInputGoal += " wikihow"; // for wikihow links
			File file = new File(fileName);

			if (!file.exists()) {
				file.createNewFile();

			} else {
				file.delete();
				file.createNewFile();
			}

			printWriter = new PrintWriter(fileName, "UTF-8");
			printWriter.println(userInputGoal);
			printWriter.close();
			
			String parserModel = "models/englishPCFG.ser.gz";
			LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
			
			Result result = TreeParser.parseTree(lp, fileName);
			List<String> npList = result.getNPList();
			List<String> vbList = result.getVBList();
			String nounPhrase = "";
			for (String np : npList) {
				nounPhrase += np + " ";
			}

			String queryInput = nounPhrase;
			CustomSearchAPIInvocation.invokeAPI(queryInput);
			HTMLParser.parseHTMLPage(CustomSearchAPIInvocation.webSiteSource);
			List<String> nnList = HTMLParser.result.getNNList();
//			System.out.println("Nouns:" + nnList.toString());
			nouns.addAll(nnList);
			System.out.println("Generating query from input and the parsed nouns from HTML pages...");
			lucene(new ArrayList<String>(nouns));
		} catch (Exception ex) {
		}
	}
}

/*
 * OUTPUT Please enter your goal: 
 * 1) I would like to get a server which will allow me remote access
 * 2) book a flight for travel
 * 3) find me an online hotel booking service
 */
