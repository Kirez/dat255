package plugins;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;

public class DriveFW extends PlugInComponent {
    //private PluginPPort fs;
    //private PluginRPort ff;
	
    public DriveFW() {}
	
    public DriveFW(String[] args) {
	super(args);
    }
	
    public static void main(String[] args) {
	DriveFW plugin = new DriveFW(args);
	plugin.run();
    }

    public void init() {
	//fs = new PluginPPort(this, "fs");
	//ff = new PluginRPort(this, "ff");
    }
	
    public void doFunction() throws InterruptedException {
    }

    public void run() {
	init();

	try {
	    doFunction();
	} catch (InterruptedException e) {
	    VM.println("**************** Interrupted.");
	    return;
	}
    }
}
