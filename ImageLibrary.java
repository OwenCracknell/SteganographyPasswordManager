import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLibrary{
    public SteganographyImage[] library;
    private String[] files;
    private String filePath; 
    public int numOfImages;

    //constructors

    public ImageLibrary (String filePath){
        this.filePath = filePath;
        library = new SteganographyImage[0]; // init SteganographyImage array, adding to it in loadImages()
        files = imgsInFolder(filePath); // get imgs in filePath
        numOfImages = library.length;
        loadImages(files);
    }

    public void loadImages (String[] files){
        // creates SteganographyImage objs from files in String[] files array and loads them into library array 
        for(int index = 0; index < files.length; index++){
            try {
                BufferedImage newImage = ImageIO.read(new File(filePath+files[index]));
                SteganographyImage newStegoImage = null;
                if(files[index].endsWith("png")){
                    newStegoImage = new SteganographyImage(newImage, files[index].toLowerCase().replaceFirst(".png",""), "png");

                }
                else if (files[index].toLowerCase().endsWith("jpg")){
                    newStegoImage = new SteganographyImage(newImage, files[index].toLowerCase().replaceFirst(".jpg",""), "jpg");
                }

                if(newStegoImage != null && newStegoImage.getName() != null && newStegoImage.getFileType() != null){
                    library = (SteganographyImage[]) addToImageArray(library, newStegoImage);
                    numOfImages++;
                }
                else{
                    System.out.println(newStegoImage);
                    System.out.println(newStegoImage.getFileType());
                    System.out.println(newStegoImage.getName());
                }
            } catch (IOException e) {
                System.err.println("error while loading images");
            }
        }
    }

    public void sort(String sortBy){
        if(numOfImages >0){
            switch (sortBy.toLowerCase()) {
                case "name":
                    library = nameSort(library, 0);            
                    break;
                case "filetype":
                    library = fileTypeSort(library, 0);
                    break;
                default:
                    System.out.println("not supported sort");
                    break;
            }
        }
    }

    public int findFirstIndexOfFileType(String fileType){
        sort("fileType");

        String[] fileTypeArray = new String[numOfImages];

        for(int index = 0; index < numOfImages; index++){
            fileTypeArray[index] = library[index].getFileType();
        }
        int confirmedIndex = binarySearch(fileTypeArray, fileType, 0, numOfImages);
        return findFirstInStringArrayBlock(fileTypeArray, confirmedIndex);

    }

    private static int findFirstInStringArrayBlock(String[] array, int confirmedIndex){
        if(confirmedIndex ==0){
            return confirmedIndex;
        }
        else if (!array[confirmedIndex].toLowerCase().contains(array[confirmedIndex-1].toLowerCase())){
            return confirmedIndex;
        }
        else{
            return findFirstInStringArrayBlock(array, confirmedIndex-1);
        }
    }


    public int findLastIndexOfFileType(String fileType){
        sort("fileType");

        String[] fileTypeArray = new String[numOfImages];

        for(int index = 0; index < numOfImages; index++){
            fileTypeArray[index] = library[index].getFileType();
        }
        int confirmedIndex = binarySearch(fileTypeArray, fileType, 0, numOfImages);
        return findLastInStringArrayBlock(fileTypeArray, confirmedIndex);
    }

    public String getFilePath(){
        return filePath;
    }

    private static int findLastInStringArrayBlock(String[] array, int confirmedIndex){
        if(confirmedIndex == array.length-1){
            return confirmedIndex;
        }
        else if (!array[confirmedIndex].toLowerCase().contains(array[confirmedIndex+1].toLowerCase())){
            return confirmedIndex;
        }
        else{
            return findLastInStringArrayBlock(array, confirmedIndex+1);
        }
    }

    public int search(String searchTerm){
        String[] names = new String[numOfImages];
        for(int index = 0; index < numOfImages; index++){
            names[index] = library[index].getName();
        }
        return binarySearch(names, searchTerm,0,files.length-1);
    }


    private static String[] imgsInFolder (String filePath){
        //returns all files ending in .png within filePath parameter as a String array
        File fileFolder = new File(filePath);

        String[] allFilesInFolder = new String[0];
        allFilesInFolder = fileFolder.list();

        String[] imgsInFolder = new String[0];
        for(int index = 0; index < allFilesInFolder.length; index++){
            if(allFilesInFolder[index].toLowerCase().endsWith(".png")||allFilesInFolder[index].toLowerCase().endsWith(".jpg")){
                imgsInFolder = addToStringArray(imgsInFolder, allFilesInFolder[index]);
            }
        }
        return imgsInFolder;
    }

    private static String[] addToStringArray (String[] array, String element){
        // returns new String array with same elements in array parameter with element parameter appended on end
        String[] newArray = new String[array.length+1];
        for (int index = 0; index < array.length; index++){
            newArray[index] = array[index];
        }
        newArray[array.length] = element;
        return newArray;
    }

    public static SteganographyImage[] addToImageArray (SteganographyImage[] array, SteganographyImage element){
        // returns new String array with same elements in array parameter with element parameter appended on end
        SteganographyImage[] newArray = new SteganographyImage[array.length+1];
        for (int index = 0; index < array.length; index++){
            newArray[index] = array[index];
        }
        newArray[array.length] = element;
        return newArray;
    }

    private static SteganographyImage[] nameSort(SteganographyImage[] imageArray, int sortedIndex) {
        // sorts imageArray param using the insertionSort algorithm applied onto the objs name var
        //returns sorted SteganographyImage array, from lowest letter to highest (a---->z)
        // sortedIndex should be 0 on first pass as we assume that index at 0 is sorted already
        if(sortedIndex == imageArray.length-1){
            return imageArray;
        }
        else {
            String unsortedElement = imageArray[sortedIndex+1].getName();
            for(int index = sortedIndex; index >= 0; index--){
                if(unsortedElement.compareTo(imageArray[index].getName()) < 0){
                    SteganographyImage temp = imageArray[index];
                    imageArray[index] = imageArray[index+1];
                    imageArray[index+1] = temp;
                }
            }
            return nameSort(imageArray, sortedIndex+1);
        }
    }

    private static SteganographyImage[] fileTypeSort(SteganographyImage[] imageArray, int sortedIndex){
        // sorts imageArray param using the insertionSort algorithm applied onto the objs fileType var
        //returns sorted SteganographyImage array, from lowest letter to highest (a---->z)
        // sortedIndex should be 0 on first pass as we assume that index at 0 is sorted already
        if(sortedIndex == imageArray.length-1){
            return imageArray;
        }
        else {
            String unsortedElement = imageArray[sortedIndex+1].getFileType();
            for(int index = sortedIndex; index >= 0; index--){
                if(unsortedElement.compareTo(imageArray[index].getFileType()) < 0){
                    SteganographyImage temp = imageArray[index];
                    imageArray[index] = imageArray[index+1];
                    imageArray[index+1] = temp;
                }
            }
            return fileTypeSort(imageArray, sortedIndex+1);
        }
    }

    private static int binarySearch (String[] strArr, String searchTerm, int firstIndex, int lastIndex){
        // returns index of searchTerm param in strArr param using the binarySearch algorithm
        // strArr param must be already sorted
        // also takes in firstIndex and lastIndex as a param in order to recursively split array
        int middleIndex = (firstIndex+lastIndex)/2;
        if(lastIndex >= firstIndex){
            if(searchTerm.compareTo(strArr[middleIndex]) == 0 || strArr[middleIndex].contains(searchTerm)){
                return middleIndex;
            }
            else if(searchTerm.compareTo(strArr[middleIndex]) < 0){
                return binarySearch(strArr, searchTerm, firstIndex, middleIndex-1);
            }
            else if(searchTerm.compareTo(strArr[middleIndex])> 0){
                return binarySearch(strArr, searchTerm, middleIndex+1, lastIndex);
            }
        }
        return -1;

    }

    public String toString() {
        String text = "";
        for(int index = 0; index < library.length; index++){
            text = text + library[index].getName() + "\t";
        }
        return text;
    }
}
