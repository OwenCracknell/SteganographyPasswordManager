import java.io.*;
import java.util.Scanner;

public class Main {
    // Name: Owen Cracknell 
    // Date: June 11, 2025
    // Purpose: reads config file and sets up gui for SteganographyPasswordManager
    
    public static String confFilePath = "SteganoPassMan.conf"; // looks for config file where ever Main.java is run
    public static GUI gui;
    
    public static void writeToConfigFile(String[] lines) throws IOException{
        //writes lines in String array lines to config file
        BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath));
        for(int index = 0; index < lines.length; index++){
            writer.write(lines[index]);
            writer.newLine();
        }
        writer.close(); // save image
    }

    public static String[] readConfigFile() throws IOException{
        //returns String array with content in config file
        //sort of recursive
        try {
            BufferedReader reader = new BufferedReader(new FileReader(confFilePath));
            String[] fileContent = new String[4];
            for(int index = 0; index < fileContent.length; index++){
                fileContent[index] = reader.readLine();
            }
            reader.close();
            return fileContent;
        } 
        catch (IOException e) { //config file does not exist
            createDefaultConfigFile();
            return readConfigFile();
        }
    }
    public static void createDefaultConfigFile() throws IOException{
        //create deafult config file, also asks user where they would like password manager to save passwords
        String[] options = new String[4];
        options[0] = "name"; //sort type
        options[1] = "all"; // show Filetype
        options[2] = "light"; //apperence
        
        Scanner input = new Scanner(System.in);
        System.out.println("where would you passwords to be saved");
        options[3] = input.next();
        input.close();
        writeToConfigFile(options);
    }

    public static void loadOptions (String[] options){
        //applys options in options String array parameter to gui
        
        //sort type
        if(options[0].equals("name")){
            gui.lib.sort("name");
        }
        else if (options[0].equals("file type")){
            gui.lib.sort("fileType");
        }

        //show file type
        if(options[1].equals("all")){
            gui.imgShowStartIndex = 0;
            gui.imgShowEndIndex = gui.lib.numOfImages-1;
        }
        else if (options[1].equals("png")){
            gui.imgShowStartIndex = gui.lib.findFirstIndexOfFileType("png");
            gui.imgShowEndIndex = gui.lib.findLastIndexOfFileType("png");
        }

        //apperence mode
        if (options[2].equals("dark")){
            gui.changeToDarkMode();
        }
        else if (options[2].equals("light")){
            gui.changeToLightMode();
        }
        gui.displayLibraryImages(); //applys apperence changes
    }

    public static void main (String[] args) throws IOException{
        String[] options = readConfigFile();
        gui = new GUI (options[3]);
        loadOptions(options);
    }
}
