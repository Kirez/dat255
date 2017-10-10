
import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.Point2D;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.*;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.videoio.Videoio.CAP_FFMPEG;

public class ImageProcessing {

    static {
        OpenCV.loadShared();
    }

    /**
     * Example, delete later
     *
     * @param args
     */
    public static void main(String[] args) {
        ImageProcessing i = new ImageProcessing();

        long start = System.currentTimeMillis();

        //i.findCirclesAndDraw("images/opencv-dots.jpg", "images/opencv-dots2.jpg");


        String argv = "nc -l 2222";
        VideoCapture stream = new VideoCapture();

        stream.open("tcp://192.168.43.230:2222");
        Mat frame = new Mat();

        while (true) {

            if (stream.read(frame)) {
                System.out.println("hejhejehejeh");

                ProcessedImage circle = i.findCircles(frame, "images/opencv-dots2.jpg", false);

                if (circle == null) {
                    System.out.println("NULL");
                } else {
                    System.out.println("x, y: " + (int) circle.getCenterX() + ", " + (int) circle.getCenterY() + ", x offset: " + (int) circle.getxOffset());
                }

                long time = System.currentTimeMillis() - start;

                System.out.println("Time taken: " + time + "ms\n");

            }

        }
    }

    /**
     * Finds the center circle of the three
     *
     * @param image      the image to check
     * @param pathToOutput     where the image will be saved
     * @param drawCenterCircle if an image with the result should be saved
     * @return the center circle
     */
    public ProcessedImage findCircles(Mat image, String pathToOutput, boolean drawCenterCircle) {
        // Read the image file

        List<MatOfPoint> circles = findAllCircles(image);

        if (circles.size() == 0) {
            return null;
        }

        ProcessedImage centerCircle = matrixToList(circles, image);

        if (centerCircle == null) {
            return null;
        }

        if (drawCenterCircle) {
            for (final MatOfPoint circle : circles) {
                if (circle.toArray().length < 5) { // Crashes when trying to create an ellipse with less than 5 points
                    continue;
                }

                // Create an ellipse and draw it
                RotatedRect ellipse = fitEllipse(new MatOfPoint2f(circle.toArray()));

                if (centerCircle.getCenterX() != ellipse.center.x) {
                    continue;
                }

                ellipse(image, ellipse, new Scalar(255, 0, 0), 4);

                // draw circle center
                circle(image, ellipse.center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            }

            imwrite(pathToOutput, image);
        }

        return centerCircle;
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
        inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lower_red_hue_range);// new Scalar(blue, green, red)
        inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upper_red_hue_range);

        Mat red_hue_image = new Mat();
        // Combine the two matrices
        addWeighted(lower_red_hue_range, 1.0, upper_red_hue_range, 1.0, 0.0, red_hue_image);

        GaussianBlur(red_hue_image, red_hue_image, new Size(3, 3), 2, 2);

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
    private ProcessedImage matrixToList(List<MatOfPoint> circles, Mat src) {
        ArrayList<ProcessedImage> circleList = new ArrayList<ProcessedImage>();

        for (final MatOfPoint circle : circles) {
            Point center = new Point(circle.get(0, 0));

            if (circle.toArray().length < 5) {
                continue;
            }

            RotatedRect ellipse = fitEllipse(new MatOfPoint2f(circle.toArray()));

            if ((ellipse.angle >= 80 && ellipse.angle <= 110) || ellipse.size.area() <= 5) {
                continue;
            }

            double imageWidth = src.cols();
            // Calculate the center offset of each circle
            double offset = (center.x / imageWidth) * 200.0 - 100;
            RotatedRect copy = new RotatedRect(ellipse.center, ellipse.size, 0);

            double height;
            if (copy.size.height >= copy.size.width) {
                height = copy.size.height;
            } else {
                height = copy.size.width;
            }

            circleList.add(new ProcessedImage(ellipse.center.x, ellipse.center.y, offset, height));
        }

        System.out.println("Number of circles & ellipses found: " + circleList.size());

        return findCorrectCircle(circleList);
    }

    /**
     * Loops through all circles and checks if 3 of them are in a line
     *
     * @param circleList the list of circles to check
     * @return the center circle
     */
    private ProcessedImage findCorrectCircle(ArrayList<ProcessedImage> circleList) {
        for (int i = 0; i < circleList.size(); i++) {
            for (int j = 0; j < circleList.size(); j++) {
                for (int k = 0; k < circleList.size(); k++) {

                    if (i == j || j == k || i == k) {
                        continue;
                    }

                    ProcessedImage c1 = circleList.get(i);
                    ProcessedImage c2 = circleList.get(j);
                    ProcessedImage c3 = circleList.get(k);

                    if (lineIntersectsCircle(c1, c2, c3)) {
                        return c2;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if the center circle intersects the line from c1 to c3
     *
     * @param c1     the first circle
     * @param center the center circle
     * @param c3     the 3rd circle
     * @return true if the center circle is intersecting the line between c1 and c3
     */
    private boolean lineIntersectsCircle(ProcessedImage c1, ProcessedImage center, ProcessedImage c3) {
        final double circleRadius = center.getHeight() / 2;

        Line2D line = new Line2D((float) c1.getCenterX(), (float) c1.getCenterY(), (float) c3.getCenterX(), (float) c3.getCenterY());
        Point2D point = new Point2D((float) center.getCenterX(), (float) center.getCenterY());

        double distanceFromCircleToLine = line.ptSegDist(point);

        return distanceFromCircleToLine <= circleRadius;
    }

}