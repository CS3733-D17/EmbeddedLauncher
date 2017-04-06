/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package embeddedserverlauncher_fx;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class FXMLDocumentController implements Initializable {
    
    public static final String WAR_FOLDER = "deploy\\";
    public static final String LOC_PATH = "http://localhost:10080";
    
    @FXML
    private Label stat;
    @FXML
    private Label folder;
    @FXML
    private Button actBtn;
    @FXML
    private Button openBrowser;
    @FXML
    private Button shutdownBtn;
    
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    
    @FXML
    private void doAction(ActionEvent event) {
        try
        {
            if (state==0)
            {
                start();               
            }
            else if (state==1)
            {
                stop();         
            }
        }
        catch (Exception e)
        {
            stat.setText("FAILED");
            this.destroy();
        }
    }
    
    @FXML
    private void browser(ActionEvent event) {
        if(Desktop.isDesktopSupported())
        {
            try {
                Desktop.getDesktop().browse(new URI("http://www.google.com"));
            } catch (Exception ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void shutdown(ActionEvent event) {
        this.destroy();
        openBrowser.setDisable(true);
        shutdownBtn.setDisable(false);
    }
    
    private GlassFish glassfish;
    private Deployer deployer;
    private int state;
    private Set<String> deployedApps;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            deployedApps = new HashSet<>();
            GlassFishProperties glassfishProperties = new GlassFishProperties();
            glassfishProperties.setPort("http-listener", 10080);
            glassfishProperties.setPort("https-listener", 10081);
            glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
            state = 0;
            deployer=null;
            stat.setText("Stopped");
            folder.setText("");
            this.start();
            /*executor.submit(new Runnable(){
                @Override
                public void run() {
                    while (true)
                    {
                        try {
                            Thread.sleep(100);
                            stat.setText(glassfish.getStatus().name());
                        } catch (Exception ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                            break;
                        }
                    }
                }
            });*/
        } catch (GlassFishException ex) {
            this.destroy();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    private void destroy()
    {
        stat.setText("FAILED");
        try {
            this.stop();
        } catch (Exception ex) {
        }
        executor.shutdownNow();
        Platform.exit();
        System.exit(0);
    }
    
    private void start() throws GlassFishException, IOException
    {        
        openBrowser.setDisable(false);
        shutdownBtn.setDisable(true);
        glassfish.start();
        deployer = glassfish.getDeployer();
        state=1;
        stat.setText("Deploying");   
        this.deployAll();
        stat.setText("Running");  
        actBtn.setText("Stop");
    }
    
    private void stop() throws GlassFishException
    {
        openBrowser.setDisable(true);
        shutdownBtn.setDisable(false);
        glassfish.stop();
        state=0;
        stat.setText("Undeploying"); 
        if (deployer!=null)
        {
            for (String s : this.deployedApps)
            {
                deployer.undeploy(s);
            }
        }
        stat.setText("Stopped"); 
        actBtn.setText("Start");
        folder.setText("");
        deployer=null;
    }
    
    private void deployAll() throws GlassFishException, IOException
    {
        File f = new File("");
        folder.setText(f.getAbsolutePath());
        File wFolder = new File(WAR_FOLDER);
        if (!wFolder.exists())
        {
            wFolder.mkdir();
        }
        File[] wars = wFolder.listFiles((File pathname) -> pathname.getAbsolutePath().endsWith(".war"));
        for (File w : wars)
        {
            if (deployer!=null)
            {
                String loc = deployer.deploy(w);
                this.deployedApps.add(loc);
                this.openBrowser(loc);
                folder.setText(LOC_PATH+"/"+loc);
            }
        }
    }
    
    private void openBrowser(String location)
    {
        if(Desktop.isDesktopSupported())
        {
            try {
                Desktop.getDesktop().browse(new URI(LOC_PATH+"/"+location));
            } catch (Exception ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
