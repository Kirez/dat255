import java.io.IOException;


public class MotorControl implements Moveable{

	private int i = 0;
	private int lastSpeed = 0;

	private CAN can;
	public MotorControl(CAN can){
		this.can = can;
	}

	public void setSpeed(int speed){
		if(speed > 127)
			speed = 127;
		else if (speed < -127)
			speed = -127;

		/* send CAN */

		try
		{
			can.sendMotorValue((byte)speed);
			lastSpeed = speed;
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}

	}


 	public double getSpeed(){
		return lastSpeed;
		/* Read ordometer */
		/*i++;
		double[] dummy = {2.1, 2.5, 2.3, 2.9, 3.5, 1.9, 3.1};
		Convert to cm/s
		if(i == 6)
			i = 0;
		return dummy[i];*/
	}

}
