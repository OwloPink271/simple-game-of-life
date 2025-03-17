package SettingFrame;

import MainFrame.MainFrame;
import Resources.NumericDocumentFilter;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class SettingsFrame extends JFrame {

    int nrIterations;
    int nrIterationsbase=10000;
    int nrStartingCells;
    int nrStartingCellsBase=50;
    boolean isBounded;
    int cellSize;
    int cellSizeBase=25;
    int threadDelay;
    int baseThreadDelay= 100;



    private static final String PREFERENCES_NODE_APP="/GameofLifePrefs";
    Preferences prefs= Preferences.userRoot().node(PREFERENCES_NODE_APP);
    static JFrame frameDialog;
    ImageIcon imageSmile=new ImageIcon("src/Resources/Happy.png");
    //NumericDocumentFilter docFilter= new NumericDocumentFilter();

    public int getNrIterations() {
        return nrIterations;
    }

    public SettingsFrame(MainFrame mainFrame) {
        super("SettingFrame");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setIconImage(imageSmile.getImage());

        //Formater

        NumberFormat formatInt=NumberFormat.getIntegerInstance();
        NumberFormatter formatterInt=new NumberFormatter(formatInt);
        formatterInt.setValueClass(Integer.class);
        formatterInt.setMinimum(0);
        formatterInt.setMaximum(Integer.MAX_VALUE);
        formatterInt.setAllowsInvalid(false);
        formatterInt.setOverwriteMode(false);

        // Label: "Max iterations"
        JLabel maxIterations = new JLabel("Max iterations");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 20, 10, 10);
        add(maxIterations, gbc);

        // Input max iterations
         JFormattedTextField inputMaxIterations = new  JFormattedTextField(formatterInt);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;






        //Nr of iterations
        nrIterations=prefs.getInt("maxIterations",nrIterationsbase);
        inputMaxIterations.setText(String.valueOf(nrIterations));
        add(inputMaxIterations, gbc);

        //Label interval iterations
        JLabel intervalIterations=new JLabel("Please use a number contained in this interval: 2-100000");
        gbc.gridx=2;
        add(intervalIterations,gbc);

        //Label "Number of possible starting cells"
        JLabel startCells=new JLabel("Starting cells");
        gbc.gridx=0;
        gbc.gridy=1;
        add(startCells,gbc);


        //Input nr starting cells
         JFormattedTextField inputStartingCells=new  JFormattedTextField(formatterInt);
        gbc.gridx=1;
        gbc.weightx=1.0;
        gbc.fill=GridBagConstraints.HORIZONTAL;

        nrStartingCells=prefs.getInt("startCells",nrStartingCellsBase);
        inputStartingCells.setText(String.valueOf(nrStartingCells));
        add(inputStartingCells,gbc);

        //Label interval starting cells
        JLabel intervalStartingCells=new JLabel("Please use a number contained in this interval: 2-20000");
        gbc.gridx=2;
        add(intervalStartingCells,gbc);

        //Radio label
        JLabel radioLabel= new JLabel("Type of grid");
        gbc.gridx=0;
        gbc.gridy=2;
        add(radioLabel,gbc);
        //Radio buttons
        JRadioButton  toroidalRadio = new JRadioButton("Toroidal");
        JRadioButton boundedRadio=new JRadioButton("Bounded");
        gbc.gridx=1;
        add(toroidalRadio,gbc);
        gbc.gridx=2;
        add(boundedRadio,gbc);

       boolean tempGameTypeBounded=prefs.getBoolean("gameType",isBounded);
       if(tempGameTypeBounded){
           boundedRadio.setSelected(true);

       }
       else{
           toroidalRadio.setSelected(true);

       }


        //Radio group
        ButtonGroup radioGroup= new ButtonGroup();
        radioGroup.add(toroidalRadio);
        radioGroup.add(boundedRadio);

        //Label cell size
        JLabel labelCellSize= new JLabel("Cell size");
        gbc.gridx=0;
        gbc.gridy=3;
        add(labelCellSize,gbc);

        //Change cell size text field
         JFormattedTextField inputCellSize= new  JFormattedTextField(formatterInt);
        gbc.gridx=1;
        cellSize=prefs.getInt("cellSize",cellSizeBase);
       inputCellSize.setText(String.valueOf(cellSize));
        add(inputCellSize,gbc);
        int currentTextCellSize=Integer.parseInt(inputCellSize.getText());

        //Label interval cell size
        JLabel intervalCellSize=new JLabel("Please use a number contained in this interval: 2-50");
        gbc.gridx=2;
        add(intervalCellSize,gbc);

        //Label thread delay
        JLabel labelThreadDelay= new JLabel("Delay ms");
        gbc.gridx=0;
        gbc.gridy=4;
        add(labelThreadDelay,gbc);

        //Text field thread delay
         JFormattedTextField inputThreadDelay =new  JFormattedTextField(formatterInt);
        threadDelay=prefs.getInt("threadDelay",baseThreadDelay);
        inputThreadDelay.setText(String.valueOf(threadDelay));
        gbc.gridx=1;
        gbc.gridy=4;
        add(inputThreadDelay,gbc);

        //Label interval time delay milliseconds
        JLabel intervalThreadDelay=new JLabel("Please use a number contained in this interval: 2-1000");
        gbc.gridx=2;
        add(intervalThreadDelay,gbc);




        // Filler to push the button down
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel filler=new JPanel();
        filler.setBackground(Color.WHITE);
        add(filler, gbc);

        // Centered Button
        JButton saveSettings = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveSettings, gbc);


        //Apply doc filter

        applyNumericFilter(inputMaxIterations);
        applyNumericFilter(inputStartingCells);
        applyNumericFilter(inputCellSize);
        applyNumericFilter(inputThreadDelay);


        saveSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<JFormattedTextField>textFieldsSettings=new ArrayList<>();
                textFieldsSettings.add(inputMaxIterations);
                textFieldsSettings.add(inputStartingCells);
                textFieldsSettings.add(inputCellSize);
                textFieldsSettings.add(inputThreadDelay);

                if(checkSettingTextFieldsIntervals(textFieldsSettings)){

                    nrIterations=Integer.parseInt(inputMaxIterations.getText().replaceAll(",",""));
                    mainFrame.setNrIterations(nrIterations);
                    prefs.putInt("maxIterations", nrIterations);

                    nrStartingCells=Integer.parseInt(inputStartingCells.getText().replaceAll(",",""));
                    mainFrame.setStartingCells(nrStartingCells);
                    prefs.putInt("startCells",nrStartingCells);

                    if(toroidalRadio.isSelected()){
                        isBounded=false;
                        mainFrame.setBounded(false);
                        prefs.putBoolean("gameType",isBounded);
                    }
                    else if(boundedRadio.isSelected()){
                        isBounded=true;
                        mainFrame.setBounded(true);
                        prefs.putBoolean("gameType",isBounded);
                    }

                    cellSize=Integer.parseInt(inputCellSize.getText().replaceAll(",",""));
                    prefs.putInt("cellSize",cellSize);
                    if(cellSize!=currentTextCellSize){

                        mainFrame.updateGridPanelCellSize(cellSize);
                        try {
                            restartApp();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    threadDelay = Integer.parseInt(inputThreadDelay.getText().replaceAll(",",""));
                    mainFrame.setThreadDelay(threadDelay);
                    prefs.putInt("threadDelay",threadDelay);



                    dispatchEvent(new WindowEvent(SettingsFrame.this,WindowEvent.WINDOW_CLOSING));

                }

                else{


                    JOptionPane.showMessageDialog(frameDialog,"The following fields use values outside the allowed intervals: \n"+listTextFieldsWithErrors(textFieldsSettings)+".","ERROR!",JOptionPane.ERROR_MESSAGE);
                    setLocationRelativeTo(null);
                }



            }
        });

        setSize(700, 400);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        setLocationRelativeTo(null);
    }

    public void restartApp() throws IOException {
        final String javaBin=System.getProperty("java.home")+ File.separator+"bin"+File.separator+"java";
        final String classPath = System.getProperty("java.class.path");
        final String mainClass=System.getProperty("sun.java.command").split(" ")[0];

        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp",classPath,mainClass);
        builder.inheritIO();
        builder.start();

        System.exit(0);

    }

    public boolean checkSettingTextFieldsIntervals(ArrayList<JFormattedTextField>textFields){
       for(int i=0;i<=3;i++){
           int numberSetting=Integer.parseInt(textFields.get(i).getText().replaceAll(",",""));
           if(i==0){
               if(numberSetting<2||numberSetting>100000){
                   return false;
               }
           }
           else if (i==1){
               if(numberSetting<2||numberSetting>20000){
                   return false;
               }
           }

           else if (i==2){
               if(numberSetting<2||numberSetting>50){
                   return false;
               }
           }

           else {
               if(numberSetting<2||numberSetting>1000){
                   return false;
               }
           }
       }

        return true;
    }

    public String listTextFieldsWithErrors(ArrayList<JFormattedTextField>textFields){

        String finalString="";
        for(int i=0;i<=3;i++){
            String setting;
            int numberSetting=Integer.parseInt(textFields.get(i).getText().replaceAll(",",""));
            if(i==0){
                if(numberSetting<2||numberSetting>100000){
                    setting="Max iterations";
                    finalString+=setting+", ";
                }
            }
            else if (i==1){
                if(numberSetting<2||numberSetting>20000){
                    setting="Starting cells";
                    finalString+=setting+", ";
                }
            }

            else if (i==2){
                if(numberSetting<2||numberSetting>50){
                    setting="Cell size";
                    finalString+=setting+", ";
                }
            }

            else if (i==3) {
                if(numberSetting<2||numberSetting>1000){
                    setting="Delay ms";
                    finalString+=setting;
                }
            }
        }
        return  finalString;
    }

    private void applyNumericFilter(JFormattedTextField textField){
        ((PlainDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
    }


}
