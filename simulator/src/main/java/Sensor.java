public class Sensor {

    public double getDistanceTo(Car car1, Car car2) {
        return Math.sqrt(Math.pow(car2.getX() - car1.getX(), 2) + Math.pow(car2.getY() - car1.getY(), 2));
    }

}
