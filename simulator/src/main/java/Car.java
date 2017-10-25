// TODO: Auto-generated Javadoc
/**
 * The Class Car.
 */
public class Car {

  /** The speed. */
  private double speed;
  
  /** The wanted speed. */
  private double wantedSpeed;
  
  /** The new speed. */
  private double newSpeed;
  
  /** The is leading car. */
  private boolean isLeadingCar;
  
  /** The regulator. */
  private Regulator regulator;
  
  /** The sensor. */
  private Sensor sensor;
  
  /** The current state. */
  private MovingState currentState;
  
  /** The accelerating state. */
  private MovingState acceleratingState;
  
  /** The deaccelerating state. */
  private MovingState deacceleratingState;
  
  /** The y. */
  private double x, y;

  /**
   * Instantiates a new car.
   *
   * @param isLeadingCar the is leading car
   */
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

  /**
   * Instantiates a new car.
   *
   * @param x the x
   * @param y the y
   * @param isLeadingCar the is leading car
   */
  public Car(double x, double y, boolean isLeadingCar) {
    this(isLeadingCar);
    this.x = x;
    this.y = y;
  }

  /**
   * Gets the x.
   *
   * @return the x
   */
  public double getX() {
    return x;
  }

  /**
   * Move X.
   *
   * @param speed the speed
   */
  public void moveX(double speed) {
    this.x = this.x + speed;
    updateCurrentSpeed(speed);
  }

  /**
   * Gets the y.
   *
   * @return the y
   */
  public double getY() {
    return y;
  }

  /**
   * Gets the distance to.
   *
   * @param c the c
   * @return the distance to
   */
  public double getDistanceTo(Car c) {
    return sensor.getDistanceTo(this, c);
  }

  /**
   * Move.
   */
  public void move() {
    currentState.move(this, wantedSpeed);
  }

  /**
   * Gets the speed.
   *
   * @return the speed
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * Sets the correct MovingState according to the current speed, sets the
   * wantedSpeed.
   *
   * @param speed corresponding to the new speed
   */
  public void setSpeed(double speed) {
    setMovingState(speed);
    wantedSpeed = speed;
  }

  /**
   * Update current speed.
   *
   * @param speed the speed
   */
  public void updateCurrentSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * Gets the moving state.
   *
   * @return the moving state
   */
  public MovingState getMovingState() {
    return currentState;
  }

  /**
   * Sets the moving state.
   *
   * @param movingState the new moving state
   */
  public void setMovingState(MovingState movingState) {
    currentState = movingState;
  }

  /**
   * Sets the moving state.
   *
   * @param speed the new moving state
   */
  public void setMovingState(double speed) {
    if (isCarAccelerating(speed)) {
      setMovingState(acceleratingState);
    } else {
      setMovingState(deacceleratingState);
    }
  }

  /**
   * If the car is accelerating, the "new" speed should be higher than the
   * "current" speed.
   *
   * @param speed the speed
   * @return true if
   */
  public boolean isCarAccelerating(double speed) {
    return speed > this.speed;
  }

  /**
   * Gets the accelerating state.
   *
   * @return the accelerating state
   */
  public MovingState getAcceleratingState() {
    return acceleratingState;
  }

  /**
   * Gets the deaccelerating state.
   *
   * @return the deaccelerating state
   */
  public MovingState getDeacceleratingState() {
    return deacceleratingState;
  }

  /**
   * Checks if is leading car.
   *
   * @return true, if is leading car
   */
  public boolean isLeadingCar() {
    return isLeadingCar;
  }

  /**
   * Gets the sensor.
   *
   * @return the sensor
   */
  public Sensor getSensor() {
    return sensor;
  }

  /**
   * Gets the new speed.
   *
   * @return the new speed
   */
  public double getNewSpeed() {
    return newSpeed;
  }

  /**
   * Sets the new speed.
   *
   * @param c the new new speed
   */
  public void setNewSpeed(Car c) {
    if (this.getMovingState().equals(acceleratingState)) {
      newSpeed = regulator
          .getNewSpeed(acceleratingState.getFactor(), getDistanceTo(c));
    } else {
      newSpeed = regulator
          .getNewSpeed(deacceleratingState.getFactor(), getDistanceTo(c));
    }
  }

  /**
   * Checks if is car within range of.
   *
   * @param c the c
   * @return true, if is car within range of
   */
  public boolean isCarWithinRangeOf(Car c) {
    return sensor.isCarWithinRangeOf(this, c);
  }

  /**
   * Only used in test methods.
   *
   * @param speed the new instant speed
   */
  public void setInstantSpeed(double speed) {
    setMovingState(speed);
    this.speed = speed;
  }

  /**
   * Gets the wanted speed.
   *
   * @return the wanted speed
   */
  public double getWantedSpeed() {
    return wantedSpeed;
  }
}