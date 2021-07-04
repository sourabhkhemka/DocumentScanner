package scan;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Scan {

    // Mat objects to store input image, filtered image from HPF & scanned image
    private Mat inputImg;
    private Mat filtered = new Mat();
    private Mat processedImg = new Mat();

    private int kSize;
    private int blackPoint;
    private int whitePoint;

    // enum type to help select mode of scanning
    public static enum ScanMode{
        GCMODE,
        RMODE,
        SMODE,
    }

    // constructor
    public Scan (Mat image, int kernelSize, int blackPoint, int whitePoint){
        this.inputImg = image.clone();
        this.kSize = kernelSize;
        this.blackPoint = blackPoint;
        this.whitePoint = whitePoint;
    }


    /* High Pass Filter
     * Output of HPF depends on the kernel size provided through the constructor
     * Links to the docuementation:
     *  Introduction: https://github.com/sourabhkhemka/DocumentScanner/wiki/Scan:-Introduction
     *  HPF: https://github.com/sourabhkhemka/DocumentScanner/wiki/GCMODE
     */
    private void highPassFilter() 
    {        
        if ( kSize%2 == 0 )
            kSize++;
        
        Mat kernel = Mat.ones(kSize, kSize, CvType.CV_32FC1);
        kernel = kernel.mul(kernel, 1/((float)kSize * (float)kSize));
        
        Imgproc.filter2D(inputImg, filtered, -1, kernel);

        // Convert both to float to avoid saturation of pixel values to 255
        filtered.convertTo(filtered, CvType.CV_32FC3);
        inputImg.convertTo(inputImg, CvType.CV_32FC3);

        Core.subtract(inputImg, filtered, filtered);

        kernel = Mat.zeros(inputImg.size(), CvType.CV_32FC3);
        kernel.setTo(new Scalar(1,1,1));
        
        Core.multiply(kernel, new Scalar(127.0, 127.0, 127.0), kernel);
        
        Imgproc.cvtColor(filtered,filtered,Imgproc.COLOR_RGBA2RGB);
        
        Core.add(filtered, kernel, filtered);

        filtered.convertTo(filtered, CvType.CV_8UC3);

        // "filtered" now contains high pass filtered image.
    }


    /* Method to select whitePoint in the image
     *
     * Links to documentation: 
     *  Introduction: https://github.com/sourabhkhemka/DocumentScanner/wiki/Scan:-Introduction
     *  White Point Select: https://github.com/sourabhkhemka/DocumentScanner/wiki/White-Point-Select
     */
    private void whitePointSelect(){

        // refer repository's wiki page for detailed explanation

        Imgproc.threshold(processedImg, processedImg, whitePoint, 255, Imgproc.THRESH_TRUNC);

        Core.subtract(processedImg, new Scalar(0, 0, 0), processedImg);
        
        float tmp = (255.0f) / ((float)whitePoint - 0);
        Core.multiply(processedImg, new Scalar(tmp, tmp, tmp), processedImg);
        
    }

    /* Method to select black point in the image
     *
     * Links to documentation: 
     *  Introduction: https://github.com/sourabhkhemka/DocumentScanner/wiki/Scan:-Introduction
     *  Black Point Select: https://github.com/sourabhkhemka/DocumentScanner/wiki/Black-Point-Select
     */    
    private void blackPointSelect(){

        // refer repository's wiki page for detailed explanation

        Core.subtract(processedImg, new Scalar(blackPoint, blackPoint, blackPoint), processedImg);
        
        float tmp = (255.0f) / (255.0f - blackPoint);
        Core.multiply(processedImg, new Scalar(tmp, tmp, tmp), processedImg);
    }

    /* Method to process image in LAB color space to generate black and white images
     *  Wiki link: https://github.com/sourabhkhemka/DocumentScanner/wiki/SMODE:-Black-&-White
     */
    private void blackAndWhite(){

        // refer repository's wiki page for detailed explanation
        List <Mat> lab = new ArrayList<>();
        Mat subA = new Mat();
        Mat subB = new Mat();

        Imgproc.cvtColor(processedImg, processedImg, Imgproc.COLOR_BGR2Lab);
        Core.split(processedImg, lab);

        Core.subtract(lab.get(0), lab.get(1), subA);
        Core.subtract(lab.get(0), lab.get(2), subB);

        Core.add(subA, subB, processedImg);
    }


    /* Method scanImage is the only public method of the Scan class.
     *  This method will be called to scan the image provided at time of
     *  constructing Scan class' object.
     * 
     * scanImage method uses switch-case to execute required methods in correct order
     *  to implemnt desired mode of scanning.
     * 
     * This method takes enum type as argument.
     * 
     * whitePointSelect() and blackPointSelect() methods are designed to process "processedImg"
     *  hence "inputImg" is copied to "processedImg" in RMODE and SMODE.
     *  highPassFilter() outputs filtered image as "filtered" hence we need to copy "filtered"
     *  to "processedImg" so that whitePointSelect() can further process it.
     */
    public Mat scanImage(ScanMode mode) {
        
        switch (mode) {
            case GCMODE:
                this.highPassFilter();
                processedImg = filtered.clone();
                // Fix white point value at 127 for GCMODE
                this.whitePoint = 127;
                this.whitePointSelect();
                this.blackPointSelect();
                break;

            case RMODE:
                processedImg = inputImg.clone();
                this.blackPointSelect();
                this.whitePointSelect();
                break;

            case SMODE:
                processedImg = inputImg.clone();
                this.blackPointSelect();
                this.whitePointSelect();
                this.blackAndWhite();
                break;
        
            default:
                System.out.println("Error: Incorrect ScanMode supplied. Expected input: GCSCAN/RSCAN/SCAN");
                break;
        }


    return processedImg;
    }
}
