import java.io.*;

import javax.swing.JOptionPane;

public class Main {
    public static String confFilePath = "/home/owen/javagr12/FEU/StenoPassMan.conf";
    public static GUI gui;
    
    public static void writeToConfigFile(String[] lines) throws IOException{
        BufferedWriter writer = new BufferedWriter(new FileWriter(confFilePath));
        for(int index = 0; index < lines.length; index++){
            writer.write(lines[index]);
            writer.newLine();
        }
        writer.close();
    }

    private static String[] readConfigFile() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(confFilePath));
        
        String[] fileContent = new String[4];

        for(int index = 0; index < fileContent.length; index++){
            fileContent[index] = reader.readLine();
        }
        
        reader.close();
        return fileContent;
    }

    private static void loadOptions (String[] options){

        if(options[0].equals("name")){
            gui.lib.sort("name");
        }
        else if (options[0].equals("file type")){
            gui.lib.sort("fileType");
        }

        if(options[1].equals("all")){
            gui.imgShowStartIndex = 0;
            gui.imgShowEndIndex = gui.lib.numOfImages-1;
        }
        else if (options[1].equals("png")){
            gui.imgShowStartIndex = gui.lib.findFirstIndexOfFileType("png");
            gui.imgShowEndIndex = gui.lib.findLastIndexOfFileType("png");
        }

        if (options[2].equals("dark")){
            gui.changeToDarkMode();
        }
        else if (options[2].equals("light")){
            gui.changeToLightMode();
        }
        gui.displayLibraryImages();
    }

    public static void main (String[] args) throws IOException{
        String[] options = readConfigFile();
        gui = new GUI (options[3]);
        loadOptions(options);
    }
}
