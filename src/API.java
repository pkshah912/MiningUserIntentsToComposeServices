

/**
 *
 * @author Team 3 - Shailesh Vajpayee, Pooja Shah, Aarti Gorade
 * Used to create an API object with corresponding attributes
 *
 */
public class API {
	String name;
	String link;
	String score;
	String category;
	String filename;
	
	public API(){
		
	}
	
	public API(String name, String link, String score, String category, String filename) {
		this.name = name;
		this.link = link;
		this.score = score;
		this.category = category;
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		
		return "API info: -\nCategory: " + category +name + "\nLink: " + link + "\n";// + "\nFile: " + filename + "\n";
	}

}
