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
      System.out.println("Starting ACC");
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
      while(true) {
          try {
              distance = sensor.getDistance();
              if(distance != -1)
              {
                  mc.setSpeed(regulator.initNewCalc(distance));
              }
              sleep(500);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  }
}