package org.firstinspires.ftc.teamcode.scrimmagecode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ScrimmageHardware {
    // declare hardware
    public DcMotor frontLeft, frontRight, backLeft, backRight, launcherLeft, launcherRight, intake;
    public Servo launcherPush;
    public ElapsedTime transferTime = new ElapsedTime();

    // CONSTRUCTOR
    // assign hardware
    public ScrimmageHardware(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        launcherLeft = hardwareMap.get(DcMotor.class, "out1");
        launcherRight = hardwareMap.get(DcMotor.class, "out2");
        launcherPush = hardwareMap.get(Servo.class, "servo");

        intake = hardwareMap.get(DcMotor.class, "in");

        launcherRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void doIntake(boolean pull, boolean push) {
        if (pull) {
            intake.setPower(1);
        } else if (push) {
            intake.setPower(-1);
        } else {
            intake.setPower(0);
        }
    }

    public void doLaunch(boolean launchLong, boolean launchShort, boolean transfer) {
        if (launchLong) {
            launcherLeft.setPower(1);
            launcherRight.setPower(1);
        } else if (launchShort) {
            launcherLeft.setPower(0.8);
            launcherRight.setPower(0.8);
        } else {
            launcherLeft.setPower(0);
            launcherRight.setPower(0);
        }

        if (transfer) {
            launcherPush.setPosition(1);
            transferTime.reset();
        } else if (transferTime.milliseconds() >= 500) {
            launcherPush.setPosition(0);
        }
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
        }

        double flPwr = ctrlY + ctrlX - ctrlYaw;
        double frPwr = ctrlY - ctrlX + ctrlYaw;
        double blPwr = ctrlY - ctrlX - ctrlYaw;
        double brPwr = ctrlY + ctrlX + ctrlYaw;

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