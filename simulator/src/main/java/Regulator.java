/**
 * Created by Macken on 2017-09-25.
 */
class Regulator {

  private double v1;
  private double vDes;
  private double accFactor;
  private final double k;
  private final double i;
  private double i_acc;
  private final double d;
  private double lastEr;
  private final int s;

  Regulator() {
    v1 = 0; /* k = 236; */
    k = 0.32; /* k = 0.5; */
    s = 0;
    i = 0.000;
    i_acc = 0;
    d = 0.00;
    lastEr = 0;
  }

  public double getNewSpeed(double accFactor, double distance) {
    double error = -40 + distance;
    i_acc += error * i; /* vDes = error * k; */
    vDes = error * k + i_acc + (-error + lastEr) * d;
    v1 += 0.1 * (vDes - v1);
    lastEr = error;
    return v1;
  }
}