
import org.opencv.core.*;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.addWeighted;
import static org.opencv.core.Core.inRange;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;

public class ImageProcessing {

    /**
     - Create platooning.iml in the platooning folder and add the following lines to load OpenCV libraries:
     - Or follow: https://medium.com/@aadimator/how-to-set-up-opencv-in-intellij-idea-6eb103c1d45c

     <?xml version="1.0" encoding="UTF-8"?>
     <module org.jetbrains.idea.maven.project.MavenProjectsManager.isMavenModule="true" type="JAVA_MODULE" version="4">
     <component name="NewModuleRootManager" LANGUAGE_LEVEL="JDK_1_5">
     <output url="file://$MODULE_DIR$/target/classes" />
     <output-test url="file://$MODULE_DIR$/target/test-classes" />
     <content url="file://$MODULE_DIR$">
     <sourceFolder url="file://$MODULE_DIR$/src/main/java" isTestSource="false" />
     <excludeFolder url="file://$MODULE_DIR$/target" />
     </content>
     <orderEntry type="inheritedJdk" />
     <orderEntry type="sourceFolder" forTests="false" />
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
     </component>
     </module>

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

        i.findCirclesAndDraw("images/lind.jpg", "images/lind2.jpg");

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

        List<MatOfPoint> circles = findAllCircles(src);

        if (circles.size() == 0) {
            return null;
        }

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

        List<MatOfPoint> circles = findAllCircles(src);
        int counter = 0;

        for (final MatOfPoint circle : circles) {
            // Create an ellipse and draw it
            RotatedRect ellipse = fitEllipse(new MatOfPoint2f(circle.toArray()));

            if ((ellipse.angle >= 80 && ellipse.angle <= 110) || ellipse.size.area() <= 5) {
                continue;
            }

            ellipse(src, ellipse, new Scalar(255, 0, 0), 4);

            // draw circle center
            circle(src, ellipse.center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            counter++;
        }

        System.out.println("Number of circles & ellipses found: " + counter);

        imwrite(pathToOutput, src);

        System.out.println("\nThe output image was created at " + pathToOutput);
    }

    /**
     * Finds all the red circles & ellipses
     *
     * @param src the source image
     * @return a matrix of the found circles
     */
    private List<MatOfPoint> findAllCircles(Mat src) {
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

        List<MatOfPoint> circles = new ArrayList<MatOfPoint>();
        findContours(red_hue_image, circles, new Mat(), RETR_CCOMP, CHAIN_APPROX_SIMPLE, new Point(0, 0));

        return circles;
    }

    /**
     * Converts a list of matrices to a list of circles
     *
     * @param circles the list of matrices to convert
     * @return an arraylist with the circles
     */
    private ArrayList<ProcessedImage> matrixToList(List<MatOfPoint> circles, Mat src) {
        ArrayList<ProcessedImage> circleList = new ArrayList<ProcessedImage>();

        for (final MatOfPoint circle : circles) {
            Point center = new Point(circle.get(0, 0));

            RotatedRect ellipse = fitEllipse(new MatOfPoint2f(circle.toArray()));

            if ((ellipse.angle >= 80 && ellipse.angle <= 110) || ellipse.size.area() <= 5) {
                continue;
            }

            double imageWidth = src.cols();
            // Calculate the center offset of each circle
            double offset = (center.x / imageWidth) * 200.0 - 100;

            circleList.add(new ProcessedImage(ellipse.center.x, ellipse.center.y, offset));
        }

        System.out.println("Number of circles & ellipses found: " + circleList.size());

        return circleList;
    }

}