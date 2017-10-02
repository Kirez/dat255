/**
 * Created by Macken on 2017-10-02.
 *
 *
 * Test Sim for steering
 */
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

public class Steering implements Observer{

   double course;
   double frontCourse;
   double error;
   double k;
   int s;

    public Steering() {

    s = 0;
    course = 0;
    k = 0.5;

    FrontCar fc = new FrontCar(this);

    new Thread(fc).start();

    }


    @Override
    public void update(Observable o, Object arg) {
        s++;
        frontCourse = (double)arg;
        error = course - frontCourse;

        course = error * k;

        if (s % 5 == 0){

            if (course > 180)
                course -= 360;
            else if (course < -180)
                course += 360;

            System.out.println("The front car's course is: " + frontCourse + ". The second car's course is: " + course  + " and is off course by: " + error);
        }

    }


    public class FrontCar extends Observable implements Runnable {

    double course;
    Random r;

    public FrontCar(Steering s) {

        this.addObserver(s);
        course = 0;
        r = new Random();

    }

        @Override
        public void run() {

            try {
                while (!interrupted()) {

                    course += r.nextInt(80) - 40 ;

                    if (course > 180)
                        course -= 360;
                    else if (course < -180)
                        course += 360;

                    setChanged();
                    notifyObservers(course);

                    sleep(200);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
