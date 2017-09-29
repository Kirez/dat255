import java.io.IOException;


public class MotorControl implements Moveable{

	private int i = 0;
	private CAN can;
	public MotorControl(CAN can){
		this.can = can;
	}

	public void setSpeed(byte speed){
		/* send CAN */
		/*try
		{
			can.sendMotorValue(speed);
		}
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}*/
	}


 	public double getSpeed(){
		double speed = 0;
		/* Read ordometer */
		i++;
		double[] dummy = {2.1, 2.5, 2.3, 2.9, 3.5, 1.9, 3.1};
		/* Convert to cm/s */
		if(i == 6)
			i = 0;
		return dummy[i];
	}

}
