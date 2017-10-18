import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import java.io.*;
import java.net.*;

import java.io.IOException;

/**
 * Created by Macken on 2017-10-06.
 */
public class ImageServer implements Runnable {
  private Process cameraProcess;
  private boolean stopFlagged;
  private ALCRegulator ALCreg;




  public ImageServer(ALCRegulator ALCreg) throws IOException {
	  System.out.println("FRESH NEW SERVER");
	  this.ALCreg = ALCreg;
	  String argv = "raspivid -l -o tcp://0.0.0.0:2222 --framerate 10 -w 1270 -h 292 -t 0 --mode 5";
	  cameraProcess = Runtime.getRuntime().exec(argv);
  }






  @Override
  public void run() {
    try {
    	DatagramSocket serverSocket = new DatagramSocket(9876);
    	System.out.println("listening on port 9876");
    	byte[] send = new byte[300];
    	byte[] receive = new byte[300];
    	
    	long startTime;
    	long endTime;
    	long waitTime;
    	int hertz = 10;
    	
    	while(true){
    		
    		startTime = System.currentTimeMillis();
    		
    		DatagramPacket receivePack = new DatagramPacket(receive, receive.length);
    		serverSocket.receive(receivePack);
    		byte[] message = receivePack.getData();


    		int x = java.nio.ByteBuffer.wrap(message).getInt();
    		System.out.println("Sending to ALC : " + x);
    		
    		ALCreg.calcSteering(x);
    		
    		InetAddress IP = receivePack.getAddress();
    		int port = receivePack.getPort();
    	
    		send = "OK".getBytes();
    		DatagramPacket sendPack = new DatagramPacket(send, send.length, IP, port);
    		serverSocket.send(sendPack);
    		
    		//endTime = System.currentTimeMillis();
    		//if(endTime>1000)
    		//	endTime = 1000;
    		//waitTime = 1000 - (endTime - startTime);
    		
    		//waitTime /= hertz;
    		
    		
    		//Thread.sleep(waitTime);
    	}
      
 
   	  
      
    
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
