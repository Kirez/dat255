package simulator;

public class Car {
    private double speed;
    private boolean isLeadingCar;

    private Regulator regulator;
    private Sensor sensor;

    private double x;
    private double y;

    public Car(boolean isLeadingCar) {
        this.isLeadingCar = isLeadingCar;

        if (isLeadingCar) {
            regulator = new Regulator();
            sensor = new Sensor();
        }
    }

    public Car(double x, double y, boolean isLeadingCar) {
        this(isLeadingCar);
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDistanceTo(Car c) {
        return sensor.getDistanceTo(this, c);
    }

    public void move(double speed) {
        this.x = this.x + speed;
    }
}
