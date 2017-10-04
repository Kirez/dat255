public class Sensor {

    /**
     * sqrt((x_1 - x_2)^2 + (y_1 - y_2)^2))
     * @param followingCar
     * @param leadingCar
     * @return distance in "cm"
     */
    public double getDistanceTo(Car followingCar, Car leadingCar) {
        return Math.sqrt(
                        Math.pow(leadingCar.getX() - followingCar.getX(), 2) +
                        Math.pow(leadingCar.getY() - followingCar.getY(), 2)
        );
    }

    /**
     * 1 pixel = 1 cm, we want to be within 40cm of the car in front.
     * @return true if distance is less than 40cm
     */
    public boolean isCarWithinRangeOf(Car followingCar, Car leadingCar) {
        return getDistanceTo(followingCar, leadingCar) <= 60;
    }

}
