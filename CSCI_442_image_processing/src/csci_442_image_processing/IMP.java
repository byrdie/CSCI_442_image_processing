package csci_442_image_processing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ColorModel;
import java.io.File;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;

class IMP implements MouseListener {

    JFrame frame;
    JPanel mp;
    JButton start;
    JScrollPane scroll;
    JMenuItem openItem, exitItem, resetItem;
    Toolkit toolkit;
    File pic;
    ImageIcon img;
    int colorX, colorY;
    int[] pixels;
    byte[] bytePixels;
    int[] results;

    byte[][] mask = {
        {-1, -1, -1},
        {-1, 8, -1},
        {-1, -1, -1}
    };

    byte[][] betterMask = {
        {-1, -1, -1, -1, -1},
        {-1, 0, 0, 0, -1},
        {-1, 0, 16, 0, -1},
        {-1, 0, 0, 0, -1},
        {-1, -1, -1, -1, -1},};
    byte[][] laplace = {
        {-1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1},
        {-1, -1, 24, -1, -1},
        {-1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1},};
   //Instance Fields you will be using below

    //This will be your height and width of your 2d array
    int height = 0, width = 0;

    //your 2D array of pixels
    int picture[][];
    byte grayPic[][];

    /* 
     * In the Constructor I set up the GUI, the frame the menus. The open pulldown 
     * menu is how you will open an image to manipulate. 
     */
    IMP() {
        toolkit = Toolkit.getDefaultToolkit();
        frame = new JFrame("Image Processing Software by Hunter");
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu functions = getFunctions();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                quit();
            }
        });
        openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                handleOpen();
            }
        });
        resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                reset();
            }
        });
        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quit();
            }
        });
        file.add(openItem);
        file.add(resetItem);
        file.add(exitItem);
        bar.add(file);
        bar.add(functions);
        frame.setSize(600, 600);
        mp = new JPanel();
        mp.setBackground(new Color(0, 0, 0));
        scroll = new JScrollPane(mp);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        JPanel butPanel = new JPanel();
        butPanel.setBackground(Color.black);
        start = new JButton("start");
        start.setEnabled(false);
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fun2();
            }
        });
        butPanel.add(start);
        frame.getContentPane().add(butPanel, BorderLayout.SOUTH);
        frame.setJMenuBar(bar);
        frame.setVisible(true);
    }

    /* 
     * This method creates the pulldown menu and sets up listeners to selection of the menu choices. If the listeners are activated they call the methods 
     * for handling the choice, fun1, fun2, fun3, fun4, etc. etc. 
     */
    private JMenu getFunctions() {
        JMenu fun = new JMenu("Functions");
        JMenuItem firstItem = new JMenuItem("MyExample - fun1 method");
        firstItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fun1();
            }
        });

        fun.add(firstItem);

        JMenuItem secondItem = new JMenuItem("PopUp");
        secondItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                fun2();
            }
        });

        fun.add(secondItem);

        JMenuItem thirdItem = new JMenuItem("RGB to Grayscale");
        thirdItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rgbToGrayLuminosity();
            }
        });

        fun.add(thirdItem);

        JMenuItem fourthItem = new JMenuItem("Edge Detection");
        fourthItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                edgeDetection();
            }
        });

        fun.add(fourthItem);

        JMenuItem fifthItem = new JMenuItem("Better Edge Detection");
        fifthItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                betterEdgeDetection();
            }
        });

        fun.add(fifthItem);
        
        JMenuItem sixthItem = new JMenuItem("Orange Color Tracking");
        sixthItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                orangeColorTracking();
            }
        });

        fun.add(sixthItem);

        return fun;

    }

    /*
     * This method handles opening an image file, breaking down the picture to a one-dimensional array and then drawing the image on the frame. 
     * You don't need to worry about this method. 
     */
    private void handleOpen() {
        img = new ImageIcon();
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            pic = chooser.getSelectedFile();
            img = new ImageIcon(pic.getPath());
        }
        width = img.getIconWidth();
        height = img.getIconHeight();

        JLabel label = new JLabel(img);
        label.addMouseListener(this);
        pixels = new int[width * height];

        results = new int[width * height];

        Image image = img.getImage();

        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("Interrupted waiting for pixels");
            return;
        }
        for (int i = 0; i < width * height; i++) {
            results[i] = pixels[i];
        }
        turnTwoDimensional();
        mp.removeAll();
        mp.add(label);

        mp.revalidate();
    }

    /*
     * The libraries in Java give a one dimensional array of RGB values for an image, I thought a 2-Dimensional array would be more usefull to you
     * So this method changes the one dimensional array to a two-dimensional. 
     */
    private void turnTwoDimensional() {
        picture = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                picture[i][j] = pixels[i * width + j];
            }
        }

    }
    /*
     *  This method takes the picture back to the original picture
     */

    private void reset() {
        for (int i = 0; i < width * height; i++) {
            pixels[i] = results[i];
            picture[i/width][i%width] = results[i];
        }
        Image img2 = toolkit.createImage(new MemoryImageSource(width, height, pixels, 0, width));

        JLabel label2 = new JLabel(new ImageIcon(img2));
        mp.removeAll();
        mp.add(label2);

        mp.revalidate();
    }
    /*
     * This method is called to redraw the screen with the new image. 
     */

    private void resetPicture() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i * width + j] = picture[i][j];
            }
        }
        Image img2 = toolkit.createImage(new MemoryImageSource(width, height, pixels, 0, width));

        JLabel label2 = new JLabel(new ImageIcon(img2));
        mp.removeAll();
        mp.add(label2);

        mp.revalidate();

    }

