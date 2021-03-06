package org.gogpsproject.fx.model;

import java.util.Arrays;
import java.util.List;

import org.gogpsproject.GoGPS;
import org.gogpsproject.fx.model.Mode;

import net.java.html.json.Model;
import net.java.html.json.Property;

@Model(className = "Mode", targetId = "", properties = {
    @Property(name = "name", type = String.class ),
    @Property(name = "value", type = int.class )
})
public class RunModes {
  public static List<Mode> init(){
    return Arrays.asList(  
        new Mode("Code Stand-alone",             GoGPS.RUN_MODE_STANDALONE), 
        new Mode("Code Double Difference",       GoGPS.RUN_MODE_DOUBLE_DIFF), 
        new Mode("Kalman Filter",                GoGPS.RUN_MODE_KALMAN_FILTER), 
        new Mode("Code Stand-alone Coarse Time", GoGPS.RUN_MODE_STANDALONE_COARSETIME),
        new Mode("Code Stand-alone Snapshot",    GoGPS.RUN_MODE_STANDALONE_SNAPSHOT)
    );
  }
}
