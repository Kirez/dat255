/**
 * @author Johan Svennungsson
 */
public class AcceleratingState implements MovingState {

    private double acceleratingFactor = 1.1;
    private double timeStep = 0.1;

    public AcceleratingState() {
        /* empty constructor */
    }

    public AcceleratingState(double acceleratingFactor, double timeStep) {
        this.acceleratingFactor = acceleratingFactor;
        this.timeStep = timeStep;
    }

    public void move(Car c, double wantedSpeed) {
        if (c.getSpeed() < wantedSpeed) {
            c.moveX(c.getSpeed() + acceleratingFactor * timeStep);
        } else {
            c.moveX(c.getSpeed());
        }
/*         System.out.println(c.getSpeed() + acceleratingFactor * timeStep); */
    }

    public double getFactor() {
        return acceleratingFactor;
    }

    public void setFactor(double acceleratingFactor) {
        this.acceleratingFactor = acceleratingFactor;
    }
}
