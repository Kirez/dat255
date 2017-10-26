package se.byggarebob.platooning.simulator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import se.byggarebob.platooning.simulator.AcceleratingState;
import se.byggarebob.platooning.simulator.Car;
import se.byggarebob.platooning.simulator.DeacceleratingState;

// TODO: Auto-generated Javadoc
/**
 * The Class CarTest.
 *
 * @author Johan Svennungsson
 */
public class CarTest {

  /** The car. */
  private Car car;

  /**
   * Sets the up.
   */
  @Before
  public void setUp() {
    car = new Car(0, 0, false);
  }

  /**
   * Sets the speed.
   */
  @Test
  public void setSpeed() {
    car.setSpeed(5);
    assertEquals(car.getWantedSpeed(), 5, 0);
  }

  /**
   * Sets the moving state with speed.
   */
  @Test
  public void setMovingStateWithSpeed() {
    assertEquals(car.getSpeed(), 0, 0);
    car.setMovingState(5);
  }

  /**
   * Sets the moving state with state.
   */
  @Test
  public void setMovingStateWithState() {
    AcceleratingState accState = new AcceleratingState();
    DeacceleratingState deaccState = new DeacceleratingState();
    car.setMovingState(accState);
    assertEquals(car.getMovingState(), accState);
    car.setMovingState(deaccState);
    assertEquals(car.getMovingState(), deaccState);
  }

  /**
   * Sets the accelerating state.
   */
  @Test
  public void setAcceleratingState() {
    assertEquals(car.getSpeed(), 0, 0);
    car.setInstantSpeed(5);
    assertEquals(car.getSpeed(), 5, 0);
    assertEquals(car.getAcceleratingState(), car.getMovingState());
  }

  /**
   * Sets the deaccelerating state.
   */
  @Test
  public void setDeacceleratingState() {
    car.setInstantSpeed(5);
    assertEquals(car.getSpeed(), 5, 0);
    car.setInstantSpeed(3);
    assertEquals(car.getSpeed(), 3, 0);
    assertEquals(car.getMovingState(), car.getDeacceleratingState());
  }

  /**
   * Checks if is leading car.
   */
  @Test
  public void isLeadingCar() {
    Car leadingCar = new Car(true);
    assertEquals(leadingCar.isLeadingCar(), true);
    Car followingCar = new Car(false);
    assertEquals(followingCar.isLeadingCar(), false);
  }

  /**
   * Checks if is leading car with coordinates.
   */
  @Test
  public void isLeadingCarWithCoordinates() {
    Car leadingCar = new Car(50, 50, true);
    assertEquals(leadingCar.isLeadingCar(), true);
    Car followingCar = new Car(100, 100, false);
    assertEquals(followingCar.isLeadingCar(), false);
  }

  /**
   * Checks if is car accelerating.
   */
  @Test
  public void isCarAccelerating() {
    car.setInstantSpeed(5);
    assertEquals(car.isCarAccelerating(4), false);
    assertEquals(car.isCarAccelerating(6), true);
  }

  /**
   * Gets the sensor.
   */
  @Test
  public void getSensor() {
    Car leadingCar = new Car(true);
    assertEquals(leadingCar.getSensor(), null);
    Car followingCar = new Car(false);
    assertNotEquals(followingCar.getSensor(), null);
  }

  /**
   * Checks if is car within range.
   */
  @Test
  public void isCarWithinRange() {
    Car nearbyCar = new Car(0, 40, false);
    assertEquals(car.isCarWithinRangeOf(nearbyCar), true);
    Car farawayCar = new Car(0, 41, false);
    assertEquals(car.isCarWithinRangeOf(farawayCar), false);
  }
}