

public class MotorControl implements Movable{

	
	public MotorControl(){
		
	}

	public void setSpeed(byte speed){
		sendMotorValue(speed.ByteValue());
	}


 	public double getSpeed(){
		double speed = 0;
		/* Read ordometer */
		CAN instance = CAN.getInstance();
		
		/* Convert to cm/s */ 
		return speed;		
	}

}
