public interface KeepDistance {

  /**
   * Funktionsbeskrivning
   *
   * @return värdet vi vill ha
   */
  public double readSensor();

  /**
   * Funktionsbeskrivning 2
   *
   * @param speed speeden vi vill sätta
   */
  public void setSpeed(double speed);

  /**
   * Funktionsbeskrivning 3
   *
   * @return speed of the car in m/s
   */
  public double readSpeed();
}
