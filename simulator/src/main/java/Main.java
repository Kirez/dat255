public class Main {

  public static void main(String[] args) {
    Car followingCar = new Car(0, 0, false);
    Car leadingCar = new Car(50, 0, true);
    followingCar.setSpeed(0);
    leadingCar.setSpeed(1.5);
    Simulator simulator = new Simulator(followingCar, leadingCar);
    simulator.run();
  }
}