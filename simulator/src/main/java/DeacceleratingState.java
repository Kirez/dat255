// TODO: Auto-generated Javadoc
/**
 * The Class DeacceleratingState.
 *
 * @author Johan Svennungsson
 */
public class DeacceleratingState implements MovingState {

  /** The deaccelerating factor. */
  private double deacceleratingFactor = 0.9;
  
  /** The time step. */
  private double timeStep = 0.1;

  /**
   * Instantiates a new deaccelerating state.
   */
  public DeacceleratingState() { /* empty constructor */ }

  /**
   * Instantiates a new deaccelerating state.
   *
   * @param deacceleratingFactor the deaccelerating factor
   * @param timeStep the time step
   */
  public DeacceleratingState(double deacceleratingFactor, double timeStep) {
    this.deacceleratingFactor = deacceleratingFactor;
    this.timeStep = timeStep;
  }

  /* (non-Javadoc)
   * @see MovingState#move(Car, double)
   */
  public void move(Car c, double wantedSpeed) {
    if (c.getSpeed() > wantedSpeed) {
      c.moveX(c.getSpeed() - deacceleratingFactor * timeStep);
    } else {
      c.moveX(c.getSpeed());
    }
  }

  /* (non-Javadoc)
   * @see MovingState#getFactor()
   */
  public double getFactor() {
    return deacceleratingFactor;
  }

  /* (non-Javadoc)
   * @see MovingState#setFactor(double)
   */
  public void setFactor(double deacceleratingFactor) {
    this.deacceleratingFactor = deacceleratingFactor;
  }
}