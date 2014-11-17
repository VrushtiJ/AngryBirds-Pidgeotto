package ab.demo;

import ab.vision.ABObject;

import java.lang.reflect.Array;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import ab.demo.other.ActionRobot;
import ab.vision.ABType;
import ab.vision.Vision;
import java.awt.geom.Line2D;
import java.awt.Rectangle;
/**
 * Created by admin on 10/10/2014.
 */
public class Deciding_Trajectory {

    BufferedImage screenshot = ActionRobot.doScreenShot();
    Vision vision = new Vision(screenshot);

    public int Traj() {
        int flag=0;
        //Structure
        List<ABObject> Blocks = vision.findBlocksMBR();
        List<ABObject> Hills = vision.findHills();
        List<ABObject> TNTs = vision.findTNTs();
        //Birds
        List<ABObject> Pigs = vision.findPigsMBR();

        //Trajetory Pts

        //starting of structure and ending of structure
        double first_PigX=0,last_PigX=0,first_HillX=0,last_HillX=0,first_TNTX=0,last_TNTX=0,first_BlockX=0,last_BlockX=0;
        double[] point_list;
        if(Pigs.size()!=0) {
            point_list = sortedListX(Pigs);
            if (point_list != null) {
                first_PigX = point_list[0];
                last_PigX = point_list[point_list.length - 1];
            }
        }
        double pigw=last_PigX-first_PigX;
       // System.out.println(pigw+"  pigw  ");
        /*if(Hills!=null) {
            System.out.println("Hills");
            point_list = sortedListX(Hills);
            if(point_list!=null) {
                first_HillX = point_list[0];
                last_HillX = point_list[point_list.length - 1];
            }
        }*/
        if(TNTs.size()!=0) {
            System.out.println("Hills");
            point_list = sortedListX(TNTs);
            if (point_list != null) {
                first_TNTX = point_list[0];
                last_TNTX = point_list[point_list.length - 1];
            }
        }
        if(Blocks.size()!=0) {
            point_list = sortedListX(Blocks);
            if (point_list != null) {
                first_BlockX = point_list[0];
                last_BlockX = point_list[point_list.length - 1];
            }
        }
        //finding structure's width
        double maxX=0, minX=0, width=0;
        minX=FindMin(first_BlockX,first_TNTX,first_PigX);
        maxX=FindMax(last_BlockX,last_TNTX,last_PigX);
        width=maxX-minX;
       // System.out.println(width+" width");

        //finding maximum and minimum Y of structure
        double first_PigY=0,last_PigY=0,first_HillY=0,last_HillY=0,first_TNTY=0,last_TNTY=0,first_BlockY=0,last_BlockY=0;

        if(Pigs.size()!=0) {
            point_list = sortedListY(Pigs);
            if (point_list != null) {
                first_PigY = point_list[0];
                last_PigY = point_list[point_list.length - 1];
            }
        }
        double pigH=last_PigY-first_PigY;

       // System.out.println(pigH+"  pigH  ");
        /*if(Hills!=null) {
            System.out.println("Hills");
            point_list = sortedListX(Hills);
            if(point_list!=null) {
                first_HillX = point_list[0];
                last_HillX = point_list[point_list.length - 1];
            }
        }*/
        if(TNTs.size()!=0) {
            System.out.println("Hills");
            point_list = sortedListY(TNTs);
            if (point_list != null) {
                first_TNTY = point_list[0];
                last_TNTY= point_list[point_list.length - 1];
            }
        }
        if(Blocks.size()!=0) {
            point_list = sortedListY(Blocks);
            if (point_list != null) {
                first_BlockY= point_list[0];
                last_BlockY = point_list[point_list.length - 1];
            }
        }
        //finding structure's width
        double maxY=0, minY=0, Height=0;
        minY=FindMin(first_BlockY,first_TNTY,first_PigY);
        maxY=FindMax(last_BlockY,last_TNTY,last_PigY);
        Height=maxY-minY;
        //System.out.println(Height+" Height");
        if(2*Height<width)
        {
            flag=2;
        }
        else
        {
            flag=1;
        }
        return flag;
    }
    public double[] sortedListX(List<ABObject> Objects)
    {
        double[] point_list=new double[Objects.size()];
        for(int i=0;i<Objects.size();i++)
        {
            point_list[i]=Objects.get(i).getX();
     //       System.out.println(point_list[i]+" "+0);
        }
        Arrays.sort(point_list);
        return point_list;
    }

    public double[] sortedListY(List<ABObject> Objects)
    {
        double[] point_list=new double[Objects.size()];
        for(int i=0;i<Objects.size();i++)
        {
            point_list[i]=Objects.get(i).getY();
            //       System.out.println(point_list[i]+" "+0);
        }
        Arrays.sort(point_list);
        return point_list;
    }
    public double FindMin(double first_BlockX,double first_TNTX,double first_PigX)
    {
        double minX=0;
        if(first_PigX<first_BlockX)
        {
            minX=first_BlockX;
        }
        else
        {
            minX=first_PigX;
        }
        if(minX>first_TNTX)
        {
            minX=first_BlockX;
        }
        return minX;
    }

    public double FindMax(double first_BlockX,double first_TNTX,double first_PigX)
    {
        double maxX=0;
        if(first_PigX>first_BlockX)
        {
            maxX=first_BlockX;
        }
        else
        {
            maxX=first_PigX;
        }
        if(maxX<first_TNTX)
        {
            maxX=first_BlockX;
        }
        return maxX;
    }



    public Point Penetration(int index)
    {
        List<ABObject> Blocks = vision.findBlocksMBR();
        List<ABObject> Birds = vision.findBirdsMBR();
        List<ABObject> Pigs = vision.findPigsMBR();
        Rectangle sling=vision.findSlingshotMBR();
        ArrayList<ABObject> Line_Blocks=new ArrayList<ABObject>();

            System.out.println(" sling " + sling.getLocation());
            ABObject ab=Pigs.get(index);
            Line2D line=new Line2D.Double(sling.getCenterX(),sling.getCenterY(),ab.getCenterX(),ab.getCenterY());
            System.out.println(line.getP1()+"  ----------line------  "+line.getP2()+"  "+line.getBounds());
            for(ABObject bl:Blocks)
            {
              //  System.out.println(bl.getCenterX()+"  print "+bl.getCenterY()+" type "+ bl.getType()+" "+bl.getHeight()+" "+bl.getWidth());
                if(line.intersects(bl))
                {
         //           System.out.println("********in********  "+bl.getCenter()+" "+bl.getHeight()+" "+bl.getWidth()+" "+bl.getType());
                    Line_Blocks.add(bl);
                }
            }
        //System.out.println(index+" index "+ ab.getCenter());
        int o=0;
        Point po=new Point(0,0);
        //System.out.println(po.getLocation()+" "+ Line_Blocks.size()+" ++++++line size+++++");
        for(ABObject lb:Line_Blocks)
        {
          //  System.out.println("Blocks in between " +lb.getCenter()+" "+o);
            po=lb.getCenter();
            o++;
        }
        if(Line_Blocks.size()!=0)
        {
            return Line_Blocks.get(0).getCenter();
        }
        else {
            return po;
        }
    }
}