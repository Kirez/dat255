import java.util.*;

public class ACC extends Thread{

  private MotorControl mc;
  private UltraSonicSensor sensor;
  private Regulator regulator;

  public ACC(MotorControl mc, UltraSonicSensor sensor){
      this.sensor = sensor;
      this.mc = mc;
      regulator = new Regulator();
      start();
  }

  /* public double getDistance(){
     return (double)sensor.getDistance();
   }

   public void setSpeed(int speed){
     mc.setSpeed(speed);
   }

   public double getSpeed(){

   }*/

  public void run() {
      double distance = sensor.getDistance();

      mc.setSpeed(regulator.initNewCalc(distance));

  }


}