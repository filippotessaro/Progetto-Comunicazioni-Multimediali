package maze.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import maze.Main;


public class LoginController {
	
	@FXML
	private Label lb_status;
	@FXML
	private TextField lb_user;
	@FXML
	private TextField lb_pass;
	@FXML
	private Button btn_Login;
	@FXML
	private AnchorPane apLogin;
	@FXML
	private Label lbWrongPass;
	
	//Azione di login 
	//se i campi username e password contengono testo viene chiamata la funzione call_me per la richiesta al server
	public void Login(ActionEvent event) throws Exception {
		System.out.println("password: " + lb_pass.getText());
		if(checkConnection()==true) {
			if(lb_user.getText() != null && lb_pass.getText() != null) {
				lb_status.setText("Connesso al Server");
				lb_status.setTextFill(Paint.valueOf("green"));
				call_me(event);
			}
		}else {
			lb_status.setText("No Connection");
			lb_status.setTextFill(Paint.valueOf("red"));
		}
		
	}
	
	//Funzione che riceve in ingresso l'evento generato dal click dell bottone di Login
	//Essa manda una POST request al server, il quale a sua volta 
	//controlla lo username e la password e ritorna un Json contenente il Token
	public void call_me(ActionEvent event) throws Exception {
		
		URL url = new URL("https://servermaze.herokuapp.com/api/authenticate");
		Map<String,Object> params = new LinkedHashMap<>();
		
		//Inserisco parametri
		params.put("name", lb_user.getText());
		params.put("password", lb_pass.getText());
		
		//Decodifico
		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String,Object> param : params.entrySet()) {
			if (postData.length() != 0) postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}	
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");
		
		//Connessione con POST
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
		//Richiedo tramite POST
		conn.setRequestMethod("POST");
		
		//Header
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		
		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0;)
			sb.append((char)c);
		String response = sb.toString();
		
		//Salvo le risposte in una variabile JSON
		JSONObject myResponse = new JSONObject(response.toString());
		System.out.println(myResponse);
		if((boolean) myResponse.get("success") == true) {
			String token = (String) myResponse.get("token");
			System.out.println("Token: " + token);
			String user = (String) myResponse.get("user");

			//Cambio di view: da Login => a LevelController
			((Node) (event.getSource())).getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader();
			
			Parent  parent = loader.load(getClass().getResource("LevelView.fxml").openStream());
			LevelController out = new LevelController();
			out = loader.getController();
			out.SetVariables(token,user,lb_user.getText());
			out.setTable();
			
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(parent));
			stage.getScene().getStylesheets().add(Main.class.getResource("view/application.css").toExternalForm());
			stage.show();
		} else 
			lbWrongPass.setText("Password o Username errati");
		
	}
	
	//Verifico lo stato della connessione creando una socket al server Heroku(come stest)
    public static boolean checkConnection() throws IOException {
        String site = "servermaze.herokuapp.com";
        try (Socket socket = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress(site, 80);
            socket.connect(addr, 3000);
            return socket.isConnected();
        }catch(Exception e){
        		return false;
        }
    }
	
}
