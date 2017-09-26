public class Speed {
    private double speed;

    /**
     * Set the speed of the MOPED
     * @param speed a value between -100 (reverse) to 100 (full speed)
     */
    public void setSpeed(double speed) {
        this.speed = speed;
        //setSpeedInHW(speed);
    }

    /**
     * Returns the current speed of the MOPED
     * @return a double between -100 and 100
     */
    public double getSpeed() {
        //speed = getSpeedInHW();
        return speed;
    }

}
