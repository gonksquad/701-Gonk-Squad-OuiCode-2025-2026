package org.firstinspires.ftc.teamcode.StatesScripts;

import org.firstinspires.ftc.teamcode.MiscScripts.Hardware;
import org.firstinspires.ftc.teamcode.WorldsScripts.AutoToTeleData;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import com.qualcomm.robotcore.hardware.HardwareMap;

import com.acmerobotics.roadrunner.Pose2d;

public class odoteleop {
    Hardware hardware;

    // ===== 100 DEGREE SERVO TUNING =====

    private static double TURRET_CENTER = 0.47; // adjust until forward is perfect
    private static double SERVO_TOTAL_DEGREES = 100.0;
    private static double TURRET_MAX_DEGREES = 45.0; // safe turret swing each side

    // ==================================


    // random odoaim vars
    private static int goalX = 144;
    private static int goalY = 144;
    private double angleOffset;
    private double theta;
    private boolean isBlue;
    private double currentX;
    private double currentY;
    private double currentHeading;

    // runs every update
    String setOdoVariables(boolean isBlue) {

        Pose2d currentPose = hardware.getOdoPose();

        currentX = currentPose.position.x;
        currentY = currentPose.position.y;
        currentHeading = currentPose.heading.toDouble();

        if(isBlue) {
            TURRET_CENTER = 0.5;
            goalX = 0;
        } else {
            goalX = 144;
        }
        angleOffset = currentHeading;
        if(isBlue) {
            theta = Math.atan2(goalY - currentY, goalX - currentX) - angleOffset; //make sure atan isnt negative for blue
        } else {
            angleOffset += Math.toRadians(12.5);
            theta = Math.atan2(Math.abs(goalY - currentY), Math.abs(goalX - currentX)) - angleOffset; //make sure atan isnt negative for blue
        }

        theta = Math.toDegrees(theta);
        if(theta > 180) theta -= 360;
        if(theta < -180) theta += 360;
        return "theta: " + theta + ", angleoffset: " + Math.toDegrees(angleOffset);
    }

    public odoteleop(HardwareMap hardwareMap, boolean onBlue, boolean startingFar) {
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
        hardware = new Hardware(hardwareMap);
    }

    public String odoAimTurret(boolean autoAim, boolean isBlue, boolean aimLimelight) {
        if (autoAim) {
            String printStuff = setOdoVariables(isBlue) + ", ";
            theta = Math.max(-TURRET_MAX_DEGREES, Math.min(TURRET_MAX_DEGREES, theta));


            // Convert turret angle to servo position
            double servoScale = TURRET_MAX_DEGREES / SERVO_TOTAL_DEGREES;
            if (!isBlue) {
                //servoScale *= 0.9;
            }

            double servoPos = TURRET_CENTER - (theta * servoScale / TURRET_MAX_DEGREES);

            servoPos = Math.max(0, Math.min(1, servoPos));
//            if(!isBlue) {
//                servoPos = 1 - servoPos;
//            }
            hardware.launcherTurn.setPosition(servoPos);

            printStuff += "SP: " + servoPos;
            printStuff += "Pose: (" + currentX + ", " + currentY + ", " + currentHeading + ")";
            return printStuff;//"heading: " + follower.getHeading()
        } else {
            hardware.launcherTurn.setPosition(0.5f);
            return "no autoaim";
        }
    }



    public void resetOdoPos(boolean isBlue){
        if(isBlue) {
            //set to blue goal
        } else {
            //set to red goal
        }
    }

    public String getMotorPower(float powerOffset, Boolean isBlue) {
        setOdoVariables(isBlue);
        double xDist = goalX - currentX;
        double yDist = goalY - currentY;
        double dist = yDist/Math.sin(theta);
        double power = dist * powerOffset;

        return "calculated pwr: " + power + ", dist: (" + xDist + ", " + yDist + "), total dist: " + dist + ", ";
    }
}
