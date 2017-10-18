import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

class Simulator implements Runnable {

  private final Car followingCar;
  private final Car leadingCar;

  public Simulator(Car followingCar, Car leadingCar) {
    this.followingCar = followingCar;
    this.leadingCar = leadingCar;
  }

  @Override
  public void run() {
    while (!interrupted()) {
      try {
        followingCar.setNewSpeed(leadingCar);
        followingCar.setSpeed(followingCar.getNewSpeed());
        followingCar.move();
        leadingCar.move();
        System.out
            .println("followingCar currentSpeed :" + followingCar.getSpeed());
        System.out.println("leadingCar currentSpeed :" + leadingCar.getSpeed());
        System.out.println("followingCar distanceTo " + followingCar
            .getDistanceTo(leadingCar));
        sleep(25);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}