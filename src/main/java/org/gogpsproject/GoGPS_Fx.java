package org.gogpsproject;
	
import org.gogpsproject.model.GoGPSDef;
import org.gogpsproject.model.GoGPSModel;
import org.gogpsproject.model.SerialPortDef;
import org.gogpsproject.model.SerialPortModel;

import net.java.html.boot.BrowserBuilder;
import net.java.html.json.Models;


public class GoGPS_Fx {
  public static BrowserBuilder b;
  static GoGPSModel goGPSModel;
  static String args[];

  public static void main(String... argsp) throws Exception {
    String rootdir = System.getProperty("user.dir") + "/src/main/webapp";
    System.setProperty("browser.rootdir", rootdir ); 
    args = argsp;
    b = BrowserBuilder.newBrowser();
    //reload();
    b.loadPage("pages/index.html");
    b.loadClass(GoGPS_Fx.class).
    invoke("onPageLoad", args).
    showAndWait();
    System.exit(0);
  }
  
  /**
   * Called when the page is ready.
   */
  public static void onPageLoad() throws Exception {
      goGPSModel = new GoGPSModel();
      
      goGPSModel.getSerialPort().setPort("undefined");
      goGPSModel.getSerialPort().setSpeed(9600);
      
      Models.toRaw(goGPSModel);
      GoGPSDef.registerModel();
      goGPSModel.applyBindings();

      SerialPortDef.setRxTxLibPath();
      SerialPortDef.getPortList( goGPSModel.getSerialPort() );
  }    
}

