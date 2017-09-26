
public class Distance {

    private CarAPITestV api;
    private long lastTime;
    private double Input, Output, Setpoint;
    private double errSum, lastErr;
    private double kp, ki, kd;

    public static void main(String[] args){
        new Distance(1, 1,5);
    }

    public Distance(double kp, double ki, double kd) {
        api = CarAPITestV.getInstance();
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        Setpoint = 50;
        for(int i = 0; i < 10; i++)
        {
            api.setSpeed(computePID(0.4));
        }

    }

    public double computePID(double timeChange)
    {
        double error = api.readSensor() - Setpoint;
        errSum += (error * timeChange);
        double dErr = (error - lastErr) / timeChange;
        Output = kp * error + ki * errSum + kd * dErr;
        lastErr = error;
        System.out.println("This is our error: " + error + "This is the new speed value: " + Output);

        return Output;
    }
}