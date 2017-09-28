public interface IMotorVoltage {

    /**
     * @return the current motor value
     */
    byte getMotorVoltage();

    /**
     * Calling setMotorValue(25) should make getMotorVoltage() return 25.
     * @param voltage 0 should make the motor stop running.
     */
    void setMotorValue(byte voltage);
}
