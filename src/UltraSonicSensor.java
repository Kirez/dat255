/**
 * Created by hugfro on 2017-09-29.
 */
public class UltraSonicSensor implements IDistance{

    private int i = 0;
    private CAN can;

    public UltraSonicSensor(CAN can){
        this.can = can;
    }
    public int getDistance(){
        /* get CAN-messages */
		/*try
		{*/
		     /* Read from can Data*/
		/*	//can.;
		}
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}*/

        int[] dummy = {200, 267, 305, 289, 190 , 340 , 500, 600, 532, 510, 400};
        i++;

        if(i == 10)
            i = 0;
        return dummy[i];
    }
}
