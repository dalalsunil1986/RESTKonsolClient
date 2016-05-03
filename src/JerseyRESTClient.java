import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.google.gson.Gson;

public class JerseyRESTClient {	
	
	public ArrayList<Item> itemList;
	private String baseURL = "http://fbballin.com/v1/items/";
		
	public void getItemsFromAPI() throws JSONException, IOException {
		URL url = new URL(baseURL);
		JSONTokener tokener = new JSONTokener(url.openStream());
		JSONObject root = new JSONObject(tokener);
		Gson gson = new Gson();

		JSONObject object1 = root.getJSONObject("data");
		JSONArray array = object1.getJSONArray("default");
		
		itemList = new ArrayList<Item>();
		
		//Opretter liste med items fra JSONArrayet
		for (int i = 0; i < array.length(); i++) {
			Item item = gson.fromJson(array.get(i).toString(), Item.class);
			itemList.add(item);
		}
	}
	
	public static void main(String[] args) {		
		try {
			JerseyRESTClient client = new JerseyRESTClient();
			client.printMenu();		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	private void printMenu() throws IOException, JSONException {
		getItemsFromAPI();
		System.out.println("Velkommen til Super Simple DDHF Java REST klient!");
		System.out.println("Vælg en kommando:");
		System.out.println("1. List alle genstande");
		System.out.println("2. Slå genstand op");
		System.out.println("3. Slet genstand");
		
		Scanner scan = new Scanner(System.in);
		String userInput = scan.nextLine();
		switch(userInput) {
		case "1":
			listAllItems();
			break;
		case "2":
			System.out.println("Indtast genstand id:");
			userInput = scan.nextLine();
			System.out.println(lookupItem(userInput));
			userInput = scan.nextLine();
			break;
		case "3":
			System.out.println("Indtast genstand id:");
			userInput = scan.nextLine();
			System.out.println("Indtast genstand id igen for at bekræfte sletning:");
			String userInput2 = scan.nextLine();
			if(userInput.equals(userInput2)) {
				deleteItem(userInput);
			} else {
				System.out.println("Item id matchede ikke");
			}
		}
		printMenu();
	}
	
	private void deleteItem(String userInput) throws IOException {
		URL url = new URL(baseURL + Integer.valueOf(userInput) + "?token=test");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestProperty(
		    "Content-Type", "application/x-www-form-urlencoded" );
		httpCon.setRequestMethod("DELETE");
		httpCon.connect();	
	}

	private String lookupItem(String userInput) {
		for (int i = 0; i < itemList.size(); i++) {
			if(itemList.get(i).getId() == Integer.valueOf(userInput)) {
				return itemList.get(i).toString() + " (Tryk Enter for at forstætte)";
			}
		}
		return "Genstand med ID " + userInput + " kunne ikke findes. (Enter for at fortsætte)";
	}

	private void listAllItems() {
		for (int i = 0; i < itemList.size(); i++) {
			System.out.println("---------------------------------");
			System.out.println(itemList.get(i).toString());
		}		
	}


	public class Item {
		private int id;
		private String headline;
		private String description;
		private String donator;
		private String producer;
		private String zipcode;
//		private JSONObject dating_to;
		
		public String getDescription() { return description; }
		
		public String getHeadline() { return headline; }
		
		public int getId() { return id; }
		
		public String toString() {
			return "Titel: " + headline + "\n" + 
					"ID: " + id + "\n" +
					"Beskrivelse: " + description + "\n" +
					"Postnr.: " + zipcode + "\n" +
					"Producent: " + producer + "\n" +
					"Donator: " + donator + "\n" 
//					"Dateret fra: " + dating_to + "\n"
					;
		}
		
	}

}
