package ab.demo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import ab.demo.other.ActionRobot;
import ab.vision.ABObject;
import ab.vision.ABShape;
import ab.vision.Vision;
import ab.vision.real.shape.Body;

public class BlocksDetails {
    private ActionRobot aRobot;

    public void Structure()
    {
        BufferedImage screenshot = ActionRobot.doScreenShot();
        Vision vision = new Vision(screenshot);

        List<ABObject> pigs = vision.findPigsMBR();
        List<ABObject> blocks = vision.findBlocksMBR();
        List<ABObject> Blocks_shape = vision.findBlocksRealShape();
        List<ABObject> hills = vision.findHills();
        ABObject type;
        ABShape circle=ABShape.Circle;
        ABShape tri=ABShape.Triangle;
        ABShape rect=ABShape.Rect;
        ABShape poly=ABShape.Poly;
        for(int i=0;i<blocks.size();i++) {
            type=new ABObject(blocks.get(i));
            System.out.println(blocks.get(i) + " block " +i+" type "+type.getType()+" shape "+blocks.get(i).shape);
        }
    }


}