/**
 * @author Johan Svennungsson
 */
public class DeacceleratingState implements MovingState {

    private double deacceleratingFactor = 0.9;
    private double timeStep = 0.1;

    public DeacceleratingState() {
        /* empty constructor */
    }

    public DeacceleratingState(double deacceleratingFactor, double timeStep) {
        this.deacceleratingFactor = deacceleratingFactor;
        this.timeStep = timeStep;
    }

    public void move(Car c, double wantedSpeed) {
        if (c.getSpeed() > wantedSpeed) {
            c.moveX(c.getSpeed() - deacceleratingFactor * timeStep);
        } else {
            c.moveX(c.getSpeed());
        }
    }

    public double getFactor() {
        return deacceleratingFactor;
    }

    public void setFactor(double deacceleratingFactor) {
        this.deacceleratingFactor = deacceleratingFactor;
    }
}
