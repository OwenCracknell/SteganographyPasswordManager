import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLibrary{
    private StenographyImage[] library;
    private String filePath; 
    public int numOfImages;
    private String sortType;

    //constructors

    public ImageLibrary (String filePath){
        this.filePath = filePath;
        library = new StenographyImage[0];
        String[] files = pngsInFolder(filePath);
        loadImages(files);

    }

    public void loadImages (String[] files){
        for(int index = 0; index < files.length; index++){
            try {
                BufferedImage newImage = ImageIO.read(new File(filePath+files[index]));
                StenographyImage newStegoImage = new StenographyImage(newImage, files[index].replaceFirst(".png",""), "png");
                library = (StenographyImage[]) addToImageArray(library, newStegoImage);
            } catch (IOException e) {
                System.err.println("error while loading images");
            }
        }
    }

    private static String[] pngsInFolder (String filePath){
        File fileFolder = new File(filePath);

        String[] allFilesInFolder = new String[0];
        allFilesInFolder = fileFolder.list();

        String[] pngsInFolder = new String[0];
        for(int index = 0; index < allFilesInFolder.length; index++){
            if(allFilesInFolder[index].toLowerCase().endsWith(".png")){
                pngsInFolder = addToStringArray(pngsInFolder, allFilesInFolder[index]);
            }
        }
        return pngsInFolder;
    }

    private static String[] addToStringArray (String[] array, String element){
        String[] newArray = new String[array.length+1];
        for (int index = 0; index < array.length; index++){
            newArray[index] = array[index];
        }
        newArray[array.length] = element;
        return newArray;
    }

    public static BufferedImage[] addToImageArray (BufferedImage[] array, BufferedImage element){
        BufferedImage[] newArray = new BufferedImage[array.length+1];
        for (int index = 0; index < array.length; index++){
            newArray[index] = array[index];
        }
        newArray[array.length] = element;
        return newArray;
    }

    public String toString() {
        String text = "";
        for(int index = 0; index < library.length; index++){
            text = text + library[index].getName() + "\t";
        }
        return text;
    }
}
