package plugins;

import com.sun.squawk.VM;
import fresta.port.instances.VirtualAcceleratorRPort;
import fresta.port.instances.VirtualFrontWheelPPort;
import sics.plugin.PlugInComponent;

/**
 * Supersimple plugin to make the simulated car drive forward in the Simulator.
 * @author Johan Svennungsson
 */
public class DriveFWSimulator extends PlugInComponent {
    private VirtualFrontWheelPPort readFrontSpeed;
    private VirtualAcceleratorRPort writeAcceleration;
	
    public DriveFWSimulator() {}
	
    public DriveFWSimulator(String[] args) {
	super(args);
    }
	
    public static void main(String[] args) {
        DriveFWSimulator plugin = new DriveFWSimulator(args);
	    plugin.run();
    }

    public void init() {
        readFrontSpeed = new VirtualFrontWheelPPort(5);
        writeAcceleration = new VirtualAcceleratorRPort(3);
    }
	
    public void doFunction() throws InterruptedException {

        while(true) {
//            System.out.println(readFrontSpeed.deliver().toString());
            writeAcceleration.deliver(0.1);
            Thread.sleep(1000);
        }
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
