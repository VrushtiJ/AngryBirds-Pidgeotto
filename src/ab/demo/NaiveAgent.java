/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License. 
 **To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/ 
 *or send a letter to Creative Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
 *****************************************************************************/
package ab.demo;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;;
import ab.vision.ABShape;
import jdk.nashorn.internal.ir.Block;

public class NaiveAgent implements Runnable {

	private ActionRobot aRobot;
	private Random randomGenerator;
	public int currentLevel = 1;
	public static int time_limit = 12;
	private Map<Integer,Integer> scores = new LinkedHashMap<Integer,Integer>();
	TrajectoryPlanner tp;
	private boolean firstShot;
	private Point prevTarget;
	int count=0;
	// a standalone implementation of the Naive Agent
	public NaiveAgent() {
		
		aRobot = new ActionRobot();
		tp = new TrajectoryPlanner();
		prevTarget = null;
		firstShot = true;
		randomGenerator = new Random();
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();

	}

	
	// run the client
	public void run() {

    //    System.out.println(currentLevel+" currentlevel");
		aRobot.loadLevel(currentLevel);
		while (true) {

			GameState state = solve();
			if (state == GameState.WON) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int score = StateUtil.getScore(ActionRobot.proxy);
				if(!scores.containsKey(currentLevel))
					scores.put(currentLevel, score);
				else
				{
					if(scores.get(currentLevel) < score)
						scores.put(currentLevel, score);
				}
				int totalScore = 0;
				for(Integer key: scores.keySet()){

					totalScore += scores.get(key);
					System.out.println(" Level " + key
							+ " Score: " + scores.get(key) + " ");
				}
				System.out.println("Total Score: " + totalScore);
                count++;
                aRobot.loadLevel(++currentLevel);
				// make a new trajectory planner whenever a new level is entered
				tp = new TrajectoryPlanner();

				// first shot on this level, try high shot first
				firstShot = true;
			} else if (state == GameState.LOST) {
				System.out.println("Restart");
				aRobot.restartLevel();
			} else if (state == GameState.LEVEL_SELECTION) {
				System.out
				.println("Unexpected level selection page, go to the last current level : "
						+ currentLevel);
				aRobot.loadLevel(currentLevel);
			} else if (state == GameState.MAIN_MENU) {
				System.out
				.println("Unexpected main menu page, go to the last current level : "
						+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				aRobot.loadLevel(currentLevel);
			} else if (state == GameState.EPISODE_MENU) {
				System.out
				.println("Unexpected episode menu page, go to the last current level : "
						+ currentLevel);
				ActionRobot.GoFromMainMenuToLevelSelection();
				aRobot.loadLevel(currentLevel);
			}

		}
//        System.out.println("8 levels are completed");

	}

