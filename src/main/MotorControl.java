package main;

import java.io.IOException;


public class MotorControl implements IMovable {

	
	public MotorControl(){
		
	}

	public void setSpeed(double speed){
		/* send CAN */
	}


 	public double getSpeed(){
		double speed = 0;
		/* Read ordometer */
		try
		{
			CAN instance = CAN.getInstance();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}	
		
		/* Convert to cm/s */ 
		return speed;		
	}

}
