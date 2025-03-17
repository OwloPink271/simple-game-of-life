package MainFrame;

import Resources.Punctulet;
import Resources.buttonPanelControl;
import SettingFrame.SettingsFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
     private static final long serialVersionUID=1L;
boolean isThreadPause = false;

    private static final String PREFERENCES_NODE_APP="/GameofLifePrefs";
    Preferences prefs= Preferences.userRoot().node(PREFERENCES_NODE_APP);

    //Setting elements
int nrIterations= prefs.getInt("maxIterations", 10000);
int startingCells= prefs.getInt("startCells",50);
boolean isBounded= false;
int threadDelay= prefs.getInt("threadDelay",100);
int cellSize= prefs.getInt("cellSize",25);

//Pause and stop locks

private final Object pauseLock = new Object();
private final Object stopGameSync= new Object();

//Counter for cycles
int i=0;

//Panels
buttonPanelControl btnPanel= new buttonPanelControl();
MainGridPanel gridPanel=new MainGridPanel();
//Icon
ImageIcon imageSmile=new ImageIcon("src/Resources/Happy.png");
//
private SwingWorker<Void, Void> startSwingWorker;


    public void updateGridPanelCellSize(int cellSize){
        gridPanel.setCELLSIZE(cellSize);
        //gridPanel.update();
    }



    public MainFrame(){


        super("A simple Game of Life :)");

        updateGridPanelCellSize(cellSize);
        //Putting the prefs into registry
        prefs.putInt("cellSize",cellSize);
        prefs.putInt("maxIterations", nrIterations);
        prefs.putBoolean("gameType",isBounded);
        prefs.putInt("threadDelay",threadDelay);
        prefs.putInt("startCells",startingCells);

        gridPanel.addState(1,Color.cyan);
        gridPanel.addState(2,Color.WHITE,Color.RED,"WL");
        gridPanel.addState(3,Color.WHITE,Color.PINK,"L");
        gridPanel.addState(4,Color.WHITE,Color.BLACK,"");
        gridPanel.addState(5,Color.WHITE,Color.GREEN,"SL");
        gridPanel.addState(6,Color.BLACK,Color.WHITE,"D");
        gridPanel.addState(7,Color.WHITE,Color.DARK_GRAY,"M");




        gridPanel.setGridListener(new MainGridPanel.GridListener() {
            public void gridReady() {
                gridPanel.repaint();

                new SwingWorker<Void, Void>(){
                    protected Void doInBackground() throws InterruptedException {
                        Thread.sleep(500);
                        for(int x=0;x<=gridPanel.getGridWidth()-1;x++){
                            for(int y=0;y<=gridPanel.getGridHeight()-1;y++){
                                gridPanel.setCell(4,x,y);
                                gridPanel.update();
                            }
                        }

                        randomyStartingCells(gridPanel,startingCells);


                        return null;
                    }
                    @Override
                    protected void done() {
                        // Update the grid and display the number of alive cells
                        displayNrAlive(gridPanel, 3);
                    }
                }.execute();
            }




            public void click(int gridX, int gridY, int button) {
                gridPanel.setCell(3,gridX,gridY);
                gridPanel.update();
               displayNrAlive(gridPanel,3);
                int alives=gridPanel.countAlivePeriodic(gridX,gridY,3);
                System.out.println("x: "+gridX+" y: "+gridY+" alives: "+alives);

            }
        });

        //setContentPane(gridPanel);

        //Create window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,1000);
        setResizable(false);

        //setLayout(null);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JScrollPane scrollPane=new JScrollPane(gridPanel);
        add(scrollPane,BorderLayout.CENTER);

//add(buttonPanel,BorderLayout.NORTH);
        add(btnPanel,BorderLayout.NORTH);
        setVisible(true);

//add icon
        setIconImage(imageSmile.getImage());

//Button action listeners

        btnPanel.pauseResume.setEnabled(false);
        btnPanel.nextCycle.setEnabled(false);

        btnPanel.startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(btnPanel.startButton.getText().equals("Start")){
                    btnPanel.startButton.setText("Stop");

                    startSwingWorker= new SwingWorker<Void,Void>(){

                        @Override
                        protected Void doInBackground() throws Exception {

                            startItUpForRealThirdTime(gridPanel,3,4);
                            //System.out.println("GATA");

                            return null;
                        }
                        @Override
                        protected void done() {
                            gridPanel.repaint();
                            System.out.println("GATA");
                            i=0;

                        }
                    };
                    startSwingWorker.execute();
                }

            else{
                btnPanel.startButton.setText("Start");
                if(startSwingWorker !=null && !startSwingWorker.isDone()){
                    startSwingWorker.cancel(true);
                    i=0;
                    btnPanel.pauseResume.setEnabled(false);
                    btnPanel.nextCycle.setEnabled(false);
                }
                }

            }
        });


        btnPanel.randomizeCells.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int x=0;x<=gridPanel.getGridWidth()-1;x++){
                    for(int y=0;y<=gridPanel.getGridHeight()-1;y++){
                        gridPanel.setCell(4,x,y);
                        gridPanel.update();
                    }
                }
                randomyStartingCells(gridPanel,startingCells);
                gridPanel.update();
                gridPanel.repaint();
            }
        });


        btnPanel.pauseResume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    synchronized (pauseLock) {
                        if (btnPanel.pauseResume.getText().equals("Pause")) {
                            isThreadPause = true;
                            btnPanel.pauseResume.setText("Resume");
                        } else {
                            isThreadPause = false;
                            pauseLock.notifyAll();
                            btnPanel.pauseResume.setText("Pause");
                        }
                    }


            }
        });




        btnPanel.nextCycle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                startItUpOnce(gridPanel,3,4);
                ++i;
                btnPanel.setNrIterField(String.valueOf(i));

            }
        });

        btnPanel.openSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SettingsFrame settings= new SettingsFrame(MainFrame.this);
                settings.setVisible(true);
            }
        });


    }



    void startItUpOnce(MainGridPanel mainGridPanel, int stateAlive, int stateDead){
        //btnPanel.setNrIterField(String.valueOf(i));
        ArrayList<ArrayList<Punctulet>>punctuleteArrays= mainGridPanel.updateGridArray(stateAlive, stateDead, isBounded);
        ArrayList<Punctulet>willDie=punctuleteArrays.get(0);
        ArrayList<Punctulet>becomesAlive=punctuleteArrays.get(1);

        for (Punctulet punct:becomesAlive){
            mainGridPanel.setCell(stateAlive,punct.getX(),punct.getY());
            System.out.println(punct);
        }

        for(Punctulet punct: willDie){
            mainGridPanel.setCell(stateDead,punct.getX(),punct.getY());

        }


        mainGridPanel.update();
        pause(threadDelay);
        btnPanel.setNrIterField(String.valueOf(i));

    }

    void displayNrAlive(MainGridPanel mainGridPanel, int state){
        int nr= mainGridPanel.nrAlive(state);
    }

    void randomyStartingCells(MainGridPanel mainGridPanel, int nrBlocks){
        Random randomy=new Random();

        for(int i=0;i<nrBlocks;i++){
            int x=randomy.nextInt(mainGridPanel.getGridWidth());
            int y=randomy.nextInt(mainGridPanel.getGridHeight());
            mainGridPanel.setCell(3,x,y);
            int nrVecini=randomy.nextInt(9);
            for(int j=0;j<=nrVecini;j++){
                int limitXMax=Math.max(0,x-1);
                int limitXMin=Math.min(x+1, mainGridPanel.getGridWidth()-1);

                int limitYMax=Math.max(0,y-1);
                int limitYMin=Math.min(y+1, mainGridPanel.getGridHeight()-1);

                int xCord, yCord;

                do{
                   xCord=randomy.nextInt(limitXMax,limitXMin+1);
                   yCord=randomy.nextInt(limitYMax,limitYMin+1);
                }while(xCord ==x && yCord ==y);

                mainGridPanel.setCell(3,xCord,yCord);

            }
        }

    }

    void startItUpForRealThirdTime(MainGridPanel mainGridPanel, int stateAlive, int stateDead) throws InterruptedException {

        for(int j=i;j<nrIterations;j=i){

            synchronized (pauseLock) {
                while (isThreadPause) {
                    pauseLock.wait();
                }
            }

            synchronized (stopGameSync){

            }


            ArrayList<ArrayList<Punctulet>>punctuleteArrays= mainGridPanel.updateGridArray(stateAlive, stateDead, isBounded);
            ArrayList<Punctulet>willDie=punctuleteArrays.get(0);
            ArrayList<Punctulet>becomesAlive=punctuleteArrays.get(1);

            for (Punctulet punct:becomesAlive){
                mainGridPanel.setCell(stateAlive,punct.getX(),punct.getY());
                System.out.println(punct);
            }

            for(Punctulet punct: willDie){
                mainGridPanel.setCell(stateDead,punct.getX(),punct.getY());

            }


            mainGridPanel.update();
            i++;
            btnPanel.setNrIterField(String.valueOf(i));
            btnPanel.pauseResume.setEnabled(true);
            btnPanel.nextCycle.setEnabled(true);
            pause(threadDelay);


        }

    }

    private void pause(int mili){
        try {
            Thread.sleep(mili);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void  setNrIterations(int nrIterations) {
        this.nrIterations = nrIterations;
    }

    public void setStartingCells(int startingCells) {
        this.startingCells = startingCells;
    }

    public void setBounded(boolean bounded) {
        isBounded = bounded;
    }
    public void setThreadDelay(int delay){
        this.threadDelay=delay;
    }

}