	private double distance(Point p1, Point p2) {
		return Math
				.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
						* (p1.y - p2.y)));
	}

	public GameState solve() {


        // capture Image
        BufferedImage screenshot = ActionRobot.doScreenShot();

        // process image
        Vision vision = new Vision(screenshot);

        // find the slingshot
        Rectangle sling = vision.findSlingshotMBR();

        // confirm the slingshot
        while (sling == null && aRobot.getState() == GameState.PLAYING) {
            System.out
                    .println("No slingshot detected. Please remove pop up or zoom out");
            ActionRobot.fullyZoomOut();
            screenshot = ActionRobot.doScreenShot();
            vision = new Vision(screenshot);
            sling = vision.findSlingshotMBR();
        }
        // get all the pigs
        ABObject p, q, Top_point;
        Point pt;
        List<ABObject> pigs = vision.findPigsMBR();
        List<ABObject> blocks = vision.findBlocksMBR();

            /*for(int i=0;i<pigs.size();i++) {
            p=pigs.get(i);
            for(int j=i+1;j<pigs.size();j++) {
                q = pigs.get(j);
                System.out.println(p.getY()+" vvv "+q.getY());

                if(p.getY()>q.getY())
                {
                    System.out.println("++q++");
                    Top_point=q;
                    System.out.println(Top_point.getY()+"--------q ");
                }
                if(p.getY()<q.getY())
                {
                    System.out.println("++p++");
                    Top_point=p;
                    System.out.println(Top_point.getY()+"---------p");
                }
            }
        }*/
        GameState state = aRobot.getState();

        // if there is a sling, then play, otherwise just skip.
        if (sling != null) {
            BlocksDetails bd = new BlocksDetails();
            //  bd.Structure();

            Point TargetPoint;
            if (!pigs.isEmpty()) {

                Deciding_Trajectory DT = new Deciding_Trajectory();

                int flag = DT.Traj();
                double min = pigs.get(0).getY();
                int minid = 0;

                //
                //    System.out.println(pigs.size()+" pig size "+pigs.get(0).getY());
                // If height of structure is more than width of it then we hitting is done on 3/4 of the pig..!!
                if (flag == 1) {
                    System.out.println("------------Height is greater--------------");
                    int mid = (pigs.size() * 3) / 4;
                    minid = mid;
                    TargetPoint=DT.Penetration(minid);
                    if(TargetPoint.getX()==0 && TargetPoint.getY()==0)
                    {
                        ABObject pig = pigs.get(minid);
                        TargetPoint=pig.getCenter();
                    }
                    else
                    {   ABObject pig = pigs.get(minid);
                        TargetPoint=pig.getCenter();
                    }

                } else {// flag==2 vicevera
                    System.out.println("------------------Width is greater--------------");
                    for (int i = 1; i < pigs.size(); i++) {
                        if (pigs.get(i).getY() < min) {
                            min = pigs.get(i).getY();
                            minid = i;
                            }
                    }
                    TargetPoint=DT.Penetration(minid);
                    if(TargetPoint.getX()==0 && TargetPoint.getY()==0) {
                        ABObject pig = pigs.get(minid);
                        TargetPoint = pig.getCenter();
                    }
                    else
                    {   ABObject pig = pigs.get(minid);
                        TargetPoint=pig.getCenter();
                    }

                }
                    Point releasePoint = null;
                    Shot shot = new Shot();
                    int dx, dy;
                    {
                        // random pick up a pigrandomGenerator.nextInt(pigs.size())

                        Point _tpt = TargetPoint;// if the target is very close to before, randomly choose a

                        // point near it
                        if (prevTarget != null && distance(prevTarget, _tpt) < 10) {
                            double _angle = randomGenerator.nextDouble() * Math.PI * 2;
                            _tpt.x = _tpt.x + (int) (Math.cos(_angle) * 10);
                            _tpt.y = _tpt.y + (int) (Math.sin(_angle) * 10);
                            System.out.println("Randomly changing to " + _tpt);
                        }

                        prevTarget = new Point(_tpt.x, _tpt.y);

                        // estimate the trajectory
                        ArrayList<Point> pts = tp.estimateLaunchPoint(sling, _tpt);
                        for (int i = 0; i < pts.size(); i++) {
                            System.out.println(pts.get(i) + " points " + i);
                        }
                        // do a high shot when entering a level to find an accurate velocity
                        if (flag == 2 && pts.size() > 1) //if width is greater than the height of the structure then heighest trajectory will be used
                        {
                            releasePoint = pts.get(1);
                        } else if (flag == 1)  // if height is greater than the width than lowest trajcetory will be used..!!
                            releasePoint = pts.get(0);
				/*	else if (pts.size() == 2)
					{
						// randomly choose between the trajectories, with a 1 in
						// 6 chance of choosing the high one
						if (randomGenerator.nextInt(6) == 0)
							releasePoint = pts.get(1);
						else
							releasePoint = pts.get(0);
					}*/
                        else if (pts.isEmpty()) {
                            System.out.println("No release point found for the target");
                            System.out.println("Try a shot with 45 degree");
                            releasePoint = tp.findReleasePoint(sling, Math.PI / 4);
                        }

                        // Get the reference point
                        Point refPoint = tp.getReferencePoint(sling);


                        //Calculate the tapping time according the bird type
                        if (releasePoint != null) {
                            double releaseAngle = tp.getReleaseAngle(sling,
                                    releasePoint);
                            System.out.println("Release Point: " + releasePoint);
                            System.out.println("Release Angle: "
                                    + Math.toDegrees(releaseAngle));
                            int tapInterval = 0;
                            switch (aRobot.getBirdTypeOnSling()) {

                                case RedBird:
                                    tapInterval = 0;
                                    break;               // start of trajectory
                                case YellowBird:
                                    tapInterval = 65 + randomGenerator.nextInt(25);
                                    break; // 65-90% of the way
                                case WhiteBird:
                                    tapInterval = 70 + randomGenerator.nextInt(20);
                                    break; // 70-90% of the way
                                case BlackBird:
                                    tapInterval = 70 + randomGenerator.nextInt(20);
                                    break; // 70-90% of the way
                                case BlueBird:
                                    tapInterval = 65 + randomGenerator.nextInt(20);
                                    break; // 65-85% of the way
                                default:
                                    tapInterval = 60;
                            }

                            int tapTime = tp.getTapTime(sling, releasePoint, _tpt, tapInterval);
                            dx = (int) releasePoint.getX() - refPoint.x;
                            dy = (int) releasePoint.getY() - refPoint.y;
                            shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);
                        } else {
                            System.err.println("No Release Point Found");
                            return state;
                        }
                    }

                    // check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
                    {
                        ActionRobot.fullyZoomOut();
                        screenshot = ActionRobot.doScreenShot();
                        vision = new Vision(screenshot);
                        Rectangle _sling = vision.findSlingshotMBR();
                        if (_sling != null) {
                            double scale_diff = Math.pow((sling.width - _sling.width), 2) + Math.pow((sling.height - _sling.height), 2);
                            if (scale_diff < 25) {
                                if (dx < 0) {
                                    aRobot.cshoot(shot);
                                    state = aRobot.getState();
                                    if (state == GameState.PLAYING) {
                                        screenshot = ActionRobot.doScreenShot();
                                        vision = new Vision(screenshot);
                                        List<Point> traj = vision.findTrajPoints();
                                        tp.adjustTrajectory(traj, sling, releasePoint);
                                        firstShot = false;
                                    }
                                }
                            } else
                                System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
                        } else
                            System.out.println("no sling detected, can not execute the shot, will re-segement the image");
                    }

                }

            }

        return state;

    }


	public static void main(String args[]) {

       	NaiveAgent na = new NaiveAgent();
		if (args.length > 0) {
            na.currentLevel = Integer.parseInt(args[0]);
        }
        na.run();

	}
}
