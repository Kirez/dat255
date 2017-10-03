import java.util.*;

public class ACC extends Thread{

  private MotorControl mc;
  private UltraSonicSensor sensor;
  private Regulator regulator;
  private double distance;

  public ACC(MotorControl mc, UltraSonicSensor sensor){
      this.sensor = sensor;
      this.mc = mc;
      distance = 0;
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
      if(sensor.getDistance() != -1)
          distance = sensor.getDistance();

      mc.setSpeed(regulator.initNewCalc(distance));

      try {
          sleep(1000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }


}