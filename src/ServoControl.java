/**
 * Created by hugfro on 2017-10-03.
 */
public class ServoControl implements Steering{

    private CAN can;

    public ServoControl(CAN can){
        this.can = can;
    }

    public void steer(int steering){

    }
}
