package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

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
    public final double[] intakePos = {1.0, 0.6, 0.2}; // sorter servo positions for outtaking
    public final double[] outtakePos = {0.4, 0.0, 0.8}; // sorter servo positions for intaking
    public double sorterOffset = 0d;
    public byte[] sorterPos = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green
    public int currentPos = 0; // 0-2
    int targetTps = 0; // protect launcher tps from override
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

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


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

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void tryIntake(boolean button) {
        // check if intake button is being pressed and not currently intaking
        if (button && !intaking) {
            // set sorter position
            for (int i = 0; i < 3; i++) {
                //if spot is empty, set to that pos
                if (sorterPos[i] == 0) {
                    stopLaunch();

                    intaking = true;
                    intake.setPower(1);

                    sorter.setPosition(intakePos[i]);
                    currentPos = i;
                    changePosTimer.reset();

                    break;
                }
            }
        }
        if (intaking && changePosTimer.milliseconds() > 1000) {
            byte guess = detectFilled();
            if (guess == 0) return;
            //set current sorter pos to color-sensor-detected color
            sorterPos[currentPos] = guess;
            currentPos = (currentPos + 2) % 3;
            //change to outtake
            sorter.setPosition(outtakePos[currentPos]);
            stopIntake();
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
    public void tryLaunch(boolean button, int color, int tps) { // 1=purple, 2=green, other=any color
        if (button && !(launchingPurple || launchingGreen)) { // on first button press
            // check if sorter has purple
            for (int i = currentPos; i < currentPos + 3; i++) { // for every sorter position starting at the current one
                //if position has purple
                if ((sorterPos[i % 3] == (color==2? 2 : 1) || (sorterPos[i%3] != 0 && color==0))) {
                    stopIntake();

                    launchingGreen = (color == 2 || color == 0);
                    launchingPurple = (color == 1 || color == 0);

                    // TODO: set velocity based on apriltag distance
                    launcherLeft.setVelocity(tps + 20);
                    launcherRight.setVelocity(tps + 20);

                    targetTps = tps;

                    currentPos = i % 3;
                    sorter.setPosition(outtakePos[currentPos]);

                    break;
                }
            }
        }
        if ((launchingPurple || launchingGreen) && launcherLeft.getVelocity() > targetTps && outtakeTransfer.getPosition() != 0.2) {
            outtakeTransfer.setPosition(0.2);
            sorterPos[currentPos] = 0;
            changePosTimer.reset();
        }
        if (outtakeTransfer.getPosition() == 0.2 && changePosTimer.milliseconds() > 1000) {
            stopLaunch();
        }
    }

//    public void tryLaunchGreen(boolean button) {
//        if (button && !launchingGreen) { // on first button press
//
//            // check if sorter has green
//            boolean sorterSuccess = false;
//            for (int i = getCurrentPos(); i < sorterPos.length + getCurrentPos(); i = (i + 1) % 3) { // for every sorter position starting at the current one
//                if (sorterPos[i] == 2) { // if the position has a green inside of it
//                    sorterSuccess = true;
//                    sorter.setPosition((outtakePos[i] + sorterOffset) % 1d);
//                    changePosTimer.reset();
//                    break;
//                }
//            }
//
//            if (sorterSuccess) {
//                launchingPurple = false;
//                launchingGreen = true;
//                // TODO: set velocity based on apriltag distance
//                launcherLeft.setVelocity(6000);
//                launcherRight.setVelocity(6000);
//            }
//        }
//        if (launchingGreen) {
//            if(changePosTimer.milliseconds() >= 750 && sorterPos[getCurrentPos()] == 1) {
//                outtakeTransfer.setPosition(1);
//                sorterPos[getCurrentPos()] = 0;
//            }
//            if(changePosTimer.milliseconds() >= 1250){
//                outtakeTransfer.setPosition(0);
//            }
//            // treat this as a loop
//            /*
//            Check for the following:
//              - sorter position
//              - robot position
//              - launcher speed?
//            if all are correct, push artifact into launcher
//            */
//        }
//    }

    public void stopLaunch() {
        outtakeTransfer.setPosition(0.9);
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

    public byte detectFilled() {
        float red = colorSensor.red();
        float green = colorSensor.green();
        float blue = colorSensor.blue();

        float multiplier = 255f / Math.max(Math.max(red, green), Math.max(blue, 255f));
        green *= multiplier;
        blue *= multiplier;

        byte guess = 0;
        if (blue > 200 && blue > green) {
            //likely purple artifact
            guess = 1;
        } else if (green > 200 && green > blue) {
            //likely green artifact
            guess = 2;
        }

        return guess;
    }

    public void setSide(String side) {
        switch (side) {
            case "blue":
                limelight.pipelineSwitch(6); // pretend this detects april tag id 20
                break;
            case "red":
                limelight.pipelineSwitch(5); // pretend this detects april tag id 24
                break;
        }
    }
    public void autoAimTurret() {
        LLResult result = limelight.getLatestResult();
        double id = -1;
        //doesn't work for detecting if its true and it doesnt cause
        //errors not having it so if it aint broke
        //if(true){//result != null && result.isValid()) {
        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

        for (LLResultTypes.FiducialResult fiducial : fiducials) {
            id = fiducial.getTargetXDegrees();
        }
        launcherTurn.setPower(3*0.03*result.getTx());
        limelightTurn.setPower(0.03*result.getTx());
    }
}
