import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SteganographyImage extends java.awt.image.BufferedImage{
    //Stenography functions only work for lossless files with support for transparency (.pngs)

    private Pixel[][] pixelArray;
    private String name;
    private String fileType;

    //constructor
    public SteganographyImage (BufferedImage image, String name, String fileType){
        //use ImageIO.read(new File(filePath)); as it returns a buffered image and is the easiest way to load in image from filepath
        //String name and String fileType parameters only used for sorting within ImageLibrary
        
        super(image.getWidth(), image.getHeight(), image.getType());
        setName(name);
        setFileType(fileType);

        // draws image into BufferedImage graphics object
        Graphics2D graphics = this.createGraphics(); //creates graphics object within current object and returns pointer
        graphics.drawImage(image, 0, 0, null); // draws image from image parameter onto graphics object, no observer as image is already loaded

        fillPixels();
        
    }
    //stenography methods
    private void fillPixels(){        
        //fills pixelArray 2d Pixel array with Pixel objects
        pixelArray = new Pixel[getHeight()][this.getWidth()];
        for(int y = 0; y < getHeight(); y++){
            for(int x = 0; x < this.getWidth(); x++){
                pixelArray[y][x] = new Pixel(getRGB(x, y));
            }
        }
    }
    public void printPixels(int numOfPixels){
        //prints the first number of pixels specifed by int numOfPixels parameter 
        int pixelsPrinted = 0;
        for(int y = 0; y < getHeight(); y++){
            for(int x = 0; x < getWidth(); x++){
                if(pixelsPrinted < numOfPixels){
                    System.out.println(pixelArray[y][x]);
                    pixelsPrinted++;
                }
            }
        }
    }
    public void stringToBlueLSB(String text) {
        //edits pixels blue channels within pixelArray to store unicode bits designated by String text parameter
        //stores as the last bit of the pixels blue channel as it has the least visual effect on the final image
        //writes 16 0s (null char) to mark text end


        // Convert text to Unicode bit array
        int[] bitArray = StringToUnicodeIntArray(text);

        int totalBitsToWrite = bitArray.length + 16; // 16 bits for null character
        int bitIndex = 0;

        for (int y = 0; y < pixelArray.length; y++) {
            for (int x = 0; x < pixelArray[y].length; x++) {
                if (bitIndex < bitArray.length) {
                    // Write message bits
                    pixelArray[y][x].modifyLastBlueBit(bitArray[bitIndex]);
                } 
                else if (bitIndex < totalBitsToWrite) {
                    // Write null terminator bits (16 zeros)
                    pixelArray[y][x].modifyLastBlueBit(0);
                }

                setRGB(x, y, pixelArray[y][x].getRGB());
                bitIndex++;
            }
        }
    }

    public void saveImage(String filePath){
        File newImgFile = new File(filePath+name+"."+fileType);
        try {
            ImageIO.write(this, fileType, newImgFile);
        } 
        catch (IOException e) {
            System.err.println("error occured when saving image"); //the same as out.println but prints in red in terminal
        }
    }

    public String getTextFromBlue() {
        // returns String with text from last bits in blue channel
        String text = "";

        int currentChar = 0; // int to hold binary of char we are working with
        boolean nulCharReached = false; // becomes true when a whole 2 bytes of 0s are reached
        int bitsRead = 0;
        // x and y axis of image
        int x = 0;
        int y = 0;

        while (!nulCharReached && y < this.getHeight()) { //pixels left to read and null char not reached
            // wrap back to first pixel in next row when last pixel in col is reached
            if (x >= this.getWidth()) {
                x = 0;
                y++;
            }
            
            // add last bit of pixel to current char
            currentChar = (currentChar << 1) | (pixelArray[y][x].getlastBlueBit() & 1);
            bitsRead++;

            if (bitsRead == 16) { // enough data to build char
                if (currentChar == 0) { // nul char
                    nulCharReached = true;
                    break;
                } 
                else {
                    text += (char) currentChar;
                    currentChar = 0;
                    bitsRead = 0;
                }
            }

            x++;
        }

        return text;
    }

    private static int[] StringToUnicodeIntArray (String text){
        //returns unicode int array of String text parameter converted to bit array
        int[] unicodeArray = new int[text.length()*16]; // array to hold binary of unicode chars
        
        for(int charIndex = 0; charIndex < text.length(); charIndex++){
            int charUnicode = (int) text.charAt(charIndex); // get char unicode value
            for(int bitIndex = 0; bitIndex < (16); bitIndex++){
                int bitMask = 1 << (15 - bitIndex); //isolates the left most bit not read
                int arrayIndex = charIndex*16 + bitIndex;
                if ((charUnicode & bitMask) > 0){ // bit is 1
                    unicodeArray[arrayIndex] = 1;
                }
                else{ // bit is 0
                    unicodeArray[arrayIndex] = 0; 
                }

            }
        }
        return unicodeArray;
    }

    //getters
    public String getName() {
        return name;
    }
    public String getFileType() {
        return fileType;
    }

    //setters
    public void setName(String name){
        if(name.isBlank()){
            System.err.println("Cannot create an Image with no name");
        }
        else{
            this.name = name;
        }
    }

    public void setFileType(String fileType){
        if(fileType.isBlank() || !fileType.contains("png")){
            System.err.println("cannot create an image with no file type or unsupported file type");
            this.fileType = fileType;
        }
        else{
            this.fileType = fileType;
        }
    }
    
}
