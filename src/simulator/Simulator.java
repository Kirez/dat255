package simulator;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

public class Simulator implements Runnable {

    private Car followingCar;
    private Car leadingCar;

    public Simulator(Car followingCar, Car leadingCar) {
        this.followingCar = followingCar;
        this.leadingCar = leadingCar;
    }

    @Override
    public void run() {
        while (!interrupted())
            try {
                followingCar.move(2);
                leadingCar.move(1.5);
                System.out.println("followingCar " + followingCar.getX());
                System.out.println("leadingCar " + leadingCar.getX());
                System.out.println(followingCar.getDistanceTo(leadingCar));
//                speed2 = 5;
//                setChanged();
//                notifyObservers(speed2);

//                sleep(2, 500);
                sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
