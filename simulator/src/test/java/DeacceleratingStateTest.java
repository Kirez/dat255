import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import se.byggarebob.platooning.simulator.AcceleratingState;
import se.byggarebob.platooning.simulator.Car;
import se.byggarebob.platooning.simulator.DeacceleratingState;

// TODO: Auto-generated Javadoc
/**
 * The Class DeacceleratingStateTest.
 *
 * @author Johan Svennungsson
 */
public class DeacceleratingStateTest {

  /** The accelerating state. */
  private AcceleratingState acceleratingState;
  
  /** The deaccelerating state. */
  private DeacceleratingState deacceleratingState;
  
  /** The car. */
  private Car car;

  /**
   * Sets the up.
   */
  @Before
  public void setUp() {
    acceleratingState = new AcceleratingState();
    deacceleratingState = new DeacceleratingState();
    car = new Car(0, 0, false);
  }

  /**
   * Move.
   */
  @Test
  public void move() {
    double wantedSpeed = 5;
    car.setSpeed(wantedSpeed);
    while (car.getSpeed() < wantedSpeed) {
      acceleratingState.move(car, wantedSpeed);
    }
    assertEquals(car.getSpeed(), 5, 0.1);
    assertEquals(car.getMovingState(), car.getAcceleratingState()); /* now deaccelerate */
    double newWantedSpeed = 3;
    car.setSpeed(newWantedSpeed);
    assertEquals(car.getMovingState(), car.getDeacceleratingState());
    deacceleratingState.move(car, newWantedSpeed); /* speed = 5.06 - 0.9*0.1 = 4.97 */
    assertEquals(car.getSpeed(), 4.97, 0.01);
    deacceleratingState.move(car, newWantedSpeed); /* speed = 4.97 - 0.9*0.1 = 4.88 */
    assertEquals(car.getSpeed(), 4.88, 0.01);
  }

  /**
   * Move and change state.
   */
  @Test
  public void moveAndChangeState() {
    double wantedSpeed = 5;
    car.setSpeed(wantedSpeed);
    while (car.getSpeed() < wantedSpeed) {
      acceleratingState.move(car, wantedSpeed);
    }
    assertEquals(car.getSpeed(), 5, 0.1);
    assertEquals(car.getMovingState(), car.getAcceleratingState());
    double newWantedSpeed = 3;
    car.setSpeed(newWantedSpeed);
    assertEquals(car.getMovingState(), car.getDeacceleratingState());
    while (newWantedSpeed < car.getSpeed()) {
      deacceleratingState.move(car, newWantedSpeed);
    }
    assertEquals(car.getSpeed(), 3, 0.1);
  }
}