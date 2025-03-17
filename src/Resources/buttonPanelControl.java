package Resources;

import javax.swing.*;
import java.awt.*;

public class buttonPanelControl extends JPanel {

     public JButton startButton=new JButton("Start");
    public JButton randomizeCells =new JButton("Random");
    public JButton pauseResume = new JButton("Pause");
    public JButton nextCycle = new JButton("Next");
    public JButton openSettings = new JButton("Settings");
    public JLabel nrIterationLabel= new JLabel("Nr iterations:");
    public JTextField nrIterationTextField= new JTextField("0",20);


   public buttonPanelControl(){

       setLayout(new FlowLayout(FlowLayout.LEFT));
       add(startButton);
       add(randomizeCells);
       add(pauseResume);
       add(nextCycle);
       add(openSettings);
       add(nrIterationLabel);
       add(nrIterationTextField);
   }

   public void setNrIterField(String newIter){
       nrIterationTextField.setText(newIter);
   }

}
