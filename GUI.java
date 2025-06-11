import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI implements ActionListener{
    // Name: Owen Cracknell 
    // Date: June 11, 2025
    // Purpose: provide a graphical user interface for SteganographyPasswordManager

    //img library vars
    public ImageLibrary lib;
    public int imgShowStartIndex;
    public int imgShowEndIndex;

    //GUI components
    public JFrame frame;
    public JLabel searchLabel;
    public JTextField searchBar;
    public JLabel sortLabel;
    public JComboBox<String> sortTypeDropDown;
    public String[] sortTypes = {"name", "file type"};
    public JLabel fileTypeLabel;
    public JComboBox<String> fileTypeDropDown;
    public String[] supportedFileTypes = {"all","png"};
    public JLabel darkModeLabel;
    public JCheckBox darkMode;
    public JButton optionsButton;
    public JPanel optionsPanel;
    public JPanel topPanel;
    public JPanel midPanel;
    public JPanel bottomPanel;
    public JButton addImageButton;

    public GUI(String filePath){
        //library set up
        lib = new ImageLibrary(filePath);
        imgShowEndIndex = lib.numOfImages-1;
        imgShowStartIndex = 0;

        //frame setup
        frame = new JFrame("Password Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700,700);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        //top pannel components
        searchLabel = new JLabel("search:");
        searchBar = new JTextField(12);
        optionsButton = new JButton("Options");
        optionsButton.addActionListener(this);
        searchBar.addActionListener(this);
        //top panel init
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        //add components to top panel
        topPanel.add(searchLabel, BorderLayout.BEFORE_FIRST_LINE);
        topPanel.add(searchBar,BorderLayout.LINE_START);
        topPanel.add(optionsButton, BorderLayout.EAST);
        //add top panel to frame
        frame.add(topPanel, BorderLayout.NORTH);

        //option pane setup
        optionsPanel = new JPanel(); // panel to hold optionsPane components
        sortLabel = new JLabel("Sort By:");
        sortTypeDropDown = new JComboBox<>(sortTypes);
        fileTypeLabel = new JLabel("Show FileType:");
        fileTypeDropDown = new JComboBox<>(supportedFileTypes);
        darkModeLabel = new JLabel("DarkMode:");
        darkMode = new JCheckBox();

        optionsPanel.add(sortLabel);
        optionsPanel.add(sortTypeDropDown);
        optionsPanel.add(fileTypeLabel);
        optionsPanel.add(fileTypeDropDown);
        optionsPanel.add(darkModeLabel);
        optionsPanel.add(darkMode);

        //mid panel setup
        midPanel = new JPanel();
        midPanel.setPreferredSize(new Dimension(700, 1)); // seting preferred hight to 1 as JScrollPane will handel hight
        midPanel.setLayout(new FlowLayout());
        
        JScrollPane midPane = new JScrollPane(midPanel);
        //turn off horizontal scrolling, forcing images to wrap to y-axis
        midPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        midPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(midPane, BorderLayout.CENTER); // BorderLayout.CENTER allows for midPane to take up entire centre of frame

        //bottom Panel
        bottomPanel = new JPanel();
        addImageButton = new JButton("Add Image");
        addImageButton.addActionListener(this);

        bottomPanel.add(addImageButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        displayLibraryImages();

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == optionsButton){ // option button is hit
            String[] options = new String[4]; // string array to hold options entered by user into optionsPane
            // Show the OptionPane
            int result = JOptionPane.showConfirmDialog(null, optionsPanel, "Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                options[0] = (String) sortTypeDropDown.getSelectedItem();
                options[1] = (String) fileTypeDropDown.getSelectedItem();
                boolean checked = darkMode.isSelected();

                //sortType
                if(options[0].equals("name")){
                    lib.sort("name");
                }
                else if (options[0].equals("file type")){
                    lib.sort("fileType");
                }
                //fileType to show
                if(options[1].equals("all")){
                    imgShowStartIndex = 0;
                    imgShowEndIndex = lib.numOfImages-1;
                }
                else if (options[1].equals("png")){
                    imgShowStartIndex = lib.findFirstIndexOfFileType("png");
                    imgShowEndIndex = lib.findLastIndexOfFileType("png");
                }
                //dark mode
                if (checked == true){
                    changeToDarkMode();
                    options[2] = "dark";
                }
                else if (checked == false){
                    changeToLightMode();
                    options[2] = "light";
                }

                options[3] = lib.getFilePath(); // options 3 grabes the file path from library
                
                //writing options array to the config file
                try {
                    Main.writeToConfigFile(options);
                } catch (IOException e1) {
                    System.err.println("Error: Failed to write to config file");
                }

                displayLibraryImages(); // clears and re adds images to midPanel
            }
        }
        // addImageButton was pressed
        else if (e.getSource() == addImageButton){
            //allows for user to select file
            JFileChooser fileChooser= new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG file", "png"); // limit choice to only png files
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.showOpenDialog(fileChooser);

            File selectedFile = fileChooser.getSelectedFile();

            String name = selectedFile.getName().replace(".png", ""); //gets the name of the file from the path
            SteganographyImage newImg = null;
            try {
                newImg = new SteganographyImage(ImageIO.read(selectedFile), name,"png");
            } 
            catch (IOException f) {
                System.err.println("error while loading image");
            }

            //password entry popup
            JTextField passwordField = new JTextField(12);
            JLabel passwordLabel = new JLabel("password:");
            JPanel passOptionsPanel = new JPanel();
            passOptionsPanel.add(passwordLabel);
            passOptionsPanel.add(passwordField);
            JOptionPane.showConfirmDialog(null, passOptionsPanel, "Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            //store text in image
            newImg.stringToBlueLSB(passwordField.getText());
            newImg.saveImage(lib.getFilePath());
            //add image to library
            lib = new ImageLibrary(lib.getFilePath());
            //display images
            imgShowStartIndex = 0;
            imgShowEndIndex = lib.numOfImages - 1;
            displayLibraryImages();
        }
        //enter pressed on searchBar
        else if (e.getSource() == searchBar){
            String searchTerm = searchBar.getText();
            int foundIndex = lib.search(searchTerm); //binary search but with String.contains to allow for partial entry as search term
            if(!searchTerm.isEmpty()&& foundIndex != -1){ //found image
                imgShowStartIndex = lib.search(searchTerm);
                imgShowEndIndex = imgShowStartIndex;
            }
            else{ //image not found
                imgShowStartIndex = 0;
                imgShowEndIndex = lib.numOfImages - 1;
            }
            displayLibraryImages();
        }
        frame.repaint();
        frame.revalidate();
    }

    public void displayLibraryImages() {
        //displays images in lib as buttons on midPanel
        midPanel.removeAll(); // Clear previous content
        midPanel.setLayout(new FlowLayout());

        for (int i = imgShowStartIndex; i <= imgShowEndIndex && i < lib.library.length; i++) {
            SteganographyImage img = lib.library[i];
            if (img != null) {
                // Create button with scaled image
                ImageIcon icon = new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                JButton button = new JButton(icon);
                button.setToolTipText(img.getName());
                button.setPreferredSize(new Dimension(100, 100));

                //panel for when it is click
                JPanel resultPanel = new JPanel();
                JLabel result = new JLabel(img.getTextFromBlue());
                result.setFont(new Font("Source Code Pro", Font.BOLD, 14));
                resultPanel.add(result);
                //action listener is added overrided here instead of the actionPerformed method as each button needs to show a different result
                ActionListener buttonAction = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //only display password if file is a png. this is due to other file types not having an alpha channel thus changing how data is stored within rgb values
                        if(img.getFileType() == "png"){
                            JOptionPane.showConfirmDialog(null, resultPanel, "result", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                };

                button.addActionListener(buttonAction);
                // Create label with image name
                JLabel nameLabel = new JLabel(img.getName());
                nameLabel.setPreferredSize(new Dimension(100, 20));

                // Create vertical panel to hold button + label
                JPanel container = new JPanel();
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.add(button);
                container.add(nameLabel);

                midPanel.add(container);
            }
        }
        midPanel.revalidate();
        midPanel.repaint();
    }

    public void changeToDarkMode(){
        //changes theme to dark mode
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Source Code Pro", Font.BOLD, 16));
        searchBar.setForeground(Color.WHITE);
        searchBar.setBackground(Color.DARK_GRAY);
        optionsButton.setForeground(Color.WHITE);
        optionsButton.setBackground(Color.DARK_GRAY);
        optionsButton.setFont(new Font("Source Code Pro", Font.BOLD, 16));
        topPanel.setBackground(Color.BLACK);
        midPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setBackground(Color.BLACK);
        addImageButton.setBackground(Color.DARK_GRAY);
        addImageButton.setForeground(Color.white);
        addImageButton.setFont(new Font("Source Code Pro", Font.BOLD, 16));
        frame.repaint();
        frame.revalidate();
    }
    public void changeToLightMode(){
        //changes theme to light mode
        searchLabel.setForeground(Color.BLACK);
        searchLabel.setFont(new Font("Source Code Pro", Font.BOLD, 16));
        searchBar.setForeground(Color.BLACK);
        searchBar.setBackground(Color.white);
        optionsButton.setForeground(Color.BLACK);
        optionsButton.setBackground(Color.white);
        optionsButton.setFont(new Font("Source Code Pro", Font.BOLD, 16));
        topPanel.setBackground(Color.lightGray);
        midPanel.setBackground(Color.white);
        bottomPanel.setBackground(Color.lightGray);
        addImageButton.setBackground(Color.white);
        addImageButton.setForeground(Color.black);
        addImageButton.setFont(new Font("Source Code Pro", Font.BOLD, 16));
        frame.repaint();
        frame.revalidate();
    }
}
    
