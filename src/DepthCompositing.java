// Part of the course materials when I TAed the computational media lab

/**********************************************************/

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

class DepthCompositing extends Frame {
    BufferedImage bubblesImg;
    BufferedImage forestImg;
    BufferedImage bubbles_depthImg;
    BufferedImage forest_depthImg;

    BufferedImage carImg;
    BufferedImage car_depthImg;

    BufferedImage compositeImage;
    BufferedImage fogImage;

    int width_bubbles, width_car; // width of the image
    int height_bubbles, height_car; // height of the image

    public DepthCompositing() {
        // constructor
        // Get an image from the specified file in the current directory on the
        // local hard disk.
        try {
            bubblesImg = ImageIO.read(new File("bubbles.jpg"));
            forestImg = ImageIO.read(new File("forest.jpg"));
            bubbles_depthImg = ImageIO.read(new File("bubbles_depth.jpg"));
            forest_depthImg = ImageIO.read(new File("forest_depth.jpg"));

            carImg = ImageIO.read(new File("car.jpg"));
            car_depthImg = ImageIO.read(new File("car_depth.jpg"));

        } catch (Exception e) {
            System.out.println("Cannot load the provided image");
        }
        this.setTitle("Image Processing in Java - Practice");
        this.setVisible(true);

        width_bubbles = bubblesImg.getWidth();
        height_bubbles = bubblesImg.getHeight();

        width_car = carImg.getWidth();
        height_car = carImg.getHeight();

        compositeImage = composite(bubblesImg, forestImg, bubbles_depthImg, forest_depthImg);
        fogImage = addFog(carImg, car_depthImg, 100);

        //keymixImage = keymixImages(birdImage, boardImage, matteImage);
        //premultipliedImage = combineImages(birdImage, matteImage, Operations.multiply);

        //Anonymous inner-class listener to terminate program
        this.addWindowListener(
                new WindowAdapter() {//anonymous class definition
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);//terminate the program
                    }//end windowClosing()
                }//end WindowAdapter
        );//end addWindowListener
    }// end constructor

    public BufferedImage composite(BufferedImage src1, BufferedImage src2,
                                   BufferedImage src1_depth, BufferedImage src2_depth) {

        BufferedImage result = new BufferedImage(src1.getWidth(),
                src1.getHeight(), src1.getType());

        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {
                int rgb1 = src1.getRGB(i, j);
                int rgb2 = src2.getRGB(i, j);
                int depth1 = src1_depth.getRGB(i, j);
                int depth2 = src2_depth.getRGB(i, j);

                // Using RGB to compare the brightness
                int grey1 = (getRed(depth1) + getGreen(depth1) + getBlue(depth1)) / 3;
                int grey2 = (getRed(depth2) + getGreen(depth2) + getBlue(depth2)) / 3;
                int resultRGB = (grey1 >= grey2) ? rgb1 : rgb2;

                // Alternative: Converting to HSB to check for the brightness
                // float[] hsb1 = new float[3];
                // float[] hsb2 = new float[3];
                // Color.RGBtoHSB(getRed(depth1), getGreen(depth1),
                // getBlue(depth1), hsb1);
                // Color.RGBtoHSB(getRed(depth2), getGreen(depth2),
                // getBlue(depth2), hsb2);
                // int resultRGB = (hsb1[2] >= hsb2[2]) ? rgb1 : rgb2;

                result.setRGB(i, j, resultRGB);

            }
        return result;
    }

    public BufferedImage addFog(BufferedImage src, BufferedImage depth, int fogIntensity) {
        BufferedImage result = new BufferedImage(src.getWidth(),
                src.getHeight(), src.getType());

        for (int i = 0; i < result.getWidth(); i++)
            for (int j = 0; j < result.getHeight(); j++) {
                int rgb = src.getRGB(i, j);
                int depthRGB = depth.getRGB(i, j);
                int depthBrightness = (getRed(depthRGB) + getGreen(depthRGB) + getBlue(depthRGB)) / 3;
                float normalized = depthBrightness / 255.0f;

                int newR = (int) (getRed(rgb) * normalized + fogIntensity * (1 - normalized));
                int newG = (int) (getGreen(rgb) * normalized + fogIntensity * (1 - normalized));
                int newB = (int) (getBlue(rgb) * normalized + fogIntensity * (1 - normalized));

                result.setRGB(i, j, new Color(newR, newG, newB).getRGB());
            }
        return result;
    }

    protected int getRed(int pixel) {
        return (pixel >>> 16) & 0xFF;
    }

    protected int getGreen(int pixel) {
        return (pixel >>> 8) & 0xFF;
    }

    protected int getBlue(int pixel) {
        return pixel & 0xFF;
    }

    public void paint(Graphics g) {

        //if working with different images, this may need to be adjusted
        int w = width_bubbles / 3;
        int h = height_bubbles / 3;

        this.setSize(w * 5 + 150, h * 4);

        g.drawImage(bubblesImg, 10, 50, w, h, this);
        g.drawImage(bubbles_depthImg, 10 + w + 25, 50, w, h, this);
        g.drawImage(forestImg, 10 + w * 2 + 50, 50, w, h, this);
        g.drawImage(forest_depthImg, 10 + w * 3 + 75, 50, w, h, this);
        g.drawImage(compositeImage, 10 + w * 4 + 125, 50, w, h, this);

        g.drawImage(carImg, 25, 50 + h + 55, width_car / 2, height_car / 2, this);
        g.drawImage(car_depthImg, 25 + width_car / 2 + 25, 50 + h + 55, width_car / 2, height_car / 2, this);
        g.drawImage(fogImage, 25 + width_car + 50, 50 + h + 55, width_car / 2, height_car / 2, this);

        g.setColor(Color.BLACK);
        Font f1 = new Font("Verdana", Font.PLAIN, 13);
        g.setFont(f1);
        g.drawString("Bubbles", 25, 45);
        g.drawString("Bubbles Depth", 25 + w + 45, 45);
        g.drawString("Forest", 25 + w * 2 + 85, 45);
        g.drawString("Forest Depth", 25 + w * 3 + 125, 45);
        g.drawString("3D Composite", 25 + w * 4 + 165, 45);

        g.drawString("Car", 30, 50 + h + 50);
        g.drawString("Car Depth Image", 30 + width_car / 2 + 25, 50 + h + 50);
        g.drawString("Car Depth Image", 30 + width_car + 50, 50 + h + 50);
    }
// =======================================================//

    public static void main(String[] args) {
        // instantiate this object
        DepthCompositing img = new DepthCompositing();
        img.repaint();// render the image

    }// end main
}
