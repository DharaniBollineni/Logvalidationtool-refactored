package ui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Entry extends Application{
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("uiV2.fxml"));
		
		Scene scene = new Scene(root, 869, 637);
		
		stage.setTitle("edX MOOC Learning Behaviour Analysis");
		stage.setScene(scene);
		stage.show();
		
		stage.setOnCloseRequest( (WindowEvent we) -> {
			try {
				UIController.serverStop();
			} catch (Exception e) {
				System.err.println("Exception while stopping server.");
				System.err.println(e.getClass()+" : "+e.getMessage());
			}
		});
	}
	
	public static void main(String[] args) {   
		launch(args);
	}
}