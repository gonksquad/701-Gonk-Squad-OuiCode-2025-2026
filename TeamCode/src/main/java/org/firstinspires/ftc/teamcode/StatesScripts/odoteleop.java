package org.firstinspires.ftc.teamcode.StatesScripts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import org.firstinspires.ftc.teamcode.WorldsScripts.AutoToTeleData;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import com.acmerobotics.roadrunner.Pose2d;

public class odoteleop {
    // ===== 100 DEGREE SERVO TUNING =====

   // private static double TURRET_CENTER = 0.47; // adjust until forward is perfect
    //private static double SERVO_TOTAL_DEGREES = 100.0;
   // private static double TURRET_MAX_DEGREES = 45.0; // safe turret swing each side

    // ==================================


    // random odoaim vars
    private static int goalX;
    private final int goalY = 144;
    private double angleOffset;
    private double theta;
    private boolean isBlue;
    private double currentX;
    private double currentY;
    private double currentHeading;

    // runs every update
    String setOdoVariables(boolean isBlue, Pose2d currentPose) {

        //they're reversed for some reason
        currentX = -currentPose.position.y;
        currentY = currentPose.position.x;
        angleOffset = currentPose.heading.toDouble();

        goalX = (isBlue) ? -9 : 135;
        /*if(isBlue) {
            theta = Math.atan2(goalY - currentY, goalX - currentX) - angleOffset; //make sure atan isnt negative for blue
        } else {
            theta = Math.atan2(goalY - currentY, goalX - currentX) - angleOffset; //make sure atan isnt negative for blue
        }*/

        theta = Math.toDegrees(Math.atan2(goalY-currentY, goalX-currentX) - angleOffset);
        if(theta > 180) theta -= 360;
        if(theta < -180) theta += 360;
        return "angle to goal: " + theta + ", angleoffset: " + Math.toDegrees(angleOffset);
    }

    public odoteleop(boolean onBlue, boolean startingFar) {
        isBlue = onBlue;
        int x = 0;
        int y = 0;
        int rot = 0;
        if(isBlue && startingFar) {
            x = 56;
            y = 35;
            rot = 90;
        } else if(isBlue && !startingFar) {
            x = 32;
            y = 85;
            rot = 144;
        } else if(!isBlue && startingFar) {
            x = 108;
            y = 27;
            rot = 90;
        } else if(!isBlue && !startingFar) {
            x = 132;
            y = 83;
            rot = 36;
        }
    }

    public String odoAimTurret(boolean autoAim, boolean isBlue, Pose2d currentPose, Servo launcherTurn) {
        if (autoAim) {
            //set the odo variables and add stuff to print
            String printStuff = setOdoVariables(isBlue, currentPose) + ", ";

            //0.31 = 0deg, 0.43 = 45 deg, 0.55 = 90deg
            double servoPos = (0.12f*theta/45) + 0.31f;

            //set launcher pos
            launcherTurn.setPosition(servoPos);

            printStuff += " SP: " + servoPos;
            printStuff += " Pose: (" + Math.round(currentX) + ", " + Math.round(currentY) + ", " + Math.round(currentHeading) + ")";
            return printStuff;//"heading: " + follower.getHeading()
        } else {
            launcherTurn.setPosition(0.5f);
            return "no autoaim";
        }
    }

    public double getGoalDistance(boolean isBlue, Pose2d currentPose) {
        setOdoVariables(isBlue, currentPose);
        return Math.sqrt(Math.pow(goalX-currentX, 2)+Math.pow(goalY-currentY, 2));
    }



    public void resetOdoPos(boolean isBlue){
        if(isBlue) {
            //set to blue goal
        } else {
            //set to red goal
        }
    }

    /*public String getMotorPower(float powerOffset, Boolean isBlue, Pose2d currentPose) {
        setOdoVariables(isBlue, currentPose);
        double xDist = goalX - currentX;
        double yDist = goalY - currentY;
        double dist = yDist/Math.sin(theta);
        double power = dist * powerOffset;

        return "calculated pwr: " + power + ", dist: (" + xDist + ", " + yDist + "), total dist: " + dist + ", ";
    }*/
}
