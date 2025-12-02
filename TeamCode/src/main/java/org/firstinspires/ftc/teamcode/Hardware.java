package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hardware {
    // declare hardware
    public DcMotor frontLeft, frontRight, backLeft, backRight, launcherLeft, launcherRight, intake;
    public CRServo launcherTurn, limelightTurn;
    public Servo sorter, outtakeTransfer;
    public Limelight3A limelight;
    public ColorSensor colorSensor;
    int red, green, blue;
    float[] hsvValues = new float[3];
    boolean nextPos = true;


    // initialize flags
    public boolean intaking = false;
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;

    // CONSTRUCTOR
    // assign hardware
    public Hardware(HardwareMap hardwareMap) {
        // e0 = expansion hub 0, c2 = control hub 2
        frontLeft = hardwareMap.get(DcMotor.class, "fl"); // c0
        frontRight = hardwareMap.get(DcMotor.class, "fr"); // e0
        backLeft = hardwareMap.get(DcMotor.class, "bl"); // c1
        backRight = hardwareMap.get(DcMotor.class, "br"); // e1

        launcherLeft = hardwareMap.get(DcMotor.class, "launcherL"); //
        launcherRight = hardwareMap.get(DcMotor.class, "launcherR"); //c2
        launcherTurn = hardwareMap.get(CRServo.class, "launcherT"); //c0
        limelightTurn = hardwareMap.get(CRServo.class, "limelightT"); //c0

        intake = hardwareMap.get(DcMotor.class, "intake"); // e2

        sorter = hardwareMap.get(Servo.class, "sorter");

        outtakeTransfer = hardwareMap.get(Servo.class, "transfer");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        colorSensor = hardwareMap.get(ColorSensor.class, "colorSens");

        launcherRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void tryIntake(boolean button) {
        if (button && !intaking) {
            intake.setPower(1);
            // set sorter position
            intaking = true;
        }
        if (intaking) {
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
        if (button && !launchingPurple) {
            launchingGreen = false;
            launchingPurple = true;
            launcherLeft.setPower(1);
            launcherRight.setPower(1);
        }
        if (launchingPurple) {
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
        if (button && !launchingGreen) {
            launchingPurple = false;
            launchingGreen = true;
            launcherLeft.setPower(1);
            launcherRight.setPower(1);
        }
        if (launchingGreen) {
            // treat this as a loop
            /*
            Check for the following:
              - correct sorter position
              - correct robot position
            */
        }
    }

    public void stopLaunch() {
        launcherLeft.setPower(0);
        launcherRight.setPower(0);
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

    public void detectFilled() {

        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();
        Color.RGBToHSV(red, green, blue, hsvValues);

        boolean validColor = hsvValues[1] > 0.55; // make sure saturation is high so we don't detect gray or smth
        float hue = hsvValues[0];
        if (hue > 220 && hue < 230 && validColor && nextPos) { // purple
            nextPos = false;
            // set current position to purple
            // move to new empty position as long as not all three are filled
        } else if(hue > 155 && hue < 175 && validColor && nextPos) { // green
            nextPos = false;
            // set current position to green
            // move to new empty position as long as not all three are filled
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