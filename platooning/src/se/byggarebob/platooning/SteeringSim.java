package se.byggarebob.platooning;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

import java.util.Observable;
import java.util.Observer;

/**
 * Test Sim for steering.
 *
 * @author Karl Ängermark
 */
public class SteeringSim implements Observer {

  /** Course of the leading car. */
  private double course;

  /** Amplification factor of the regulator. */
  private double k;

  /** Number of invocations. */
  private int s;

  /** The steering value to be sent. */
  private double steer;

  /** Instantiates a new steering sim. */
  public SteeringSim() {
    s = 0;
    course = 0;
    k = 1.1;
    FrontCar fc = new FrontCar(this);
    new Thread(fc).start();
  }

  /**
   * On update, receives the course on which the leading car is traveling,
   * calculates the error and sets the steering accordingly.
   */
  @Override
  public void update(Observable o, Object arg) {
    s++;
    double frontCourse = new Double(arg.toString()); /* Course of the front car */
    double degError = frontCourse - course; /* The error in the course in degrees */
    int carFov = 49; /* Lateral Field of View of the Camera */
    double error = degError / carFov; /* The error in general units  */
    if (s % 5 == 0) {
      steer += error * k;
      System.out.println("The front car's course is: " + frontCourse
          + " degrees. The second car's course is: " + course
          + " degrees and is off course by: " + degError + " degrees.");
    }
    course = steer * carFov;
    if (course > 180) {
      course -= 360;
    } else if (course < -180) {
      course += 360;
    }
  }

  /**
   * An observable thread that updates its course to the
   * <code>SteeringSim</code>.
   */
  public static class FrontCar extends Observable implements Runnable {

    /** The course of the preceding car. */
    double course;

    /** The number of invocations. */
    int s;

    /**
     * Instantiates a new front car.
     *
     * @param st Takes the steering sim as a parameter to make it the observer
     */
    FrontCar(SteeringSim st) {
      this.addObserver(st);
      course = 0;
      s = 0;
    }

    /**
     * Changes the course of the preceding car every 0.2 seconds and notifies
     * the sim.
     */
    @Override
    public void run() {
      try {
        while (!interrupted()) {
          if (s == 0) {
            course = 40;
          } else if (s % 50 == 0) {
            course += 40;
          }
          if (course > 180) {
            course -= 360;
          } else if (course < -179) {
            course += 360;
          }
          setChanged();
          notifyObservers(course);
          s++;
          sleep(200);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}