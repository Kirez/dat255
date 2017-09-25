public interface IMotorVoltage {

    /**
     * @return the current motor voltage
     */
    double getMotorVoltage();

    /**
     * Calling setMotorVoltage(3.0) should make getMotorVoltage() return 3.0.
     * @param voltage 0.0 should make the motor stop running.
     */
    void setMotorVoltage(double voltage);
}
