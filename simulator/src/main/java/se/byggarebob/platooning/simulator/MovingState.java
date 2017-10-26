package se.byggarebob.platooning.simulator;

// TODO: Auto-generated Javadoc
/**
 * The Interface MovingState.
 *
 * @author Johan Svennungsson
 */
public interface MovingState { /* void move(Car c); */

  /**
  * Move.
  *
  * @param c the c
  * @param wantedSpeed the wanted speed
  */
 void move(Car c, double wantedSpeed);

  /**
   * Gets the factor.
   *
   * @return the factor
   */
  double getFactor();

  /**
   * Sets the factor.
   *
   * @param factor the new factor
   */
  void setFactor(double factor);
}