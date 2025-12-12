package org.firstinspires.ftc.teamcode.QualifierScripts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware;

public class RRHardware{
    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcherLeft, launcherRight;
    public CRServo launcherTurn;
    public Servo sorter, outtakeTransfer, limelightTurn;
    public Limelight3A limelight;
    public ColorSensor colorSensor;
    int red, green, blue;
    float[] hsvValues = new float[3];
    boolean nextPos = true;
    // TODO: Set correct intake and outtake positions
    public final double[] intakePos = {1.0, 0.6, 0.2}; // sorter servo positions for outtaking
    public final double[] outtakePos = {0.4, 0.0, 0.8}; // sorter servo positions for intaking
    public double sorterOffset = 0d;
    public byte[] sorterPos = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green
    public int currentPos = 0; // intake is 0-2 outtake is 3-5
    ElapsedTime changePosTimer = new ElapsedTime();

    // initialize flags
    public boolean intaking = false;
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;


    public void init() {
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
        limelightTurn = hardwareMap.get(Servo.class, "limeservo"); //c0

        intake = hardwareMap.get(DcMotor.class, "intake"); // e2
        sorter = hardwareMap.get(Servo.class, "sorter"); //c2
        outtakeTransfer = hardwareMap.get(Servo.class, "lift"); // c5
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        colorSensor = hardwareMap.get(RevColorSensorV3.class, "colorSens");


        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public RRHardware(HardwareMap hardwareMap) {
        //super(hardwareMap);
        // e0 = expansion hub 0, c2 = control hub 2

    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void intake1() { //g
        sorter.setPosition(.2); //blue
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }
    public void intake2() { //p
        sorter.setPosition(.6); //black
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }
    public void intake3() { //p
        sorter.setPosition(1); //grey
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }

    /*

    intake pos: .2, .6, 1.0
    outtake pos: 0, .4, .8
    between blue/black: .25 (p)
    between black/grey: .65 (g)
    between grey/blue: 1.05 (p)
     */
    public void shootgpp() { //gpp start, both because spike mark 1
        sorter.setPosition(.4);
        outtakeTransfer.setPosition(0);
        sleep(500);

        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.8);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(0);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
//        1,2,3(outtake servo pos)
        //black, grey, blue
    }
    public void shootpgp() { //gpp start, both because spike mark 1
        sorter.setPosition(0);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.4);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.8);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
//        2,1,3(outtake servo pos)
    }
    public void shootppg() { //gpp start, both because spike mark 1
        sorter.setPosition(0);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.8);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.4);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
//        2,3,1
    }
}
