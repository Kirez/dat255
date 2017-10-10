/**
 * @author Johan Svennungsson
 */
public interface MovingState { /* void move(Car c); */

  void move(Car c, double wantedSpeed);

  double getFactor();

  void setFactor(double factor);
}