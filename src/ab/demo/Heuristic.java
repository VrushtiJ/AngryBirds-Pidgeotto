package ab.demo;

/**
 * Created by admin on 10/29/2014.
 */
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import java.lang.reflect.Array;
import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import ab.demo.other.ActionRobot;
import ab.vision.ABType;
import ab.vision.Vision;
import ab.vision.VisionMBR;
import ab.vision.real.shape.Rect;
import ab.vision.VisionUtils;


import java.awt.geom.Line2D;
import java.awt.Rectangle;
/**
 * Created by admin on 10/29/2014.
 */
public class Heuristic {
    BufferedImage screenshot = ActionRobot.doScreenShot();
    Vision vsn = new Vision(screenshot);
    VisionMBR vision=new VisionMBR(screenshot);
    public Point target_pt() {
        List<ABObject> blocks = vision.findBlocks();
        List<ABObject> pigs = vision.findPigs();
        List<ABObject> tnts = vision.findTNTs();
        List<ABObject> hills = vsn.findHills();
        List<ABObject> ABObj_birds = vsn.findBirdsMBR();

        List<Rectangle> redBirds = vision.findRedBirdsMBRs();
        List<Rectangle> yellowBirds = vision.findYellowBirdsMBRs();
        List<Rectangle> blueBirds = vision.findBlueBirdsMBRs();
        List<Rectangle> whiteBirds = vision.findWhiteBirdsMBRs();
        List<Rectangle> blackBirds = vision.findBlackBirdsMBRs();
        List<Rectangle> birds = new LinkedList<Rectangle>();
        birds.addAll(redBirds);
        birds.addAll(yellowBirds);
        birds.addAll(blueBirds);
        birds.addAll(blackBirds);
        birds.addAll(whiteBirds);

        int i = 0, num_of_pigs = pigs.size(), index = 0, maxH_blks_bforePigs = 0;
        double maxH = 0;
        Point pt = new Point(0, 0);

        if (pigs.size() == 1) {
            pt = pigs.get(0).getCenter();
        } else {
            ABObject p = pigs.get(pigs.size() - 1);
            i = 0;
            maxH = 0;
            maxH_blks_bforePigs = 0;
            for (ABObject b : blocks) {
                if (p.getX() > b.getX() && (p.getY() + p.getWidth()) > b.getY()) {
                    System.out.println("Before condition satisfied<<<<<<<<<<<<<<");
                    maxH = FindMaxHeight(maxH, b.getHeight());
                    index = i;
                    maxH_blks_bforePigs++;
                }
                i++;
            }
            //     System.out.println("########"+blocks.get(index).getLocation()+" "+blocks.get(index).getHeight()+" "+blocks.get(index).getType());
            TrajectoryPlanner obj = new TrajectoryPlanner();
            Rectangle bd = null;
            ABObject BD = null;
            bd = obj.findActiveBird(birds);
            for (ABObject BDs : ABObj_birds) {
//                        System.out.println(bd.getX()+"  birds rectangle and abobject-------"+BDs.getX());
                if (bd.getX() == BDs.getX()) {
//                        System.out.println("------++++++" + bd.getLocation() + " " + BDs.getLocation() + " ------+++++");
                    BD = BDs;
                    break;
                }
            }
            int j = 0, f = 0;
            if (maxH_blks_bforePigs > 1) {
//                System.out.println("Befor pigs are grater than 1 ?????????????????????????");
                for (ABObject b : blocks) {
                    f = 0;
                    if (b.getHeight() == maxH) {
                        switch (BD.getType()) {

                            case RedBird:
                                if (b.getType().compareTo(ABType.Stone) == 0||b.getType().compareTo(ABType.Ice) == 0||b.getType().compareTo(ABType.Wood) == 0) {
                                    index = j;
                                    f = 1;
                                }
                                break;


                            case BlueBird:
                                if (b.getType().compareTo(ABType.Ice) == 0) {
                                    index = j;
                                    f = 1;
                                }
                                break;

                            case YellowBird:
                                if (b.getType().compareTo(ABType.Wood) == 0) {
                                    index = j;
                                    f = 1;
                                }
                                break;

                            case BlackBird:
                                if (b.getType().compareTo(ABType.Stone) == 0 || b.getType().compareTo(ABType.Wood) == 0 || b.getType().compareTo(ABType.Ice) == 0) {
                                    index = j;
                                    f = 1;
                                }
                                break;

                            case WhiteBird:
                                if (b.getType().compareTo(ABType.Stone) == 0 || b.getType().compareTo(ABType.Wood) == 0 || b.getType().compareTo(ABType.Ice) == 0) {
                                    index = j;
                                    f = 1;
                                }
                                break;


                            default:
                                index = j;
                        }
                        if (f == 1) {
                            break;
                        }

                    }
                    j++;
                }
            }

//below pigs --- finding support of the pigs
            int k = 0, count_support = 0;
            double lowY = p.getY() + p.getHeight();
            double nearest_support = 100, min_support = 0;
//        System.out.println("----------low Y  "+lowY+"   original Y ---"+p.getY());
            for (ABObject b : blocks) {
                if (b.getY() > lowY) {
//                System.out.println("???????????????????????????/ blocks Y  "+b.getY()+" "+lowY);

                    if (b.getX() >= p.getX() - 5 &&  b.getX() <= (p.getX() + p.getWidth() + 5)) {
//                    System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzz       in between "+b.getLocation()+" "+p.getLocation());
                        min_support = b.getY() - lowY;

//                    System.out.println("%%%%%%%%%%%%%%%%%%% minimmum distance "+min_support);

                        if (nearest_support >= 0 && min_support <= nearest_support) {
//                        System.out.println("eeeeeeeeeeeeeeeeeentered in nearest support   "+count_support);
                            nearest_support = min_support;
                            count_support++;
                        }
//                    System.out.println("@@@@@@@@@@count support@@@@@@@@");
                    }
                }
                k++;
            }
            System.out.println("count support '''''''''''''''''''''  " + count_support);

            // pt=blocks.get(index).getCenter();
            ABObject bl = blocks.get(index);
            int f2 = 0, h2 = 0;
            double cover = bl.getX() + bl.getHeight();
            /*if (p.getX() < cover) {
                System.out.println("block can cover pig.. ^^^^^^^^ ");
                pt = bl.getCenter();
                f2=1;
            }*/
            if (maxH_blks_bforePigs != 0) {
                System.out.println("considering distance btwn block and pig left hand side ///////" + bl.getLocation() + " " + bl.getType());
                double min = p.getX() - blocks.get(index).getX(), dist = 0;
                int l = 0;
                for (ABObject b : blocks) {
                    dist = p.getX() - b.getX();
                    if (dist > 0 && dist < min) {
                        min = dist;
                        index = l;
                        f2 = 1;
                    }
                    l++;
                }
                pt = blocks.get(index).getCenter();
            }
            if (f2 == 0 && count_support != 0) {
                System.out.println("supporting section ********");

                k = 0;
                for (ABObject b : blocks) {
                    if (b.getY() > lowY) {
//                        System.out.println("below y................");
                        if (b.getX() >= p.getX() - 5 && b.getX() <= (p.getX() + p.getWidth()) + 5) {

//                            System.out.println("btwn x and y~~~~~~~~~~~~");
                            min_support = b.getY() - lowY;
                            if (nearest_support >= 0 && min_support <= nearest_support) {
                                System.out.println("nearest block.............????>.........");
                                min_support = nearest_support;
                                index = k;
                                h2 = 1;
                            }
//                            System.out.println("support");
                        }
                    }
                    k++;
                }
//                System.out.println(blocks.get(index).getCenter()+" vv "+blocks.get(index).getY()+" vv  "+ blocks.get(index).getX()+" pp "+p.getCenter()+" "+p.getX()+" "+p.getY());
//                System.out.println("exit "+blocks.get(index).getLocation()+" index "+index+" "+ blocks.get(index).getType());
//                System.out.println("h2 and f2"+ h2+"    "+f2);
                pt = blocks.get(index).getCenter();
            }
            if (f2 == 0 && h2 == 0) {
                System.out.println("Bird ALOn........&&&&&&&&&&&&");
                pt = p.getCenter();
            }
//        System.out.println("@@@@@@@@@@@@@@@@@@@2   time to return @@@@@@@@@@@@  "+pt.getLocation()+"  "+p.getLocation());
        }
            return pt;

    }

    public double FindMaxHeight(double maxH, double blockH)
    {
        if(maxH<blockH)
        {
            maxH=blockH;
        }
        return maxH;
    }
}

