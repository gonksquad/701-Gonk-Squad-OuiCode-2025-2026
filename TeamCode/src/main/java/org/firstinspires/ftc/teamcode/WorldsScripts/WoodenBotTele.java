package org.firstinspires.ftc.teamcode.WorldsScripts;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.StatesScripts.odoteleop;


@TeleOp(name="WorldsWoodenTele")
@Config
public class WoodenBotTele extends LinearOpMode {

    public static class Params {
        //public double outtakeVelocity = 1200;
        //public double hoodPos = 0;
        public double launchWait = 300;
        public double speedIncrement = 10;
    }

    public static WoodenBotTele.Params PARAMS = new WoodenBotTele.Params();

    DcMotor driveFr, driveFl, driveBr, driveBl;
    DcMotor intakeMotorLeft, intakeMotorRight;
    DcMotorEx outtakeMotorR, outtakeMotorL;
    Servo blocker, launcherTurn, hood;
    //boolean isBlocking = false;
    boolean intakeBtn, flushBtn, blockBtn, longRangeBtn;
    MecanumDrive drive;
    int outtakeVelocity;
    final int outtakeVelocityIdle = 1200;
    ElapsedTime velIncrementTimer = new ElapsedTime();
    double distanceToGoal;
    boolean isBlue =false;

    public void runOpMode() {

       odoteleop odoteleop = new odoteleop( isBlue, true);
        //fr =
        //
        //
        //
        drive = new MecanumDrive(hardwareMap, new Pose2d(7, -5, 0));
        driveFr = hardwareMap.get(DcMotor.class, "fr");
            //driveFr.setDirection(DcMotorSimple.Direction.REVERSE);
        driveFl = hardwareMap.get(DcMotor.class, "fl");
            driveFl.setDirection(DcMotorSimple.Direction.REVERSE);
        driveBr = hardwareMap.get(DcMotor.class, "br");
            //driveBr.setDirection(DcMotorSimple.Direction.REVERSE);
        driveBl = hardwareMap.get(DcMotor.class, "bl");
            driveBl.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeMotorLeft = hardwareMap.get(DcMotor.class, "intakel");
            intakeMotorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotorRight = hardwareMap.get(DcMotor.class, "intaker");
            intakeMotorRight.setDirection(DcMotorSimple.Direction.FORWARD);
        blocker = hardwareMap.get(Servo.class, "blocker");

        outtakeMotorR = hardwareMap.get(DcMotorEx.class, "outr");
            outtakeMotorR.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeMotorL = hardwareMap.get(DcMotorEx.class, "outl");
        launcherTurn = hardwareMap.get(Servo.class, "turn");

        hood = hardwareMap.get(Servo.class, "hood");
        waitForStart();
        while (opModeIsActive()) {
            blockBtn = gamepad1.a;
            intakeBtn = gamepad1.x || blockBtn;
            flushBtn = gamepad2.b;
            longRangeBtn = gamepad2.right_trigger>0.05;
            distanceTracking(false, odoteleop);
            SpinIntake(1); // x for intake, a for flush
            ToggleOuttaking();
            drive.localizer.update();
            telemetry.addData("yurt", odoteleop.odoAimTurret(true, isBlue, drive.localizer.getPose(), launcherTurn));
            SetMotorPowers(1);
            if(gamepad1.yWasPressed()) {
                resetOdoPos();
            }
            telemetry.update();
        }
    }

    public void SpinIntake(float intakeSpeed) {
        intakeMotorLeft.setPower(intakeBtn ? intakeSpeed : flushBtn ? -intakeSpeed : 0);
        intakeMotorRight.setPower(intakeBtn ? intakeSpeed : flushBtn ? -intakeSpeed : 0);

    }
    public void ToggleOuttaking() {
        blocker.setPosition(blockBtn & (velIncrementTimer.milliseconds() > (4.5f*distanceToGoal+PARAMS.launchWait))? 0 : 1);

        telemetry.addData("speedR", outtakeMotorR.getVelocity());
        telemetry.addData("speedL", outtakeMotorL.getVelocity());
        telemetry.addData("OUTTAKEVEL:", outtakeVelocity + PARAMS.speedIncrement*velIncrementTimer.milliseconds()/40);
        telemetry.addData("hoodpos:", hood.getPosition());
        //if not being held down, reset the velocity incrementer
        if(!blockBtn) {
            velIncrementTimer.reset();
        }
        // sets the blocker position to open once motors are at certain speed (wont work but we'll have this evench)
        //sleep(100000000);
        //outtakeMotorR.setVelocity(blockBtn ? outtakeVelocityShort : outtakeVelocityShort);
        //outtakeMotorL.setVelocity(blockBtn ? outtakeVelocityShort : outtakeVelocityShort);
        outtakeMotorR.setVelocity(blockBtn ? Math.min(1800, outtakeVelocity + (0.75f*velIncrementTimer.milliseconds()-distanceToGoal*PARAMS.speedIncrement)/40) : outtakeVelocityIdle);
        outtakeMotorL.setVelocity(blockBtn ? Math.min(1800, outtakeVelocity + (0.75f*velIncrementTimer.milliseconds()-distanceToGoal*PARAMS.speedIncrement)/40): outtakeVelocityIdle);
    }
    public void SetMotorPowers(float maxSpeed) {
        double y = -gamepad1.left_stick_y*maxSpeed;
        double x = gamepad1.left_stick_x*maxSpeed;
        double rot = gamepad1.right_stick_x*maxSpeed;

        y = Math.abs(y) > 0.05 ? y : 0;
        x = Math.abs(x) > 0.05 ? x : 0;
        rot = Math.abs(rot) > 0.05 ? rot : 0;

        double fl = y + rot + x;
        double fr = y - rot - x;
        double bl = y + rot - x;
        double br = y - rot + x;

        double denominator = Math.max(Math.max(Math.max(fl, fr), Math.max(bl, br)), 1);
        // denominator returns the max motor power if above 1, else 1

        driveFl.setPower(fl / denominator);
        driveFr.setPower(fr / denominator);
        driveBl.setPower(bl / denominator);
        driveBr.setPower(br / denominator);
    }


    public void distanceTracking(boolean onBlue, odoteleop odoteleop) {
         distanceToGoal = odoteleop.getGoalDistance(onBlue, drive.localizer.getPose());
        //formula from https://www.desmos.com/calculator/hnlvt45jvt
        outtakeVelocity = (int)Math.round(-0.000310968*Math.pow(distanceToGoal, 3)+0.0946642*Math.pow(distanceToGoal, 2)-3.63154*distanceToGoal+1030.63395);
        if(distanceToGoal > 73) {
            hood.setPosition(-0.0000001086*Math.pow(distanceToGoal,4) + 0.0000441*Math.pow(distanceToGoal,3) - 0.006325*Math.pow(distanceToGoal,2) + 0.3704*distanceToGoal - 6.9);
        } else {
            hood.setPosition(0.000000121749*Math.pow(distanceToGoal, 4) - 0.000063736*Math.pow(distanceToGoal,3) +0.012192*Math.pow(distanceToGoal, 2) -1.0071*distanceToGoal +30.383);
        }
    }

    void resetOdoPos() {
        if(isBlue) {
            drive.localizer.setPose(new Pose2d(118, -13, Math.toRadians(50)));
        } else {
            drive.localizer.setPose(new Pose2d(118, -118, Math.toRadians(-44)));

        }
    }
}