package maze.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import maze.Livello;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;


public class LevelController{
	
	private String token;
	private String userId;
	ObservableList<Livello> livelli = FXCollections.observableArrayList();
	
	@FXML
	private TableView<Livello> tableUser;
	@FXML
    private TableColumn<Livello,String> colTocchi;
    @FXML
    private TableColumn<Livello,Integer> colNliv;
    @FXML
    private TableColumn<Livello,String> colSvolto;
    @FXML
    private TableColumn<Livello,Button> colGioca;
    @FXML
    private TableColumn<Livello,String> colLivello;
    @FXML
    private TableColumn<Livello,String> colTempo;
    @FXML
    private Label lbUsername;
	
    
    //Costruttore che mi setta lo userId e il token da un'altra view
	public void SetVariables(String tk, String user, String usename) {
		System.out.println("user: " + tk + " token: " + user);
		token = tk;
		userId = user;
		lbUsername.setText(usename);
	}
	

    public void setTable() throws JSONException {
    		
    		//StringBuffer
    		System.out.println(TokenRequest(this.userId, this.token));
    		
    		//Converto lo string Buffer in String
    		String Array = TokenRequest(this.userId, this.token).toString();
    		
    		//Creo JsonArray
    		JSONArray myResponse = new JSONArray(Array);
    		
    		//Vari log per accedere ai campi del JsonArray
    		System.out.println(myResponse);		//visualizza tutto l'array
    		System.out.println(myResponse.length());		//visualizza la lunghezza dell'array
    		System.out.println(myResponse.get(0));		//visualizza l'oggetto in posizione 0 dell'array
    		
    		setLivelliList(myResponse);
    		
    		colLivello.setCellValueFactory(new PropertyValueFactory<Livello,String>("idLivello"));
    		colTocchi.setCellValueFactory(new PropertyValueFactory<Livello,String>("numeroTocchi"));
    		colNliv.setCellValueFactory(new PropertyValueFactory<Livello,Integer>("numeroLivello"));
    		colSvolto.setCellValueFactory(new PropertyValueFactory<Livello,String>("svolto"));
    		colTempo.setCellValueFactory(new PropertyValueFactory<Livello,String>("tempo"));
    		colGioca.setCellValueFactory(new PropertyValueFactory<>("btn"));
    		
    		tableUser.setItems(livelli);
    }
    
    private void setLivelliList(JSONArray jsonArray) throws JSONException{
	    	int i;
	    	for(i=0;i<jsonArray.length();i++) {  		
	    		JSONObject item = jsonArray.getJSONObject(i); 
	    		System.out.println(item.getString("_id"));
	    		livelli.add(new Livello(item.getString("_id"),
	    				String.valueOf(item.getBoolean("svolto")),
	    				item.getInt("numero"), 
	    				String.valueOf(item.getInt("numerotocchi")), 
	    				String.valueOf(item.getInt("tempo")), lbUsername.getText(), token, userId
	    				));
	    	}
    }

    
	//Funzione GET al server che mi restituisce uno string buffer contenente tutti i 
    //livelli assegnati all'utente
    public StringBuffer TokenRequest(String user_id, String token) {
		
		StringBuffer response = null;
		
		try {
			String url = "http://localhost:8080/api/users/level/" + user_id;
			URL obj = new URL(url);
			
			// Connetto tramite GET
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			
			//Setto Header della richiesta
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("x-access-token", token);
			
			//Stampo richiesta GET
			int HttpResult = con.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				//Entra se ha successo
				BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"));
				String line = null;
				response = new StringBuffer();			
				while ((line = br.readLine()) != null) {
					response.append(line + "\n");
				}
				br.close();
			} 
			else
				System.out.println(con.getResponseMessage());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}

	public void Esci(ActionEvent event) throws Exception {
		((Node) (event.getSource())).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader();	
		Parent  parent = loader.load(getClass().getResource("Login.fxml").openStream());
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(new Scene(parent));
		stage.show();
	}
}
