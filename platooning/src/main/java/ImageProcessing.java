
import org.opencv.core.*;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.core.Core.inRange;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;

public class ImageProcessing {

    /*
     - Add the following lines to platooning.iml before </component> to load OpenCV libraries

     <orderEntry type="module-library">
      <library>
        <CLASSES>
          <root url="jar://$MODULE_DIR$/../opencv/opencv-330.jar!/" />
        </CLASSES>
        <JAVADOC />
        <NATIVE>
          <root url="file://$MODULE_DIR$/../opencv/x64" />
        </NATIVE>
        <SOURCES>
          <root url="jar://$MODULE_DIR$/../opencv/opencv-330.jar!/" />
        </SOURCES>
      </library>
    </orderEntry>

     */

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) { // Source: https://github.com/sol-prog/OpenCV-red-circle-detection/blob/master/circle_detect.cpp
        // Read the image file
        Mat src = imread("images/circles.jpg");

        // Blur to reduce noise
        medianBlur(src, src, 3);

        Mat hsv_image = new Mat();
        cvtColor(src, hsv_image, COLOR_BGR2HSV);

        Mat lower_red_hue_range = new Mat();
        Mat upper_red_hue_range = new Mat();
        inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lower_red_hue_range);
        inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upper_red_hue_range);

        Mat red_hue_image = new Mat();
        // Combine the two matrices
        addWeighted(lower_red_hue_range, 1.0, upper_red_hue_range, 1.0, 0.0, red_hue_image);

        GaussianBlur(red_hue_image, red_hue_image, new Size(9, 9), 2, 2);

        MatOfPoint3f circles = new MatOfPoint3f();

        // Find all the red circles
        HoughCircles(red_hue_image, circles, CV_HOUGH_GRADIENT, 1, red_hue_image.rows() / 8, 100, 20, 0, 0);

        if (circles.size().width == 0) {
            return;
        }

        System.out.println("Number of circles found: " + (int) circles.size().width);

        // Draw all found circles
        for (int current_circle = 0; current_circle < circles.size().width; current_circle++) {
            Point center = new Point(circles.get(0, current_circle));

            double[] circle = circles.get(0, current_circle);
            if (circle == null) {
                System.out.println("NULL");
                continue;
            }

            int radius = (int) circle[2];

            // draw circle center
            circle(src, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            // draw circle outline
            circle(src, center, radius, new Scalar(255, 0, 0), 3, 8, 0);
        }

        // Write the new image to a file
        imwrite("images/circles2.jpg", src);

    }

}