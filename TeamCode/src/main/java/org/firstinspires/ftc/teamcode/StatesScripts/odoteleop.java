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

    // ===== 100 DEGREE SERVO TUNING =====

    private static final double TURRET_CENTER = 0.47; // adjust until forward is perfect
    private static final double SERVO_TOTAL_DEGREES = 100.0;
    private static final double TURRET_MAX_DEGREES = 45.0; // safe turret swing each side

    // ==================================


    // random odoaim vars
    private static int goalX = 144;
    private static int goalY = 144;
    private Pose pose;
    private double angleOffset;
    private double theta;
    private boolean isBlue;


    String setOdoVariables(boolean isBlue) {
        pose = follower.getPose();
        angleOffset = pose.getHeading();
        if(isBlue) {
            angleOffset += Math.toRadians(0);
            theta = Math.atan2(-Math.abs(goalY - pose.getY()), Math.abs(goalX - pose.getX())) - angleOffset; //make sure atan isnt negative for blue
        } else {
            angleOffset += Math.toRadians(15);
            theta = Math.atan2(Math.abs(goalY - pose.getY()), Math.abs(goalX - pose.getX())) - angleOffset; //make sure atan isnt negative for blue
        }

        theta = Math.toDegrees(theta);
        if(theta > 180) theta -= 360;
        if(theta < -180) theta += 360;
        return "theta: " + theta + ", angleoffset: " + Math.toDegrees(angleOffset);
    }

    public odoteleop(HardwareMap hardwareMap, boolean onBlue, boolean startingFar) {
        isBlue = onBlue;
        byte x = 0;
        byte y = 0;
        int rot = 0;
        if(isBlue && startingFar) {
            x = 56;
            y = 35;
            rot = 90;
        } else if(isBlue && !startingFar) {
            x = 36;
            y = 84;
            rot = 135;
        } else if(!isBlue && startingFar) {
            x = 90;
            y = 30;
            rot = 90;
        } else if(!isBlue && !startingFar) {

            rot = 45;
        }
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(x, y, Math.toRadians(rot))); // used to be 36, 84, 135
        hardware = new Hardware(hardwareMap);
    }

    public String odoAimTurret(boolean autoAim, boolean isBlue, boolean aimLimelight) {
        if(autoAim){
            follower.update();
            String printStuff = setOdoVariables(isBlue) + ", ";

            // -------- HEADING MATH --------
            // Limit to what turret is allowed to rotate
            //theta = Math.max(-TURRET_MAX_DEGREES, Math.min(TURRET_MAX_DEGREES, theta));

            // ------------------------------

            // Convert turret angle to servo position
            double servoScale = TURRET_MAX_DEGREES / SERVO_TOTAL_DEGREES;
            if(!isBlue) {
                servoScale *= 0.9;
            }

            double servoPos = TURRET_CENTER - (theta * servoScale / TURRET_MAX_DEGREES);

            servoPos = Math.max(0, Math.min(1, servoPos));
//            if(!isBlue) {
//                servoPos = 1 - servoPos;
//            }
            hardware.launcherTurn.setPosition(servoPos);
            if(aimLimelight) {
                hardware.limelightTurn.setPosition(1-(hardware.launcherTurn.getPosition()/2));
            }

            printStuff += "SP: " + servoPos;
            return printStuff;//"heading: " + follower.getHeading()
                    //+ ", pose: " + follower.getPose();
        } else {
            hardware.launcherTurn.setPosition(0.5f);
            return "im lazy";
        }
    }

    public double getOdoData(odoDataTypes odt) {

        follower.update();

        switch (odt) {
            case X:
                return follower.getPose().getX();

            case Y:
                return follower.getPose().getY();

            case HEADING:
                return follower.getHeading();
        }

        return 0;
    }

    public void resetOdoPos(boolean isBlue){
        if(isBlue) {
            follower.setPose(new Pose(16, 122, Math.toRadians(144)));
        } else {
            //follower.setPose(new Pose(138, 112, Math.toRadians(36)));
            follower.setPose(new Pose(144, 122, Math.toRadians(36)));

        }
    }

    public String getMotorPower(float powerOffset, Boolean isBlue) {
        setOdoVariables(isBlue);
        double xDist = goalX - getOdoData(odoDataTypes.X);
        double yDist = goalY - getOdoData(odoDataTypes.Y);
        double dist = yDist/Math.sin(theta);
        double power = dist * powerOffset;

        return "calculated pwr: " + power + ", dist: (" + xDist + ", " + yDist + "), total dist: " + dist + ", ";
    }
}
