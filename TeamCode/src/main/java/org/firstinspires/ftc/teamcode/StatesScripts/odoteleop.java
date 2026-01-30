package org.firstinspires.ftc.teamcode.StatesScripts;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class odoteleop {
    Hardware hardware;
    double lastTheta;

    public enum odoDataTypes {
        X,
        Y,
        HEADING
    }
    public odoteleop(HardwareMap hardwareMap) {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0, 0, 0));
        hardware = new Hardware(hardwareMap);
    }

    private final Follower follower;
    //private ElapsedTime runtime = new ElapsedTime();

    public String odoAimTurret(boolean isBlue) {
        follower.update();
        int goalX = (isBlue) ? 0 : 144;
        int goalY = 144;
        double angleOffset = (isBlue) ? 0 : -90;
        angleOffset += getOdoData(odoDataTypes.HEADING);
        Pose pose = follower.getPose();
        double theta = Math.round(180*(angleOffset + Math.atan2(goalY-pose.getY(), goalX-pose.getX()))/Math.PI % 360); //angle to the goal from 0-360
        //hardware.launcherTurn.setPower((theta - lastTheta)/10); for cr servo
        hardware.launcherTurn.setPosition(-0.5+theta/180); //assuming that  0 = 90deg right, 0.5 = forawrd, 1 = 90deg left
        lastTheta = theta;
        return theta + "=theta, " + 180*angleOffset/Math.PI + "=angleoffset, " + 180*Math.atan2(goalY-pose.getY(), goalX-pose.getX())/Math.PI + "=atan2func" + hardware.launcherTurn.getPosition() + "=launchturnpos";
    }

    public double getOdoData(odoDataTypes odt) {
        follower.update();
        switch(odt) {
            case X:
                return follower.getPose().getX();
            case Y:
                return follower.getPose().getY();
            case HEADING:
                return follower.getPose().getHeading();
        }
        return 0;
    }

}