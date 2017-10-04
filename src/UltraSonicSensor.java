import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by hugfro on 2017-09-29.
 */
public class UltraSonicSensor implements IDistance{

    private CAN can;

    public UltraSonicSensor(CAN can){
        this.can = can;
    }
    public int getDistance(){
        try {
            short[] a = can.readSensor();
            if(a.length != 20)
                return -1;
            Arrays.sort(a);
            return (a[9] + a[8])/2;
        } catch (InterruptedException e) {
            return -1;
        }
    }
}
