import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI implements ActionListener{
    public ImageLibrary lib;
    public int imgShowStartIndex;
    public int imgShowEndIndex;

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
        lib.sort("name");

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

        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        topPanel.add(searchLabel, BorderLayout.BEFORE_FIRST_LINE);
        topPanel.add(searchBar,BorderLayout.LINE_START);
        topPanel.add(optionsButton, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        //option pane setup

        optionsPanel = new JPanel();
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

        //mid panel
        midPanel = new JPanel();
        midPanel.setPreferredSize(new Dimension(700, 1));
        midPanel.setLayout(new FlowLayout());
        
        JScrollPane midPane = new JScrollPane(midPanel);
        midPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        midPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(midPane, BorderLayout.CENTER);
        System.out.println("Loaded " + lib.numOfImages + " images.");

        //bottom Panel

        bottomPanel = new JPanel();
        addImageButton = new JButton("Add Image");
        addImageButton.addActionListener(this);

        bottomPanel.add(addImageButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        imgShowEndIndex = lib.numOfImages-1;
        imgShowStartIndex = 0;
        displayLibraryImages();

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == optionsButton){
            String[] options = new String[4];
            // Show the OptionPane
            int result = JOptionPane.showConfirmDialog(null, optionsPanel, "Options",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // Handle the result
            if (result == JOptionPane.OK_OPTION) {
                options[0] = (String) sortTypeDropDown.getSelectedItem();
                options[1] = (String) fileTypeDropDown.getSelectedItem();
                boolean checked = darkMode.isSelected();

                if(options[0].equals("name")){
                    lib.sort("name");
                }
                else if (options[0].equals("file type")){
                    lib.sort("fileType");
                }

                if(options[1].equals("all")){
                    imgShowStartIndex = 0;
                    imgShowEndIndex = lib.numOfImages-1;
                }
                else if (options[1].equals("png")){
                    imgShowStartIndex = lib.findFirstIndexOfFileType("png");
                    imgShowEndIndex = lib.findLastIndexOfFileType("png");
                }

                if (checked == true){
                    changeToDarkMode();
                    options[2] = "dark";
                }
                else if (checked == false){
                    changeToLightMode();
                    options[2] = "light";
                }
                options[3] = lib.getFilePath();
                try {
                    Main.writeToConfigFile(options);
                } catch (IOException e1) {
                    System.err.println("Error: Failed to write to config file");
                }

                displayLibraryImages();
            }
        }
        else if (e.getSource() == addImageButton){
            JFileChooser fileChooser= new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG file", "png");
            fileChooser.setFileFilter(filter);
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.showOpenDialog(fileChooser);
            File selectedFile = fileChooser.getSelectedFile();
            String name = selectedFile.getName().replace(".png", "");
            StenographyImage newImg = null;
            try {
                newImg = new StenographyImage(ImageIO.read(selectedFile), name,"png");
            } 
            catch (IOException f) {
                System.err.println("error while loading image");
            }
            
            JTextField passwordField = new JTextField(12);
            JLabel passwordLabel = new JLabel("password:");
            JPanel passOptionsPanel = new JPanel();
            passOptionsPanel.add(passwordLabel);
            passOptionsPanel.add(passwordField);
            JOptionPane.showConfirmDialog(null, passOptionsPanel, "Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            newImg.stringToBlueLSB(passwordField.getText());
            newImg.saveImage(lib.getFilePath());
            lib = new ImageLibrary(lib.getFilePath());
            imgShowStartIndex = 0;
            imgShowEndIndex = lib.numOfImages - 1;
            displayLibraryImages();
        }

        else if (e.getSource() == searchBar){
            String searchTerm = searchBar.getText();
            int foundIndex = lib.search(searchTerm);
            if(!searchTerm.isEmpty()&& foundIndex != -1){
                imgShowStartIndex = lib.search(searchTerm);
                imgShowEndIndex = imgShowStartIndex;
            }
            else{
                imgShowStartIndex = 0;
                imgShowEndIndex = lib.numOfImages - 1;
            }
            displayLibraryImages();
        }
        frame.repaint();
        frame.revalidate();
    }

    public void displayLibraryImages() {
        midPanel.removeAll(); // Clear previous content
        midPanel.setLayout(new FlowLayout()); // Or use GridLayout for uniform layout

        for (int i = imgShowStartIndex; i <= imgShowEndIndex && i < lib.library.length; i++) {
            StenographyImage img = lib.library[i];
            if (img != null) {
                // Create button with scaled image
                ImageIcon icon = new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                JButton button = new JButton(icon);
                button.setToolTipText(img.getName());
                button.setPreferredSize(new Dimension(100, 100));

                JPanel resultPanel = new JPanel();
                JLabel result = new JLabel(img.getTextFromBlue());
                resultPanel.add(result);
                ActionListener buttonAction = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(img.getFileType() == "png"){
                            JOptionPane.showConfirmDialog(null, resultPanel, "result", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                };

                button.addActionListener(buttonAction);
                // Create label with image name
                JLabel nameLabel = new JLabel(img.getName(), SwingConstants.CENTER);
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

        searchLabel.setForeground(Color.WHITE);
        searchBar.setForeground(Color.WHITE);
        searchBar.setBackground(Color.DARK_GRAY);
        optionsButton.setForeground(Color.WHITE);
        optionsButton.setBackground(Color.DARK_GRAY);
        topPanel.setBackground(Color.BLACK);
        midPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setBackground(Color.BLACK);
        addImageButton.setBackground(Color.DARK_GRAY);
        addImageButton.setForeground(Color.white);
        frame.repaint();
        frame.revalidate();
    }
    public void changeToLightMode(){
        searchLabel.setForeground(Color.BLACK);
        searchBar.setForeground(Color.BLACK);
        searchBar.setBackground(Color.white);
        optionsButton.setForeground(Color.BLACK);
        optionsButton.setBackground(Color.white);
        topPanel.setBackground(Color.lightGray);
        midPanel.setBackground(Color.white);
        bottomPanel.setBackground(Color.lightGray);
        addImageButton.setBackground(Color.white);
        addImageButton.setForeground(Color.black);
        frame.repaint();
        frame.revalidate();
    }
}
    
