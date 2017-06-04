import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Team 3 - Pooja Shah, Aarti Gorade, Shailesh Vajpayee This file
 *         invokes Google Custom Search API to get how-to instructions from
 *         WikiHow or eHow website
 */
public class CustomSearchAPIInvocation {

	public static String webSiteSource = "";

	public CustomSearchAPIInvocation() {

	}

	/**
	 * This method invokes Google Custom Search API to get how-to instructions
	 * from WikiHow or eHow website
	 * 
	 * @param queryInput
	 */
	public static void invokeAPI(String queryInput) {
		try {
			String query = queryInput.replaceAll(" ", "%20");
			URL url = new URL(
					"https://www.googleapis.com/customsearch/v1?key=AIzaSyBpj55vzhzocoax0fpnQEZ_tlNyF3eRp10&cx=014806343086978459457:enje1upnujs&q="
							+ query);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				if (output.startsWith("   \"link\":")) {
					String[] linkOutput = output.trim().split(" ");
					webSiteSource = linkOutput[1];
					webSiteSource = webSiteSource.replaceAll("\"", "");
					break;
				}
			}
			System.out.println("Link: " + webSiteSource);
			conn.disconnect();
		} catch (MalformedURLException ex) {
			Logger.getLogger(CustomSearchAPIInvocation.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ProtocolException ex) {
			Logger.getLogger(CustomSearchAPIInvocation.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(CustomSearchAPIInvocation.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
