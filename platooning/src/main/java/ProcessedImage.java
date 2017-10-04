public class ProcessedImage {
    private final double centerX, centerY, xOffset, height;

    ProcessedImage(double centerX, double centerY, double xOffset, double height) {
        this.centerX = centerX;
        this.centerY = centerY;
        if (xOffset > 100) {
            xOffset = 100;
        } else if (xOffset < -100) {
            xOffset = -100;
        }
        this.xOffset = xOffset;
        this.height = height;
    }

    /**
     * The x coordinate of the circle's center point
     *
     * @return x
     */
    public double getCenterX() {
        return centerX;
    }

    /**
     * The y coordinate of the circle's center point
     *
     * @return y
     */
    public double getCenterY() {
        return centerY;
    }

    /**
     * The offset of the circle's center point
     *
     * @return a value between 0 and 100 if the circle's center point is to the right of the image's center, and a value between -100 and 0 if it's to the left
     */
    public double getxOffset() {
        return xOffset;
    }

    /**
     * The height of the circle
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

}