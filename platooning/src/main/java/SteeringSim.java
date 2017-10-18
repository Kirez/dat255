import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Test Sim for steering
 *
 * Created by Macken on 2017-10-02.
 */
public class SteeringSim implements Observer { /* Lateral Field of View of the Camera */

  int carFov = 49; /* Course of the following car */
  double course; /* Course of the leading car */
  double frontCourse; /* Error in camera units */
  double error; /* Error in degrees */
  double degError; /* Amplification */
  double k; /* Number of invocations */
  int s; /* SteeringSim */
  double steer; /* *Starts the thread running the leading car */

  public SteeringSim() {
    s = 0;
    course = 0;
    k = 1.1;
  } /* * On update, receives the course on which the leading car is traveling, calculates the error and sets the steering * accordingly. */

  public void startThread(FrontCar fc) {
    new Thread(fc).start();
  }

  @Override
  public void update(Observable o, Object arg) {
    s++;
    frontCourse = Double.valueOf(arg.toString());
    degError = frontCourse - course;
    error = degError / carFov;
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

  public static class FrontCar extends Observable implements Runnable {

    double course;
    //Random r;
    int s;

    public FrontCar(SteeringSim st) {
      this.addObserver(st);
      course = 0;
      //r = new Random();
      s = 0;
    }

    @Override
    public void run() {
      try {
        while (!interrupted()) {
          if (s == 0) {
            course = 40;
          } else if (s % 50 == 0) {
            course += 40;
          }/* else { course += r.nextInt(4) - 2; }*/
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