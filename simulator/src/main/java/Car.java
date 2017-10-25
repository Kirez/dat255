public class Car {

  private double speed;
  private double wantedSpeed;
  private double newSpeed;
  private boolean isLeadingCar;
  private Regulator regulator;
  private Sensor sensor;
  private MovingState currentState;
  private MovingState acceleratingState;
  private MovingState deacceleratingState;
  private double x, y;

  public Car(boolean isLeadingCar) {
    this.isLeadingCar = isLeadingCar;
    this.speed = 0;
    if (!isLeadingCar) {
      regulator = new Regulator();
      sensor = new Sensor();
    }
    acceleratingState = new AcceleratingState();
    deacceleratingState = new DeacceleratingState();
  }

  public Car(double x, double y, boolean isLeadingCar) {
    this(isLeadingCar);
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public void moveX(double speed) {
    this.x = this.x + speed;
    updateCurrentSpeed(speed);
  }

  public double getY() {
    return y;
  }

  public double getDistanceTo(Car c) {
    return sensor.getDistanceTo(this, c);
  }

  public void move() {
    currentState.move(this, wantedSpeed);
  }

  public double getSpeed() {
    return speed;
  }

  /**
   * Sets the correct MovingState according to the current speed, sets the
   * wantedSpeed
   *
   * @param speed corresponding to the new speed
   */
  public void setSpeed(double speed) {
    setMovingState(speed);
    wantedSpeed = speed;
  }

  public void updateCurrentSpeed(double speed) {
    this.speed = speed;
  }

  public MovingState getMovingState() {
    return currentState;
  }

  public void setMovingState(MovingState movingState) {
    currentState = movingState;
  }

  public void setMovingState(double speed) {
    if (isCarAccelerating(speed)) {
      setMovingState(acceleratingState);
    } else {
      setMovingState(deacceleratingState);
    }
  }

  /**
   * If the car is accelerating, the "new" speed should be higher than the
   * "current" speed
   *
   * @return true if
   */
  public boolean isCarAccelerating(double speed) {
    return speed > this.speed;
  }

  public MovingState getAcceleratingState() {
    return acceleratingState;
  }

  public MovingState getDeacceleratingState() {
    return deacceleratingState;
  }

  public boolean isLeadingCar() {
    return isLeadingCar;
  }

  public Sensor getSensor() {
    return sensor;
  }

  public double getNewSpeed() {
    return newSpeed;
  }

  public void setNewSpeed(Car c) {
    if (this.getMovingState().equals(acceleratingState)) {
      newSpeed = regulator
          .getNewSpeed(acceleratingState.getFactor(), getDistanceTo(c));
    } else {
      newSpeed = regulator
          .getNewSpeed(deacceleratingState.getFactor(), getDistanceTo(c));
    }
  }

  public boolean isCarWithinRangeOf(Car c) {
    return sensor.isCarWithinRangeOf(this, c);
  }

  /**
   * Only used in test methods
   */
  public void setInstantSpeed(double speed) {
    setMovingState(speed);
    this.speed = speed;
  }

  public double getWantedSpeed() {
    return wantedSpeed;
  }
}