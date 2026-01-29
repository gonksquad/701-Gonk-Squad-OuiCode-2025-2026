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

    public void odoAimTurret(boolean isBlue) {
        follower.update();
        int goalX = (isBlue) ? 0 : 144;
        int angleOffset = (isBlue) ? 90 : 0;
        Pose pose = follower.getPose();
        double theta = (angleOffset + Math.atan2(Math.abs(goalX-pose.getX()), Math.abs(144-pose.getY()))) % 360; //angle to the goal from 0-360
    //    hardware.launcherTurn.setPower((theta - lastTheta)); SHOULDNT BE COMMENTED
        lastTheta = theta;
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