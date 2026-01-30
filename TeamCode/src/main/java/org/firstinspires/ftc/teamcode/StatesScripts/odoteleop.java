package org.firstinspires.ftc.teamcode.StatesScripts;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class odoteleop {
    Hardware hardware;
    private Follower follower;
    double lastTheta;

    public enum odoDataTypes {
        X,
        Y,
        HEADING
    }
    public odoteleop(HardwareMap hardwareMap) {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(8.5, 7.625, 0)); //remove later
        hardware = new Hardware(hardwareMap);
    }
    //private ElapsedTime runtime = new ElapsedTime();

    public String odoAimTurret(boolean isBlue) {
        follower.update();
        int goalX = (isBlue) ? 0 : 144;
        int goalY = 144;
        //double angleOffset = (isBlue) ? 0 : -90;
        Pose pose = follower.getPose();
        double angleOffset = pose.getHeading();
        double theta = Math.round(180*(angleOffset + Math.atan2(goalY-pose.getY(), goalX-pose.getX()))/Math.PI % 360); //angle to the goal from 0-360
        //hardware.launcherTurn.setPower((theta - lastTheta)/10); for cr servo
        hardware.limelightTurn.setPosition((theta-90)/180); //assuming that  0 = 90deg right, 0.5 = forawrd, 1 = 90deg left
        //lastTheta = theta; for cr servo
        //return to add to telemtry bc this script deosnt have telemetry
        return "totalheading: " + follower.getTotalHeading() + ", heading:" + follower.getHeading() + ", pose:" + follower.getPose();
        //return theta + "=theta, " + 180*angleOffset/Math.PI + "=angleoffset, " + 180*Math.atan2(goalY-pose.getY(), goalX-pose.getX())/Math.PI + "=atan2func" + hardware.launcherTurn.getPosition() + "=launchturnpos";
    }

    public double getOdoData(odoDataTypes odt) {
        follower.update();
        switch(odt) {
            case X:
                return follower.getPose().getX();
            case Y:
                return follower.getPose().getY();
            case HEADING:
                return follower.getTotalHeading();
        }
        return 0;
    }

}