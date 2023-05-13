package lk.ijse.gdse.qr;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WebcamService extends Service<Image> {
    private BufferedImage bimg;
    private final Webcam cam;
    private final WebcamResolution resolution;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public WebcamService(Webcam webcam, WebcamResolution resolution) {
        this.cam = webcam;
        this.resolution = resolution;
        webcam.setCustomViewSizes(new Dimension[] {resolution.getSize()});
        webcam.setViewSize(resolution.getSize());
    }

    public WebcamService(Webcam webcam) {
        this(webcam, WebcamResolution.QVGA);
    }


    @Override
    protected Task<Image> createTask() {
        return new Task<Image>() {
            @Override
            protected Image call() throws Exception {

                Thread t1 = new Thread(() -> {
                    while (!isCancelled()){
                        try {
                            Thread.sleep(1000);
                            updateProgress(10,100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(bimg!=null) {
                            String s = QrDecoder.decodeQRCode(bimg);

                            Mat img = BufferedImage2Mat(bimg);
                            QRCodeDetector decoder = new QRCodeDetector();
                            Mat points = new Mat();
                            String data = decoder.detectAndDecode(img, points);
                            if(s != null){
                                updateMessage(s);
                                if (!points.empty()) {
                                    System.out.println("Decoded data: " + data);

                                    for (int i = 0; i < points.cols(); i++) {
                                        Point pt1 = new Point(points.get(0, i));
                                        Point pt2 = new Point(points.get(0, (i + 1) % 4));
                                        Imgproc.line(img, pt1, pt2, new Scalar(255, 0, 0), 3);
                                    }

                                    HighGui.imshow("Detected QR code", img);
                                    HighGui.waitKey(0);
                                    HighGui.destroyAllWindows();

                                }
                            }else{
                                try {
                                    updateProgress(10,100);
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                continue;
                            }
                        }

                        try {
                            updateProgress(50,100);
                            Thread.sleep(200);
                            updateProgress(80,100);
                            Thread.sleep(200);
                            updateProgress(100,100);
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t1.start();

                try {
                    cam.open();
                    while (!isCancelled()) {
                        if (cam.isImageNew()) {
                            bimg = cam.getImage();
                            updateValue(SwingFXUtils.toFXImage(bimg, null));
                        }
                    }
                    System.out.println("Cancelled, closing cam");
                    cam.close();
                    System.out.println("Cam closed");
                    return getValue();
                } finally {
                    cam.close();
                }
            }

        };
    }

    public int getCamWidth(){
        return resolution.getSize().width;
    }

    public int getCamHeight(){
        return resolution.getSize().height;
    }

    public static Mat BufferedImage2Mat(BufferedImage image) {
        image = convertTo3ByteBGRType(image);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    public static BufferedImage Mat2BufferedImage(Mat matrix) {
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        BufferedImage read = null;
        try {
            read = ImageIO.read(new ByteArrayInputStream(mob.toArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read;
    }

    private static BufferedImage convertTo3ByteBGRType(BufferedImage image) {
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(image, 0, 0, null);
        return convertedImage;
    }
}
