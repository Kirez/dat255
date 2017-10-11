import java.util.Arrays;

/**
 * Created by hugfro on 2017-09-29.
 */
public class UltraSonicSensor implements IDistance {

    private CAN can;
    private short lastDist;
    private boolean doubleErrorCheck;

    public UltraSonicSensor(CAN can){
        this.can = can;
        lastDist = 0;
        doubleErrorCheck = true;
    }

    /*public int getDistance(){
        try {
            short[] a = can.readSensor();
            if(a.length != 5)
                return -1;
            Arrays.sort(a);
            return (a[2])/2;
        } catch (InterruptedException e) {
            return -1;
        }
    }*/




    /**
     * Some check on sensor value:
     *  NOTE: Error distance we react on can/should be changed. (not tested)
     *        Currently expects just a short/int and not an array from can.readSensor(), if we decide to
     *        send one sensor value at the time.
     *
     *  If: Difference is smaller we update lastDist and return new sensor value (accepted value)
     *  If else: Difference is larger than 500 then return lastDist (this sensor value was invalid)
     *  Else: We had error difference larger than 500 twice, which means we have to assume the sensor is looking at a
     *        new object.
     * @author Arvid
     * @return a New sensor value
     */
    public int getDistance(){
        try {
            //If readSensor return int/short instead of an array (only returning one value each time)
            Short a = can.readSensor();
            if (a == null) {
                return -1;
            }

            if((a - lastDist) < 500 || (a-lastDist) > -500)
            {
                doubleErrorCheck = true;
                lastDist = a;
                return a;
            }
            else if(doubleErrorCheck)
            {
                doubleErrorCheck = false;
                return lastDist;
            }
            else
            {
                doubleErrorCheck = true;
                lastDist = a;
                return a;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
