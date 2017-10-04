public class ACC implements Runnable {

  private boolean stopFlagged = false;
  private MotorControl mc;
  private UltraSonicSensor sensor;
  private Regulator regulator;
  private double distance;

  public ACC(MotorControl mc, UltraSonicSensor sensor) {
    this.sensor = sensor;
    this.mc = mc;
    distance = 0;
    regulator = new Regulator();
  }

  @Override
  public void run() {
    while (!stopFlagged) {
      try {
        distance = sensor.getDistance();
        if (distance != -1) {
          mc.setSpeed(regulator.initNewCalc(distance));
        }
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void stop() {
    stopFlagged = true;
  }

  /* public double getDistance(){
     return (double)sensor.getDistance();
   }

   public void setSpeed(int speed){
     mc.setSpeed(speed);
   }

   public double getSpeed(){

   }*/
}
