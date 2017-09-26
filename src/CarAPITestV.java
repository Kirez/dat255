/**
 * Dummy API class to test code. Replace this with the real API constructed by the other subteam.
 */

import java.util.Random;
public class CarAPITestV implements KeepDistance {

    private static CarAPITestV api;
    private static int i;
    public static CarAPITestV getInstance() {
        if(api == null)
            api = new CarAPITestV();
        i = -1;
        return api;
    }

    @Override
    public double readSensor() {
        Random random =  new Random();
        int[] list = {60, 55, 60, 55, 45, 50, 60, 70, 65,55, 50, 40, 30, 50};
        i ++;
        return list[i];
    }

    @Override
    public void setSpeed(double speed) {
    }

    @Override
    public double getSpeed() {
        return 100;
    }
}