//    private void resetBytePicture() {
//        bytePixels = new byte[height * width];
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                bytePixels[i * width + j] = grayPic[i][j];
//            }
//        }
//        ColorModel cm = ColorModel.getRGBdefault();
//        Image img2 = toolkit.createImage(new MemoryImageSource(width, height, cm, bytePixels, 0, width));
//
//        JLabel label2 = new JLabel(new ImageIcon(img2));
//        mp.removeAll();
//        mp.add(label2);
//
//        mp.revalidate();
//
//    }
    /*
     * This method takes a single integer value and breaks it down doing bit manipulation to 4 individual int values for A, R, G, and B values
     */
    private int[] getPixelArray(int pixel) {
        int temp[] = new int[4];
        temp[0] = (pixel >> 24) & 0xff;
        temp[1] = (pixel >> 16) & 0xff;
        temp[2] = (pixel >> 8) & 0xff;
        temp[3] = (pixel) & 0xff;
        return temp;

    }
    /*
     * This method takes an array of size 4 and combines the first 8 bits of each to create one integer. 
     */

    private int getPixels(int rgb[]) {
        int alpha = 0;
        int rgba = (rgb[0] << 24) | (rgb[1] << 16) | (rgb[2] << 8) | rgb[3];
        return rgba;
    }

    public void getValue() {
        int pix = picture[colorY][colorX];
        int temp[] = getPixelArray(pix);
        System.out.println("Color value " + temp[0] + " " + temp[1] + " " + temp[2] + " " + temp[3]);
    }

    /**
     * ************************************************************************************************
     * This is where you will put your methods. Every method below is called
     * when the corresponding pulldown menu is used. As long as you have a
     * picture open first the when your fun1, fun2, fun....etc method is called
     * you will have a 2D array called picture that is holding each pixel from
     * your picture.
     * ***********************************************************************************************
     */
    /*
     * Example function that just removes all red values from the picture. 
     * Each pixel value in picture[i][j] holds an integer value. You need to send that pixel to getPixelArray the method which will return a 4 element array 
     * that holds A,R,G,B values. Ignore [0], that's the Alpha channel which is transparency, we won't be using that, but you can on your own.
     * getPixelArray will breaks down your single int to 4 ints so you can manipulate the values for each level of R, G, B. 
     * After you make changes and do your calculations to your pixel values the getPixels method will put the 4 values in your ARGB array back into a single
     * integer value so you can give it back to the program and display the new picture. 
     */
    private void fun1() {

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgbArray[] = new int[4];

                rgbArray = getPixelArray(picture[i][j]);
                //picture[i][j] = changePixels(picture[i][j]);

                rgbArray[1] = 0;
                picture[i][j] = getPixels(rgbArray);
            }
        }
        resetPicture();
    }

    private void rgbToGrayLuminosity() {
        grayPic = new byte[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgbArray[] = new int[4];

                rgbArray = getPixelArray(picture[i][j]);

                //apply luminosity equation and move into byte array
                byte grayPix = (byte) (0.21 * rgbArray[1] + 0.72 * rgbArray[2] + 0.07 * rgbArray[3]);
                grayPic[i][j] = grayPix;
//                int[] newPixel = {grayPix, grayPix, grayPix,grayPix};
//                picture[i][j] = getPixels(newPixel);
                picture[i][j] = (grayPix * 0x00010101) | 0xFF000000;
            }

        }
        resetPicture();
    }

    private void edgeDetection() {
        rgbToGrayLuminosity();
        int sum = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sum = 0;
                if (i == 0 || j == 0 || j == width - 1 || i == height - 1) {
                    sum = 0;
                } else {
                    int x = 0;
                    for (int a = i - 1; a < i + 2; a++) {
                        int y = 0;
                        for (int b = j - 1; b < j + 2; b++) {
                            sum = sum + grayPic[a][b] * mask[x][y];
                            y++;
                        }
                        x++;
                    }
                }

                if (sum > 255) {
                    sum = 255;
                }
                if (sum < 0) {
                    sum = 0;
                }
                byte bSum = (byte) sum;

                picture[i][j] = (int) (((byte) (255 - bSum)) * 0x00010101) | 0xFF000000;
            }
        }
        resetPicture();
    }

    private void betterEdgeDetection() {
        rgbToGrayLuminosity();

        int sum = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sum = 0;
                if (i == 0 || j == 0 || j == width - 1 || i == height - 1 || i == 1 || i == height - 2 || j == 1 || j == width - 2) {
                    sum=0;
                } else {
                    int x = 0;
                    for (int a = i - 2; a < i + 3; a++) {
                        int y = 0;
                        for (int b = j - 2; b < j + 3; b++) {
                            sum = sum + grayPic[a][b] * laplace[x][y];
                            y++;
                        }
                        x++;
                    }
                }

                if (sum > 255) {
                    sum = 255;
                }
                if (sum < 0) {
                    sum = 0;
                }
                byte bSum = (byte) sum;

                picture[i][j] = (int) (((byte) (255 - bSum)) * 0x00010101) | 0xFF000000;
            }
        }

        resetPicture();
    }
    
    private void orangeColorTracking(){
        
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int[] rgbArray = getPixelArray(picture[i][j]);
                if(rgbArray[1] > 200 && rgbArray[2] > 80 && rgbArray[2] < 150 && rgbArray[3] < 70){
                    picture[i][j] = (int) 0xFF000000;
                } else {
                    picture[i][j] = (int) 0xFFFFFFFF;
                }
//                System.out.println(picture[i][j]);
            }
        }
                resetPicture();
    }

    /*
     * fun2
     */
    private void fun2() {
        JFrame pop = new JFrame("Red");
        pop.setSize(300, 300);
        pop.setBackground(Color.white);
        MyPanel panel = new MyPanel();
        pop.getContentPane().add(panel, BorderLayout.CENTER);
        pop.setVisible(true);

    }

    /*
     * fun3
     */
    private void fun3() {
    }

    /*
     * fun4
     */
    private void fun4() {
    }

    /*
     * fun5
     */
    private void fun5() {
    }

    private void quit() {
        System.exit(0);
    }

    public void mouseEntered(MouseEvent m) {
    }

    public void mouseExited(MouseEvent m) {
    }

    public void mouseClicked(MouseEvent m) {
        colorX = m.getX();
        colorY = m.getY();
        System.out.println(colorX + "  " + colorY);
        getValue();
        start.setEnabled(true);
    }

    public void mousePressed(MouseEvent m) {
    }

    public void mouseReleased(MouseEvent m) {
    }

    public static void main(String[] args) {
        IMP imp = new IMP();
    }

}
