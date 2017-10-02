/**
 * @author Hamza Kadric
 * @author Timmy Truong
 * @author Arvid Wiklund
 */
public interface IDistance {
    /**
     * This method reads from sensor and returns data in form of a double
     * containing the distance to the object in front in metres.
     *
     * The return value shall be in centimetres.
     *
     * @return a double containing the value of the distance to the object in front read from the sensor
     */
    double getDistance();
}
