import java.io.IOException;


public class MotorControl implements Moveable{

	
	public MotorControl(){
		
	}

	public void setSpeed(byte speed){
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
