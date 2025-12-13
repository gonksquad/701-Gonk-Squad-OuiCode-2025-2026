package org.firstinspires.ftc. teamcode. QualifierScripts;

import com.qualcomm.hardware.limelightvision. Limelight3A;
import com.qualcomm.hardware. rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com. qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore. hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com. qualcomm.robotcore.hardware.Servo;
import com. qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware;

public class RRHardware{
    private HardwareMap hwMap; // Store hardware map

    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcherLeft, launcherRight; //limelightturn
    public CRServo launcherTurn;
    public Servo sorter, outtakeTransfer;
    public Limelight3A limelight;
    public ColorSensor colorSensor;
    int red, green, blue;
    float[] hsvValues = new float[3];
    boolean nextPos = true;

    public final double[] intakePos = {1.0, 0.6, 0.2};
    public final double[] outtakePos = {0.4, 0.0, 0.8};
    public double sorterOffset = 0d;
    public byte[] sorterPos = {0, 0, 0};
    public int currentPos = 0;
    ElapsedTime changePosTimer = new ElapsedTime();

    public boolean intaking = false;
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;

    public RRHardware(HardwareMap hardwareMap) {
        this.hwMap = hardwareMap;
    }

    public void init() {
        frontLeft = hwMap.get(DcMotor.class, "fl");
        frontRight = hwMap. get(DcMotor.class, "fr");
        backLeft = hwMap.get(DcMotor.class, "bl");
        backRight = hwMap. get(DcMotor.class, "br");

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior. BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        launcherLeft = hwMap.get(DcMotorEx.class, "launcherL");
        launcherRight = hwMap.get(DcMotorEx.class, "launcherR");

        launcherTurn = hwMap.get(CRServo.class, "launcherYaw");
        //limelightTurn = hwMap. get(Servo.class, "limeservo");

        intake = hwMap.get(DcMotor.class, "intake");
        sorter = hwMap.get(Servo.class, "sorter");
        outtakeTransfer = hwMap.get(Servo.class, "lift");
        limelight = hwMap.get(Limelight3A.class, "limelight");
        colorSensor = hwMap.get(RevColorSensorV3.class, "colorSens");

        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRight.setDirection(DcMotorSimple. Direction.REVERSE);
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

    public void intake1() {
        sorter.setPosition(.2);
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }

    public void intake2() {
        sorter.setPosition(.6);
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }

    public void intake3() {
        sorter.setPosition(1);
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }

    public void launchOnFar() {
        launcherLeft.setVelocity(1250);
        launcherRight.setVelocity(1250);
    }

    public void launchOnClose() {
        launcherLeft.setVelocity(1000);
        launcherRight.setVelocity(1000);
    }

    public void launchOff() {
        launcherLeft.setVelocity(0);
        launcherRight.setVelocity(0);
    }

    public void shootgppfar() {
        launchOnFar();
        sorter.setPosition(.4);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.8);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer. setPosition(.9);
        sleep(1000);
        sorter.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        launchOff();
    }

    public void shootpgpfar() {
        launchOnFar();
        sorter.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.4);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.8);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        launchOff();
    }

    public void shootppgfar() {
        launchOnFar();
        sorter.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.8);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.4);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        launchOff();
    }
    public void shootgppclose() {
        launchOnClose();
        sorter.setPosition(.4);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.8);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer. setPosition(.9);
        sleep(1000);
        sorter.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        launchOff();
    }

    public void shootpgpclose() {
        launchOnClose();
        sorter.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.4);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.8);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        launchOff();
    }

    public void shootppgclose() {
        launchOnClose();
        sorter.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.8);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        sleep(1000);
        sorter.setPosition(.4);
        sleep(1000);
        outtakeTransfer.setPosition(0);
        sleep(1000);
        outtakeTransfer.setPosition(.9);
        launchOff();
    }
}