package MainFrame;

import Resources.Punctulet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainGridPanel extends JPanel  {

    public interface GridListener{
        void gridReady();
        void click(int gridX,int gridY,int button);

    }

    public void setGridListener(GridListener gridListener){
        this.gridListener=gridListener;
    }


    private GridListener gridListener;
    private static final long serialVersionUID=1L;
    private static final Font font=new Font("Times New Roman", Font.BOLD,25);
    //private static final int CELLSIZE=25;
    private int CELLSIZE=25;
    private int gridWidth;
    private int gridHeight;
    private int leftMargin;
    private int topMargin;
    private Map<Integer, BufferedImage>statesMap=new HashMap<>();
    private Integer[][] states;

    public void setCELLSIZE(int CELLSIZE) {
        this.CELLSIZE = CELLSIZE;
    }

    public MainGridPanel(){
        setPreferredSize(new Dimension(gridWidth*CELLSIZE,gridHeight*CELLSIZE));

        setBackground(Color.darkGray);
        addState(0,Color.ORANGE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int gridX=(e.getX()-leftMargin)/CELLSIZE;
                int gridY=(e.getY()-topMargin)/CELLSIZE;

                if(gridListener!=null){
                    gridListener.click(gridX,gridY,e.getButton());
                    System.out.println(e.getButton());
                }
                super.mouseClicked(e);
            }
        });

    }

    public void update(){
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2=(Graphics2D) g;


        int width=getWidth();
        int height=getHeight();


//SIZE OF THE BLUE CELLS

        gridWidth=(width/CELLSIZE)-2;
        gridHeight=(height/CELLSIZE)-2;

        initCells(gridWidth,gridHeight);

        int spareX=width-(gridWidth*CELLSIZE);
        int spareY=height-(gridHeight*CELLSIZE);

         leftMargin=spareX/2;
         topMargin=spareY/2;

        g2.setColor(Color.BLUE);

        g2.fillRect(leftMargin,topMargin,width+1-spareX,height+1-spareY);

        System.out.println("Height: "+gridHeight);
        System.out.println("Width: "+gridWidth);

        for(int gridX=0;gridX<gridWidth;gridX++){
            for(int gridY=0;gridY<gridHeight;gridY++){

                int x=gridX*CELLSIZE+leftMargin;
                int y=gridY*CELLSIZE+topMargin;

                Integer state=states[gridY][gridX];

                BufferedImage bi=statesMap.get(state);

                g2.drawImage(bi,x+1,y+1,null);
            }
        }
    }



    private void initCells(int gridWidth, int gridHeight){
        if(states!=null){
            return;
        }
        states=new Integer[gridHeight][gridWidth];

        Arrays.stream(states).forEach(a->Arrays.fill(a,0));

        if(gridListener!=null){
            gridListener.gridReady();
        }
    }

    public void addState(Integer state, Color background){
        addState(state,Color.WHITE,background,"");


    }


    public void addState(Integer state, Color foreground,Color background, String character){
        BufferedImage bi=new BufferedImage(CELLSIZE-1,CELLSIZE-1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g=bi.createGraphics();
        g.setColor(background);
        g.fillRect(0,0,CELLSIZE-1,CELLSIZE-1);

        if(character.length()!=0){
            g.setColor(foreground);
            g.setFont(font);

            FontRenderContext frc=g.getFontRenderContext();
            TextLayout tl=new TextLayout(character,font,frc);

            Rectangle2D bounds=tl.getBounds();

            float x=CELLSIZE/2 - (float)bounds.getCenterX();
            float y=CELLSIZE/2 - (float)bounds.getCenterY();

            tl.draw(g,x,y);
        }

        g.dispose();

        statesMap.put(state,bi);
    }

    public void setCell(int state, int x, int y) {
        states[y][x]=state;
    }

    public Integer getCell(int x, int y){
        return states[y][x];
    }


    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int nrAlive(int state){
        int nrdeAlive=0;
        for(int x=0;x<=gridWidth-1;x++){
            for(int y=0;y<=gridHeight-1;y++){
                if(states[y][x]==state){
                    nrdeAlive++;
                }
            }
        }
        return nrdeAlive;
    }



  public int countAlive(int x, int y, int stateAlive){
        int alive=0;
      for(int i=Math.max(0,x-1);i<=Math.min(gridWidth-1,x+1);i++){
          for (int j=Math.max(0,y-1);j<=Math.min(gridHeight-1,y+1);j++){
              if(states[j][i]==stateAlive && (!(i==x && j==y))){
                    alive++;
                }
            }
        }
        return alive;
  }

  public int countAlivePeriodic(int x, int y, int stateAlive){
       int alive=0;
       if(x>=1 && x<=gridWidth-2 && y>=1 && y<=gridHeight-2){
           for(int i=x-1;i<=x+1;i++){
               for (int j=y-1;j<=y+1;j++){
                   if(states[j][i]==stateAlive && (!(i==x && j==y))){
                       alive++;
                   }
               }
           }
       }
       else{
           if(x>=1 && x<=gridWidth-2){
               for(int i=x-1;i<=x+1;i++){
                   alive += returnAliveYPeriodic(x,i,y,stateAlive);
               }
           }

           else if(x>=1 && x==gridWidth-1){
               ArrayList<Integer>listXGridWidth=new ArrayList<>();
               listXGridWidth.add(x-1);
               listXGridWidth.add(x);
               listXGridWidth.add(0);
               for(Integer xiu: listXGridWidth){

                   alive += returnAliveYPeriodic(x,xiu,y,stateAlive);
               }
           }

           else if(x==0){
                    ArrayList<Integer>listX0=new ArrayList<>();
                    listX0.add(gridWidth-1);
                    listX0.add(x);
                    listX0.add(x+1);
                    for(Integer intu:listX0){
                        alive += returnAliveYPeriodic(x, intu,y,stateAlive);
                    }
           }


       }


      return alive;
  }

 int returnAliveYPeriodic(int x, int i,int y, int stateAlive){

        int alive=0;
     if(y>=1 && y<=gridHeight-2){
         for(int j=y-1;j<=y+1;j++){
             if(states[j][i]==stateAlive && (!(i==x && j==y))){
                 alive++;
             }
         }
     }
     else if(y>=1 && y==gridHeight-1){
         ArrayList<Integer>listYHeight=new ArrayList<>();
         listYHeight.add(y-1);
         listYHeight.add(y);
         listYHeight.add(0);
         for(Integer yiu: listYHeight){
             if(states[yiu][i]==stateAlive && (!(i==x && yiu==y))){
                 alive++;
             }
         }
     }
     else if(y==0){
         ArrayList<Integer>listY0=new ArrayList<>();
         listY0.add(gridHeight-1);
         listY0.add(y);
         listY0.add(y+1);
         for (Integer yiu0: listY0){
             if(states[yiu0][i]==stateAlive && (!(i==x && yiu0==y))){
                 alive++;
             }
         }
     }
        return alive;
 }

  public ArrayList<ArrayList<Punctulet>> updateGridArray(int stateAlive, int stateDead, boolean isBound){
        ArrayList<ArrayList<Punctulet>>punctuletArray=new ArrayList<>();
        ArrayList<Punctulet>willDie=new ArrayList<>();
      ArrayList<Punctulet>becomesAlive=new ArrayList<>();


        for(int i=0;i<=gridWidth-1;i++){
            for(int j=0;j<=gridHeight-1;j++){

                int alives;

                if(isBound){
                    alives=countAlive(i,j,stateAlive);
                }

                else{
                    alives=countAlivePeriodic(i,j,stateAlive);
                }

               if(states[j][i]==stateAlive){
                  if(alives>=2 && alives<=3){
                      var punct=new Punctulet(i,j);
                      becomesAlive.add(punct);
                  }
                  else{
                      var punct=new Punctulet(i,j);
                      willDie.add(punct);
                  }
               }
               else if(states[j][i]==stateDead){
                   if(alives==3){
                       var punct=new Punctulet(i,j);
                       becomesAlive.add(punct);

                   }
                   else{
                       var punct=new Punctulet(i,j);
                       willDie.add(punct);
                   }
               }
            }
        }
        punctuletArray.add(willDie);
        punctuletArray.add(becomesAlive);
        return  punctuletArray;
  }





}
