import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Johan Svennungsson
 */
public class AcceleratingStateTest {

  private AcceleratingState acceleratingState;
  private Car car;

  @Before
  public void setUp() {
    acceleratingState = new AcceleratingState(2, 0.1);
    car = new Car(0, 0, false);
  }

  @Test
  public void getAcceleratingState() {
    acceleratingState.setFactor(1.1);
    assertEquals(acceleratingState.getFactor(), 1.1, 0);
  }

  @Test
  public void move() {
    acceleratingState.move(car, 5); /* speed = 0 + 2*0.1 = 0,2 */
    assertEquals(car.getSpeed(), 0.2, 0);
    acceleratingState.move(car, 5); /* speed = 0.2 + 2*0.1 = 0.4 */
    assertEquals(car.getSpeed(), 0.4, 0);
  }
}