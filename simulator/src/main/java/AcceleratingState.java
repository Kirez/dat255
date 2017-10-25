// TODO: Auto-generated Javadoc
/**
 * The Class AcceleratingState.
 *
 * @author Johan Svennungsson
 */
public class AcceleratingState implements MovingState {

  /** The accelerating factor. */
  private double acceleratingFactor = 1.1;
  
  /** The time step. */
  private double timeStep = 0.1;

  /**
   * Instantiates a new accelerating state.
   */
  public AcceleratingState() { /* empty constructor */ }

  /**
   * Instantiates a new accelerating state.
   *
   * @param acceleratingFactor the accelerating factor
   * @param timeStep the time step
   */
  public AcceleratingState(double acceleratingFactor, double timeStep) {
    this.acceleratingFactor = acceleratingFactor;
    this.timeStep = timeStep;
  }

  /* (non-Javadoc)
   * @see MovingState#move(Car, double)
   */
  public void move(Car c, double wantedSpeed) {
    if (c.getSpeed() < wantedSpeed) {
      c.moveX(c.getSpeed() + acceleratingFactor * timeStep);
    } else {
      c.moveX(c.getSpeed());
    } /* System.out.println(c.getSpeed() + acceleratingFactor * timeStep); */
  }

  /* (non-Javadoc)
   * @see MovingState#getFactor()
   */
  public double getFactor() {
    return acceleratingFactor;
  }

  /* (non-Javadoc)
   * @see MovingState#setFactor(double)
   */
  public void setFactor(double acceleratingFactor) {
    this.acceleratingFactor = acceleratingFactor;
  }
}