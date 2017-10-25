import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

import java.util.Observable;
import java.util.Observer;

// TODO: Auto-generated Javadoc
/**
 * Test Sim for steering
 * <p>
 * Created by Macken on 2017-10-02.
 */
public class SteeringSim implements Observer { /* Lateral Field of View of the Camera */

  /** The course. */
 private double course; /* Course of the leading car */
  
  /** The k. */
  private double k; /* Number of invocations */
  
  /** The s. */
  private int s; /* SteeringSim */
  
  /** The steer. */
  private double steer; /* *Starts the thread running the leading car */

  /**
   * Instantiates a new steering sim.
   */
  public SteeringSim() {
    s = 0;
    course = 0;
    k = 1.1;
    FrontCar fc = new FrontCar(this);
    new Thread(fc).start();
  } /* * On update, receives the course on which the leading car is traveling, calculates the error and sets the steering * accordingly. */

  /* (non-Javadoc)
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  public void update(Observable o, Object arg) {
    s++;
    double frontCourse = new Double(arg.toString()); /* Error in camera units */
    double degError = frontCourse - course; /* Amplification */
    int carFov = 49; /* Course of the following car */
    double error = degError / carFov; /* Error in degrees */
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
   * The Class FrontCar.
   */
  public static class FrontCar extends Observable implements Runnable {

    /** The course. */
    double course;
    
    /** The s. */
    int s;

    /**
     * Instantiates a new front car.
     *
     * @param st the st
     */
    FrontCar(SteeringSim st) {
      this.addObserver(st);
      course = 0;
      s = 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
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