
import org.opencv.core.*;

import java.util.ArrayList;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.core.Core.inRange;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;

public class ImageProcessing {

    /**
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

    /**
     * Example, delete later
     *
     * @param args
     */
    public static void main(String[] args) {
        ImageProcessing i = new ImageProcessing();

        i.findCirclesAndDraw("images/circles.jpg", "images/circles2.jpg");

        // i.findCircles("images/circles.jpg");
    }

    /**
     * Finds all the red circles in an image and returns them in a list
     *
     * @param pathToImage the image to check
     * @return a list with the found circles
     */
    public ArrayList<ProcessedImage> findCircles(String pathToImage) {
        // Read the image file
        Mat src = imread(pathToImage);

        MatOfPoint3f circles = findAllCircles(src);

        if (circles.size().width == 0) {
            return null;
        }

        System.out.println("Number of circles found: " + (int) circles.size().width);

        return matrixToList(circles, src);
    }

    /**
     * Finds all the red circles in an image and draws the center point and the circle outline and saves it as an image file
     *
     * @param pathToImage  the image to check
     * @param pathToOutput the output image (include .jpg/.png etc)
     */
    public void findCirclesAndDraw(String pathToImage, String pathToOutput) {
        // Read the image file
        Mat src = imread(pathToImage);

        MatOfPoint3f circles = findAllCircles(src);

        if (circles.size().width == 0) {
            return;
        }

        System.out.println("Number of circles found: " + (int) circles.size().width);

        ArrayList<ProcessedImage> circleList = matrixToList(circles, src);

        for (ProcessedImage p : circleList) {
            // draw circle center
            circle(src, new Point(p.getCenterX(), p.getCenterY()), 3, new Scalar(0, 255, 0), -1, 8, 0);
            // draw circle outline
            circle(src, new Point(p.getCenterX(), p.getCenterY()), (int) p.getRadius(), new Scalar(255, 0, 0), 3, 8, 0);
        }

        // Write image to file
        imwrite(pathToOutput, src);

        System.out.println("\nThe output image was created at " + pathToOutput + "\n");
    }

    /**
     * Finds all the red circles
     *
     * @param src the source image
     * @return a matrix of the found circles
     */
    private MatOfPoint3f findAllCircles(Mat src) {
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

        return circles;
    }

    /**
     * Converts a matrix to a list of circles
     *
     * @param circles the matrix to convert
     * @return an arraylist with the circles
     */
    private ArrayList<ProcessedImage> matrixToList(MatOfPoint3f circles, Mat src) {
        ArrayList<ProcessedImage> circleList = new ArrayList<ProcessedImage>();

        // Draw all found circles
        for (int current_circle = 0; current_circle < circles.size().width; current_circle++) {
            Point center = new Point(circles.get(0, current_circle));

            double[] circle = circles.get(0, current_circle);
            if (circle == null) {
                System.out.println("NULL");
                continue;
            }

            // The radius of the circle
            double radius = circle[2];

            double imageWidth = src.cols();
            // Calculate the center offset of each circle
            double offset = (center.x / imageWidth) * 200.0 - 100;

            circleList.add(new ProcessedImage(center.x, center.y, radius, offset));
        }
        return circleList;
    }

}