package simulator;

class Car {
    private double speed;
    private boolean isLeadingCar;

    private Regulator regulator;
    private Sensor sensor;

    Car(boolean isLeadingCar) {
        this.isLeadingCar = isLeadingCar;

        if (isLeadingCar) {
            regulator = new Regulator();
            sensor = new Sensor();
        }
    }

}
