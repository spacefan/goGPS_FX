package org.gogpsproject.fx;
	
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Logger;

import org.gogpsproject.fx.model.DynModel;
import org.gogpsproject.fx.model.DynModels;
import org.gogpsproject.fx.model.GoGPSDef;
import org.gogpsproject.fx.model.GoGPSModel;
import org.gogpsproject.fx.model.Mode;
import org.gogpsproject.fx.model.Modes;
import org.gogpsproject.fx.model.Producers;
import org.gogpsproject.fx.model.SerialPortDef;

import net.java.html.BrwsrCtx;
import net.java.html.boot.BrowserBuilder;
import net.java.html.js.JavaScriptBody;
import net.java.html.js.JavaScriptResource;
import net.java.html.json.Models;
import netscape.javascript.JSException;

public class GoGPS_Fx {
  private static final Logger logger = Logger.getLogger(GoGPS_Fx.class.getName());
  static GoGPSModel goGPSModel;
  
  public abstract static class FirebugConsole extends OutputStream {
    protected final BrwsrCtx ctx;

    public FirebugConsole( BrwsrCtx ctx ){
      this.ctx = ctx;
    }
    abstract void logNative( String msg );

    void log(String msg) {
      ctx.execute(new Runnable(){
        @Override
        public void run() {
          logNative(msg);
        }
      });
    }

    StringBuilder sb = new StringBuilder();

    @Override
    public void write(int i) {
      sb.append((char)i);
    }

    @Override
    public void flush() {
      if( sb.length() >0 && !sb.toString().equals("\r\n"))
        log(sb.toString());
      sb = new StringBuilder();
    }  
  }

  public static class FirebugConsoleInfo extends FirebugConsole{
    public FirebugConsoleInfo(BrwsrCtx ctx) {
      super(ctx);
    }

    @net.java.html.js.JavaScriptBody(args = { "msg" }, body = ""
        + "Firebug.Console.log(msg);")
    public native void logNative( String msg );

  }
  
  public static class FirebugConsoleError extends FirebugConsole{
    public FirebugConsoleError(BrwsrCtx ctx) {
      super(ctx);
    }

    @net.java.html.js.JavaScriptBody(args = { "msg" }, body = ""
        + "Firebug.Console.error(msg);")
    public native void logNative( String msg );
  }
  
  public static void main(String... args) throws Exception {
    System.out.println("Library Path is " + System.getProperty("java.library.path"));
    System.out.println("rootdir is " + System.getProperty("user.dir"));

    String rootdir = System.getProperty("user.dir") + "/src/main/webapp";
    System.setProperty("browser.rootdir", rootdir ); 
    
    BrowserBuilder.newBrowser().
                   loadPage("pages/index.html").
                   loadClass(GoGPS_Fx.class).
                   invoke("onPageLoad").
                   showAndWait();
    System.exit(0);
  }
  
  public static void onPageLoad() throws Exception {
//      if( goGPSModel != null ){
//        GoGPSDef.cleanUp(goGPSModel);
//      }
      BrwsrCtx ctx = BrwsrCtx.findDefault(GoGPS_Fx.class);
    
      goGPSModel = new GoGPSModel();
      
      goGPSModel.getRunModes().addAll( Modes.get() );
      goGPSModel.getDynModels().addAll( DynModels.get() );
      goGPSModel.setSelectedRunMode(Modes.standAlone);
      goGPSModel.setSelectedDynModel(DynModels.staticm);
      Producers.init();
      goGPSModel.getSpeedOptions().addAll( Arrays.asList(new Integer[]{9600, 115200}));
      goGPSModel.getMeasurementRateOptions().addAll( Arrays.asList(new Integer[]{1, 2, 5, 10}));
      goGPSModel.setOutputFolder("./out");
      Models.toRaw(goGPSModel);
      GoGPSDef.registerModel();
      goGPSModel.applyBindings();
      
      System.setOut(new PrintStream(new FirebugConsoleInfo(ctx), true));
//      System.setErr(new PrintStream(new FirebugConsoleError(ctx), true));

      // test Serialio. If it fails, copy native library to root
      for( int i=0; i<2; i++ ){
        try {
          GoGPSDef.getPortList( goGPSModel );
          System.out.println("RXTX Ok");
          break;
        }
        catch( JSException jse ){
          System.err.println(jse);
          break;
        }
        catch (Throwable localThrowable){
          System.err.println(localThrowable + " thrown while loading " + "gnu.io.RXTXCommDriver");
          System.err.flush();
          if( i == 0 ){
            SerialPortDef.copyRxTxLibToRoot();
            GoGPSDef.alert("RXTX lib copied to root, you might have to restart the app");
          }
        }
      }
    }    
}

