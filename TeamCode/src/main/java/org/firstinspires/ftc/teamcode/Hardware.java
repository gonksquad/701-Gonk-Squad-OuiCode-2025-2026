package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Hardware {
    // declare hardware
    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcherLeft, launcherRight;
    public CRServo launcherTurn, limelightTurn;
    public Servo sorter, outtakeTransfer;
    public Limelight3A limelight;
    public ColorSensor colorSensor;
    int red, green, blue;
    float[] hsvValues = new float[3];
    boolean nextPos = true;
    // TODO: Set correct intake and outtake positions
    public final double[] outtakePos = {0.254, 0.423, 0.085}; // sorter servo positions for outtaking
    public final double[] intakePos = {0.0, 0.169, 0.338}; // sorter servo positions for intaking
    byte[] sorterPos = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green
    int currentPos = 0; // intake is 0-2 outtake is 3-5
    ElapsedTime changePosTimer = new ElapsedTime();

    // initialize flags
    public boolean intaking = false;
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;

    // CONSTRUCTOR
    // assign hardware
    public Hardware(HardwareMap hardwareMap) {
        // e0 = expansion hub 0, c2 = control hub 2
        frontLeft = hardwareMap.get(DcMotor.class, "fl"); // c1
        frontRight = hardwareMap.get(DcMotor.class, "fr"); // e1
        backLeft = hardwareMap.get(DcMotor.class, "bl"); // c2
        backRight = hardwareMap.get(DcMotor.class, "br"); // e2

        launcherLeft = hardwareMap.get(DcMotorEx.class, "launcherL"); //e0
        launcherRight = hardwareMap.get(DcMotorEx.class, "launcherR"); //c0
        launcherTurn = hardwareMap.get(CRServo.class, "launcherYaw"); //c1
        limelightTurn = hardwareMap.get(CRServo.class, "limeservo"); //c0

        intake = hardwareMap.get(DcMotor.class, "intake"); // e2
        sorter = hardwareMap.get(Servo.class, "sorter"); //c2
        outtakeTransfer = hardwareMap.get(Servo.class, "lift"); // c5
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSens");


        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void tryIntake(boolean button) {
        if (button && !intaking) {
            // set sorter position
            boolean sorterSuccess = false;
            for (int i = 0; i < 3; i++) {
                if (sorterPos[i] == 0) {
                    sorter.setPosition(intakePos[i]);
                    currentPos = i;
                    sorterSuccess = true;
                    break;
                }
            }
            if (sorterSuccess) {
                intaking = true;
                intake.setPower(1);
            }
        }
        if (intaking) {
            //detectFilled();
            // treat this as a loop
            // try to go to the artifact (maybe split int tryIntakePurple and tryIntakeGreen)
            // check if intaking was completed -> store color at position
        }
    }

    public void stopIntake() {
        intake.setPower(0);
        intaking = false;
    }

    // NOTE: if the color is not in the
    public void tryLaunchPurple(boolean button) {
        if (button && !launchingPurple) { // on first button press

            // check if sorter has purple
            boolean sorterSuccess = false;
            for (int i = getCurrentPos(); i < sorterPos.length + getCurrentPos(); i = (i + 1) % 3) { // for every sorter position starting at the current one
                if (sorterPos[i] == 1) { // if the position has a purple inside of it
                    sorterSuccess = true;
                    sorter.setPosition(outtakePos[i]);
                    currentPos = i + 3;
                    changePosTimer.reset();
                    break;
                }
            }

            if (sorterSuccess) {
                launchingGreen = false;
                launchingPurple = true;
                // TODO: set velocity based on apriltag distance
                launcherLeft.setVelocity(6000);
                launcherRight.setVelocity(6000);
            }
        }
        if (launchingPurple) {
//            if(changePosTimer.milliseconds() >= 750 && sorterPos[getCurrentPos()] == 1) {
//                outtakeTransfer.setPosition(1);
//                sorterPos[getCurrentPos()] = 0;
//            }
//            if(changePosTimer.milliseconds() >= 1250){
//                outtakeTransfer.setPosition(0);
//            }
            // treat this as a loop
            /*
            Check for the following:
              - sorter position
              - robot position
              - launcher speed?
            if all are correct, push artifact into launcher
            */
        }
    }

    public void tryLaunchGreen(boolean button) {
        if (button && !launchingGreen) { // on first button press

            // check if sorter has green
            boolean sorterSuccess = false;
            for (int i = getCurrentPos(); i < sorterPos.length + getCurrentPos(); i = (i + 1) % 3) { // for every sorter position starting at the current one
                if (sorterPos[i] == 2) { // if the position has a green inside of it
                    sorterSuccess = true;
                    sorter.setPosition(outtakePos[i]);
                    changePosTimer.reset();
                    break;
                }
            }

            if (sorterSuccess) {
                launchingPurple = false;
                launchingGreen = true;
                // TODO: set velocity based on apriltag distance
                launcherLeft.setVelocity(6000);
                launcherRight.setVelocity(6000);
            }
        }
        if (launchingGreen) {
//            if(changePosTimer.milliseconds() >= 750 && sorterPos[getCurrentPos()] == 1) {
//                outtakeTransfer.setPosition(1);
//                sorterPos[getCurrentPos()] = 0;
//            }
//            if(changePosTimer.milliseconds() >= 1250){
//                outtakeTransfer.setPosition(0);
//            }
            // treat this as a loop
            /*
            Check for the following:
              - sorter position
              - robot position
              - launcher speed?
            if all are correct, push artifact into launcher
            */
        }
    }

    public void stopLaunch() {
        launcherLeft.setVelocity(0);
        launcherRight.setVelocity(0);
        launchingPurple = false;
        launchingGreen = false;
    }

    public void doDrive(double ctrlX, double ctrlY, double ctrlYaw) {
        if (Math.abs(ctrlY) < 0.1) {
            ctrlY = 0;
        }
        if (Math.abs(ctrlX) < 0.1) {
            ctrlX = 0;
        }
        if (Math.abs(ctrlYaw) < 0.1) {
            ctrlYaw = 0;
        } else if (Math.abs(ctrlYaw) < 0.5) {
            ctrlYaw = Math.signum(ctrlYaw) * 0.5;
        }

        double flPwr = ctrlY - ctrlYaw - ctrlX;
        double frPwr = ctrlY + ctrlYaw + ctrlX;
        double blPwr = ctrlY - ctrlYaw + ctrlX;
        double brPwr = ctrlY + ctrlYaw - ctrlX;

        double denominator = Math.max(Math.max(Math.max(flPwr, frPwr), Math.max(blPwr, brPwr)), 1);

        frontLeft.setPower(flPwr / denominator);
        frontRight.setPower(frPwr / denominator);
        backLeft.setPower(blPwr / denominator);
        backRight.setPower(brPwr / denominator);
    }

    public void doDrive(double ctrlX, double ctrlY, double ctrlYaw, double speedX, double speedY, double speedYaw) {
        double pwrY = ctrlY * speedY;
        double pwrX = ctrlX * speedX;
        double pwrYaw = ctrlYaw * speedYaw;

        if (Math.abs(pwrY) < 0.1) {
            pwrY = 0;
        }
        if (Math.abs(pwrX) < 0.1) {
            pwrX = 0;
        }
        if (Math.abs(pwrYaw) < 0.1) {
            pwrYaw = 0;
        } else if (Math.abs(pwrYaw) < 0.5) {
            pwrYaw = Math.signum(pwrYaw) * 0.5;
        }

        double flPwr = pwrY - pwrYaw - pwrX;
        double frPwr = pwrY + pwrYaw + pwrX;
        double blPwr = pwrY - pwrYaw + pwrX;
        double brPwr = pwrY + pwrYaw - pwrX;

        double denominator = Math.max(Math.max(Math.max(flPwr, frPwr), Math.max(blPwr, brPwr)), 1);

        frontLeft.setPower(flPwr / denominator);
        frontRight.setPower(frPwr / denominator);
        backLeft.setPower(blPwr / denominator);
        backRight.setPower(brPwr / denominator);
    }

    public int getCurrentPos() {
        for (int i = 0; i < sorterPos.length; i++) {
            if (sorter.getPosition() == intakePos[i] || sorter.getPosition() == outtakePos[i]) {
                return i;
            }
        }
        return -1;
    }

    public void detectFilled() {

        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();
        Color.RGBToHSV(red, green, blue, hsvValues);

        boolean validColor = hsvValues[1] > 0.5; // make sure saturation is high so we don't detect gray or smth
        float hue = hsvValues[0];
        if (hue > 260 && hue < 300 && validColor && nextPos && changePosTimer.milliseconds() > 500) { // purple
            nextPos = false;
            sorterPos[getCurrentPos()] = 1; // set current position to purple
            changePosTimer.reset();
        } else if(hue > 80 && hue < 160 && validColor && nextPos) { // green
            nextPos = false;
            sorterPos[getCurrentPos()] = 2; // set current position to green
            changePosTimer.reset();
        } else {
            nextPos = true;
        }
    }

    public void aimTurret(String side) {
        switch (side) {
            case "blue":
                limelight.pipelineSwitch(0); // pretend this detects april tag id 20
                break;
            case "red":
                limelight.pipelineSwitch(4); // pretend this detects april tag id 24
                break;
        }

        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            double tx = result.getTx();
            double ratio = 90d/270d; // approximate teeth of servo to teeth of launcher
            if(tx > 4f) { // tag is on the right
                launcherTurn.setPower(0.8 * ratio);
                limelightTurn.setPower(0.8);
            } else if(tx < -4) { // tag is on the left
                launcherTurn.setPower(-0.8 * ratio);
                limelightTurn.setPower(-0.8);
            } else { // tag is within left and right bounds
                launcherTurn.setPower(0);
            }
        }
    }
}