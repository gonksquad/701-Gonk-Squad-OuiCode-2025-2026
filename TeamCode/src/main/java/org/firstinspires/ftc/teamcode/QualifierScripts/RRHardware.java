package org.firstinspires.ftc. teamcode. QualifierScripts;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.limelightvision. Limelight3A;
import com. qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore. hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com. qualcomm.robotcore.hardware.Servo;
import com. qualcomm.robotcore.util.ElapsedTime;


public class RRHardware{
    private HardwareMap hwMap; // Store hardware map

    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcherLeft, launcherRight;
    public Servo sorter, outtakeTransferLeft, outtakeTransferRight, limelightTurn, launcherTurn;
    public Limelight3A limelight;
    public ColorSensor colorSensor;

    public final double[] intakePos = {1.0, 0.6, 0.2};//*/{0.4, 0.0, 0.8};// sorter servo positions for outtaking
    public final double[] outtakePos = {0.4, 0.0, 0.8};//*/{1.0, 0.6, 0.2}; // sorter servo positions for intaking
    public byte[] sorterContents = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green
    public double[] liftPos = {0.6, 0.2};
    public int currentPos = 0; // 0-2
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;

    public RRHardware(HardwareMap hardwareMap) {
        this.hwMap = hardwareMap;
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        launcherLeft = hardwareMap.get(DcMotorEx.class, "launcherL");
        launcherRight = hardwareMap.get(DcMotorEx.class, "launcherR");

        launcherTurn = hardwareMap.get(Servo.class, "launcherYaw");
        limelightTurn = hardwareMap.get(Servo.class, "limeservo");

        intake = hardwareMap.get(DcMotor.class, "intake"); // e2
        sorter = hardwareMap.get(Servo.class, "sorter"); //c2
        outtakeTransferLeft = hardwareMap.get(Servo.class, "liftLeft"); // c5
        outtakeTransferRight = hardwareMap.get(Servo.class, "liftRight"); // c5
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSens");


        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherRight.setDirection(DcMotorSimple.Direction.REVERSE);

        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        double x = 0.75;
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void doDrive(double speedX, double speedY, double speedYaw) {
        double pwrY = speedY;
        double pwrX = speedX;
        double pwrYaw = speedYaw;

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

//    public void doIntake(byte sorterPos) {
//        sorter.setPosition(intakePos[sorterPos]);
//        stopIntake();
//        sleep(1000);
//        intake.setPower(1);
//        intakeTimer.reset();
//        while(intakeTimer.milliseconds() < 2000 && detectFilled() == 0) {
//            //telemetry.addLine("Looking for ball");
//        }
//        sorterContents[sorterPos] = detectFilled();
//
//        if (!intaking) {
//            sorter.setPosition(intakePos[sorterPos]);
//            stopLaunch();
//
//            sleep(1000);
//            intake.setPower(1);
//
//            sleep(1000);
//
//           // sorter.setPosition(outtakePos[2]);
//            intakeTimer.reset();
//        }
//        if (intaking && intakeTimer.milliseconds() > 500) {
//            intaking = false;
//            intake.setPower(0);
//        }
//    }

    public void dontFallOut() {
        sorter.setPosition(outtakePos[0]);
        intake.setPower(0);
        //intaking = false;
    }

    public void stopIntake() {
        intake.setPower(0);
    }

    public void startIntake() {
        intake.setPower(1);
    }

//    public void tryIntake(boolean button) {
//        // check if intake button is being pressed and not currently intaking
//        if (button && !intaking) {
//            // set sorter position
//            for (int i = 0; i < 3; i++) {
//                //if spot is empty, set to that pos
//                if (sorterContents[i] == 0) {
//                    stopLaunch();
//
//                    intaking = true;
//                    intake.setPower(1);
//
//                    sorter.setPosition(intakePos[i]);
//                    currentPos = i;
//                    intakeTimer.reset();
//
//                    break;
//                }
//            }
//        }
//        if (intaking && intakeTimer.milliseconds() > 1000) {
//            byte guess = detectFilled();
//            if (guess == 0) return;
//            //set current sorter pos to color-sensor-detected color
//            sorterContents[currentPos] = guess;
//            currentPos = (currentPos + 2) % 3;
//            //change to outtake
//            sorter.setPosition(outtakePos[currentPos]);
//
//
//            // treat this as a loop
//            // try to go to the artifact (maybe split int tryIntakePurple and tryIntakeGreen)
//            // check if intaking was completed -> store color at position
//        }
//
//    }

    public void stopLaunch() {
        launchingPurple = false;
        launchingGreen = false;
    }


    // NOTE: if the color is not in the
    @SuppressLint("SuspiciousIndentation")
    public void tryLaunch(int color, int tps) { // 1=purple, 2=green, other=any color
        for (int i = currentPos; i < currentPos + 3; i++) { // for every sorter position starting at the current one
            if (sorterContents[i % 3] != 0 && (sorterContents[i % 3] == color || color == 0)) {
                stopLaunch();

                launchingPurple = sorterContents[i % 3] == 1;
                launchingGreen = sorterContents[i % 3] == 2;

                currentPos = i % 3;
                sorter.setPosition(outtakePos[currentPos]);

                break;
            }
        }
    }
}