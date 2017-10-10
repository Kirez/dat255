import java.util.Arrays;

/**
 * Created by hugfro on 2017-09-29.
 */
public class UltraSonicSensor implements IDistance {

  private CAN can;

  public UltraSonicSensor(CAN can) {
    this.can = can;
  }

  public int getDistance() {
    try {
      short[] a = can.readSensor();
      if (a.length != 5) {
        return -1;
      }
      Arrays.sort(a);
      return (a[2]) / 2;
    } catch (InterruptedException e) {
      return -1;
    }
  }
}