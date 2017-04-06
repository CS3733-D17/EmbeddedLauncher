/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddedserverlauncher_fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class EmbeddedServerLauncher_Fx extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        Platform.setImplicitExit(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        FXMLDocumentController c = loader.getController();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent t) {
                t.consume();
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
