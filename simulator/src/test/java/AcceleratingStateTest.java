import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class AcceleratingStateTest.
 *
 * @author Johan Svennungsson
 */
public class AcceleratingStateTest {

  /** The accelerating state. */
  private AcceleratingState acceleratingState;
  
  /** The car. */
  private Car car;

  /**
   * Sets the up.
   */
  @Before
  public void setUp() {
    acceleratingState = new AcceleratingState(2, 0.1);
    car = new Car(0, 0, false);
  }

  /**
   * Gets the accelerating state.
   */
  @Test
  public void getAcceleratingState() {
    acceleratingState.setFactor(1.1);
    assertEquals(acceleratingState.getFactor(), 1.1, 0);
  }

  /**
   * Move.
   */
  @Test
  public void move() {
    acceleratingState.move(car, 5); /* speed = 0 + 2*0.1 = 0,2 */
    assertEquals(car.getSpeed(), 0.2, 0);
    acceleratingState.move(car, 5); /* speed = 0.2 + 2*0.1 = 0.4 */
    assertEquals(car.getSpeed(), 0.4, 0);
  }
}