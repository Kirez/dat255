class ALCRegulator {

    private final ServoControl servo;
    private final int k;

    public ALCRegulator(ServoControl servo){
        this.servo = servo;
        k = 1;
    }

    public void calcSteering(int offset) {
        int angle = k * offset;
        servo.steer(angle);
    }
}
