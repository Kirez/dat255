package se.byggarebob.platooning.simulator;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

// TODO: Auto-generated Javadoc
/**
 * The Class Simulator.
 */
public class Simulator implements Runnable {

  /** The following car. */
  private Car followingCar;

  /** The leading car. */
  private Car leadingCar;

  /**
   * Instantiates a new simulator.
   *
   * @param followingCar the following car
   * @param leadingCar the leading car
   */
  public Simulator(Car followingCar, Car leadingCar) {
    this.followingCar = followingCar;
    this.leadingCar = leadingCar;
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
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