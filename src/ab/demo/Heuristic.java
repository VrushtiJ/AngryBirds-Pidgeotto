package ab.demo;

/**
 * Created by admin on 10/29/2014.
 */
import ab.planner.TrajectoryPlanner;
import ab.utils.ABUtil;
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

    private Random randomGenerator;
    public Point target_pt(int flag) {

        randomGenerator = new Random();
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

        int pin=0, f2=0;

        int i = 0, index = 0, maxH_blks_bforePigs = 0;
        List<ABObject> Ice_Blocks_Acc_Birds=new LinkedList<ABObject>();
        List<ABObject> Wood_Blocks_Acc_Birds=new LinkedList<ABObject>();
        List<ABObject> Stone_Blocks_Acc_Birds=new LinkedList<ABObject>();
        double maxH = 0;
        Point pt = new Point(0, 0);
        List<ABObject>  Blocks_Before_pig=new LinkedList<ABObject>();
        if(flag==1)
        {
            ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));
            Point _tpt = pig.getCenter();
        }
        else {
            pin = pigs.size();
            //If one pig in the structure than target point will be that pig.
            if (pigs.size() == 1) {
                pt = pigs.get(0).getCenter();
            } else {  //Finding left nearest block to the pig
                ABObject p = pigs.get(randomGenerator.nextInt(pigs.size()));
                i = 0;
                maxH = 0;
                maxH_blks_bforePigs = 0;
                for (ABObject b : blocks) {
                    if (p.getX() > b.getX() && (p.getY()+5) < b.getY()) {
                        maxH = FindMaxHeight(maxH, b.getHeight());
                        maxH_blks_bforePigs++;
                        //  Blocks_Before_pig.add(b);
                        if (b.getType() == ABType.Ice) {
                            Ice_Blocks_Acc_Birds.add(b);
                        }
                        if (b.getType() == ABType.Wood) {
                            Wood_Blocks_Acc_Birds.add(b);
                        }
                        if (b.getType() == ABType.Stone) {
                            Stone_Blocks_Acc_Birds.add(b);
                        }
                    }
                }
                if (maxH_blks_bforePigs != 0) {
                    double min = 0, dist = 0;
                    ABObject Ice_Block = new ABObject();
                    if (blocks.size() != 0) {
                        min = p.getX() - blocks.get(0).getX();
                    }
                    for (i = 0; i < Ice_Blocks_Acc_Birds.size(); i++) {
                        dist = p.getX() - Ice_Blocks_Acc_Birds.get(i).getX();
                        //   distY=b.getY()-p.getY();
                        if (dist > 0 && dist < min) {
                            min = dist;
                            Ice_Block = Ice_Blocks_Acc_Birds.get(i);
                            f2 = 1;
                        }
                    }
                    ABObject Wood_Block = new ABObject();
                    for (i = 0; i < Wood_Blocks_Acc_Birds.size(); i++) {
                        dist = p.getX() - Wood_Blocks_Acc_Birds.get(i).getX();
                        //   distY=b.getY()-p.getY();
                        if (dist > 0 && dist < min) {
                            min = dist;
                            Wood_Block = Wood_Blocks_Acc_Birds.get(i);
                            f2 = 1;
                        }
                    }
                    ABObject Stone_Block = new ABObject();
                    for (i = 0; i < Stone_Blocks_Acc_Birds.size(); i++) {
                        dist = p.getX() - Stone_Blocks_Acc_Birds.get(i).getX();
                        //   distY=b.getY()-p.getY();
                        if (dist > 0 && dist < min) {
                            min = dist;
                            Stone_Block = Ice_Blocks_Acc_Birds.get(i);
                            f2 = 1;
                        }
                    }
                    TrajectoryPlanner obj = new TrajectoryPlanner();
                    Rectangle bd = null;
                    ABObject BD = null;
                    bd = obj.findActiveBird(birds);
                    for (ABObject BDs : ABObj_birds) {
                        if (bd.getX() == BDs.getX()) {
                            BD = BDs;
                            break;
                        }
                    }
                    int j = 0, f = 0;
                    ABObject Final_Block = new ABObject();
                    if (BD != null) {
                        for (ABObject b : Blocks_Before_pig) {
                            switch (BD.getType()) {
                                case RedBird:
                                        double minimum = p.getX() - Stone_Block.getX();
                                        Final_Block = Stone_Block;
                                        if (p.getX() - Wood_Block.getX() < minimum) {
                                            Final_Block = Wood_Block;
                                            minimum = p.getX() - Wood_Block.getX();
                                        }
                                        if (p.getX() - Stone_Block.getX() < minimum) {
                                            Final_Block = Stone_Block;
                                            minimum = p.getX() - Stone_Block.getX();
                                        }
                                        f = 1;

                                    break;


                                case BlueBird:
                                        Final_Block = Ice_Block;
                                        if (Ice_Blocks_Acc_Birds.size() == 0) {
                                            Final_Block = Wood_Block;
                                            if (Wood_Blocks_Acc_Birds.size() == 0) {
                                                Final_Block = Stone_Block;
                                            }
                                        }
                                        f = 1;

                                    break;

                                case YellowBird:
                                        Final_Block = Wood_Block;
                                        if (Wood_Blocks_Acc_Birds.size() == 0) {
                                            Final_Block = Ice_Block;
                                            if (Ice_Blocks_Acc_Birds.size() == 0) {
                                                Final_Block = Stone_Block;
                                            }
                                        }
                                        f = 1;

                                    break;

                                case BlackBird:
                                        minimum = p.getX() - Stone_Block.getX();
                                        Final_Block = Stone_Block;
                                        if (p.getX() - Wood_Block.getX() < minimum) {
                                            Final_Block = Wood_Block;
                                            minimum = p.getX() - Wood_Block.getX();
                                        }
                                        if (p.getX() - Stone_Block.getX() < minimum) {
                                            Final_Block = Stone_Block;
                                            minimum = p.getX() - Stone_Block.getX();
                                        }
                                        f = 1;

                                    break;

                                case WhiteBird:
                                        minimum = p.getX() - Stone_Block.getX();
                                        Final_Block = Stone_Block;
                                        if (p.getX() - Wood_Block.getX() < minimum) {
                                            Final_Block = Wood_Block;
                                            minimum = p.getX() - Wood_Block.getX();
                                        }
                                        if (p.getX() - Stone_Block.getX() < minimum) {
                                            Final_Block = Stone_Block;
                                            minimum = p.getX() - Stone_Block.getX();
                                        }
                                        f = 1;

                                    break;


                                default:
                                    if (b.getType().compareTo(ABType.Stone) == 0 || b.getType().compareTo(ABType.Wood) == 0 || b.getType().compareTo(ABType.Ice) == 0) {
                                        minimum = p.getX() - Stone_Block.getX();
                                        Final_Block = Stone_Block;
                                        if (p.getX() - Wood_Block.getX() < minimum) {
                                            Final_Block = Wood_Block;
                                            minimum = p.getX() - Wood_Block.getX();
                                        }
                                        if (p.getX() - Stone_Block.getX() < minimum) {
                                            Final_Block = Stone_Block;
                                            minimum = p.getX() - Stone_Block.getX();
                                        }
                                        f = 1;
                                    }
                                    //   index = j;
                            }
                        }
                    }
                    System.out.println("Befor blocks will be target point````````````````");
                    pt = Final_Block.getCenter();
                }
//below pigs --- finding support of the pigs
            /*int k = 0, count_support = 0;
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
            }*/
                //System.out.println("count support '''''''''''''''''''''  " + count_support);

                // pt=blocks.get(index).getCenter();
            /*if (p.getX() < cover) {
                System.out.println("block can cover pig.. ^^^^^^^^ ");
                pt = bl.getCenter();
                f2=1;
            }*/
                int h2 = 0;
                ABUtil support = new ABUtil();
                int nearest_supp_blk = 0, count_support = 0;
                List<ABObject> Supp_blocks = support.getSupporters(p, blocks);

                for (i = 0; i < Supp_blocks.size(); i++) {
                    if (support.isSupport(p, Supp_blocks.get(i))) {
                        count_support++;
                    }
                }

                if (f2 == 0 && count_support != 0) {
                    ABObject supp_blk = Supp_blocks.get(0);
                    System.out.println("BIRD IS NOT ALONE IT HAS SUPPORT.. :) :)");
                    for (i = 0; i < Supp_blocks.size(); i++) {
                        if (Supp_blocks.get(i).getY() - p.getY() >= nearest_supp_blk) {
                            supp_blk = Supp_blocks.get(i);
                            System.out.println("Supporting Blocks :--------------------  " + Supp_blocks.get(i) + " " + Supp_blocks.get(i).getType());
                        }
                    }
                    pt = supp_blk.getCenter();
                    h2 = 1;
                }

/*
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
            }*/
                if (f2 == 0 && h2 == 0) {
                    System.out.println("Bird ALOn........&&&&&&&&&&&&");
                    pt = p.getCenter();
                }
//        System.out.println("@@@@@@@@@@@@@@@@@@@2   time to return @@@@@@@@@@@@  "+pt.getLocation()+"  "+p.getLocation());
            }
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

