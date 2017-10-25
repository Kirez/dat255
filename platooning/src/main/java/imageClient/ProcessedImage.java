package imageClient;

// TODO: Auto-generated Javadoc
/**
 * The Class ProcessedImage.
 */
public class ProcessedImage {

  /** The width. */
  private final double centerX, centerY, xOffset, height, width;

  /**
   * Instantiates a new processed image.
   *
   * @param centerX the center X
   * @param centerY the center Y
   * @param xOffset the x offset
   * @param height the height
   * @param width the width
   */
  ProcessedImage(double centerX, double centerY, double xOffset, double height,
      double width) {
    this.centerX = centerX;
    this.centerY = centerY;
    if (xOffset > 100) {
      xOffset = 100;
    } else if (xOffset < -100) {
      xOffset = -100;
    }
    this.xOffset = xOffset;
    this.height = height;
    this.width = width;
  }

  /**
   * The x coordinate of the circle's center point.
   *
   * @return x
   */
  public double getCenterX() {
    return centerX;
  }

  /**
   * The y coordinate of the circle's center point.
   *
   * @return y
   */
  public double getCenterY() {
    return centerY;
  }

  /**
   * The offset of the circle's center point.
   *
   * @return a value between 0 and 100 if the circle's center point is to the
   * right of the image's center, and a value between -100 and 0 if it's to the
   * left
   */
  public double getxOffset() {
    return xOffset;
  }

  /**
   * The height of the circle.
   *
   * @return the height
   */
  public double getHeight() {
    return height;
  }

  /**
   * The width of the circle.
   *
   * @return the width
   */
  public double getWidth() {
    return width;
  }
}