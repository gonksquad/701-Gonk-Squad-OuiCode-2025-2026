package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hardware {
    // declare hardware
    public DcMotor frontLeft, frontRight, backLeft, backRight, launcherLeft, launcherRight, intake;
    public Servo sorter, launcherTurn, shoulder, elbow, wrist;

    // initialize flags
    public boolean intaking = false;
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;

    // CONSTRUCTOR
    // assign hardware
    public Hardware(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        launcherLeft = hardwareMap.get(DcMotor.class, "launcherL");
        launcherRight = hardwareMap.get(DcMotor.class, "launcherR");
        launcherTurn = hardwareMap.get(Servo.class, "launcherT");

        intake = hardwareMap.get(DcMotor.class, "intake");

        sorter = hardwareMap.get(Servo.class, "sorter");

        shoulder = hardwareMap.get(Servo.class, "shoulder");
        elbow = hardwareMap.get(Servo.class, "elbow");
        wrist = hardwareMap.get(Servo.class, "wrist");

        launcherRight.setDirection(DcMotorSimple.Direction.REVERSE);
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
}