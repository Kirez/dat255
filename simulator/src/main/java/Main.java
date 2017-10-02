public class Main {

    public static void main(String[] args) {
        final Car car1 = new Car(0, 0, true);
        final Car car2 = new Car(100, 0, false);
        car1.setSpeed(1.5);
        car2.setSpeed(2);

        Simulator simulator = new Simulator(car1, car2);
        simulator.run();
    }
}
