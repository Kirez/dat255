/**
 * @author Johan Svennungsson
 */
public class DeacceleratingState implements MovingState {

    private double deacceleratingFactor;
    private double timeStep = 0.1;

    public DeacceleratingState() {
        //empty constructor
    }

    public DeacceleratingState(double deacceleratingFactor, double timeStep) {
        this.deacceleratingFactor = deacceleratingFactor;
        this.timeStep = timeStep;
    }

    public void move(Car c, double wantedSpeed) {
        //TODO: implement deacceleration
    }

    public double getFactor() {
        return deacceleratingFactor;
    }

    public void setFactor(double deacceleratingFactor) {
        this.deacceleratingFactor = deacceleratingFactor;
    }
}
