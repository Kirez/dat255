import java.io.IOException;


public class MotorControl implements IMovable {

	private int i = 0;
	private int lastSpeed = 0;
	boolean lastWasForward = true;

	private CAN can;
	public MotorControl(CAN can){
		this.can = can;
		try {
			can.sendMotorAndSteerValue((byte) 0, (byte) 0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public void setSpeed(int speed){
		if(speed > 127) {
			speed = 127;
		}
		else if (speed < -127) {
			speed = -127;
		}


		/* send CAN */

		try
		{
			if(lastWasForward) {
				if (speed < 0)
				{
					can.sendMotorValue((byte) 0);
					lastWasForward = false;
				}
			}

			if(speed > 0)
				lastWasForward = true;
			can.sendMotorValue((byte) speed);
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
