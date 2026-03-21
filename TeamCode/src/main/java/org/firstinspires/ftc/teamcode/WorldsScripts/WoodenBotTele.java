package org.firstinspires.ftc.teamcode.WorldsScripts;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.StatesScripts.odoteleop;
//import com.acmerobotics.dashboard.config.Config;


@TeleOp(name="WorldsWoodenTele")
public class WoodenBotTele extends LinearOpMode {

   /* public static class Params {
        public double servoPos = 0;
    }

    public static WoodenBotTele.Params PARAMS = new WoodenBotTele.Params();*/

    DcMotor driveFr, driveFl, driveBr, driveBl;
    DcMotor intakeMotorLeft, intakeMotorRight;
    DcMotorEx outtakeMotorR, outtakeMotorL;
    Servo blocker, launcherTurn;
    //boolean isBlocking = false;
    boolean intakeBtn, flushBtn, blockBtn, longRangeBtn;
    MecanumDrive drive;

    public void runOpMode() {

       odoteleop odoteleop = new odoteleop( true, true);
        //fr =
        //
        //
        //
        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
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

        waitForStart();
        while (opModeIsActive()) {
            blockBtn = gamepad2.a;
            intakeBtn = gamepad2.x || blockBtn;
            flushBtn = gamepad2.b;
            longRangeBtn = gamepad2.right_trigger>0.05;
            //SpinIntake(1); // x for intake, a for flush
            //ToggleOuttaking();

            drive.localizer.update();
            telemetry.addData("yurt", odoteleop.odoAimTurret(true, true, false, drive.localizer.getPose(), launcherTurn));
            SetMotorPowers(1);
            telemetry.update();
        }
    }

    public void SpinIntake(float intakeSpeed) {
        intakeMotorLeft.setPower(intakeBtn ? intakeSpeed : flushBtn ? -intakeSpeed : 0);
        intakeMotorRight.setPower(intakeBtn ? intakeSpeed : flushBtn ? -intakeSpeed : 0);

    }
    public void ToggleOuttaking() {
        final int outtakeVelocityShort = 1200;
        final int outtakeVelocityLong = 1400;
        final int outtakeVelocityIdle = 1050;
        blocker.setPosition(blockBtn ? 0 : 1);
        telemetry.addData("speedR", outtakeMotorR.getVelocity());
        telemetry.addData("speedL", outtakeMotorL.getVelocity());
        // sets the blocker position to open once motors are at certain speed (wont work but we'll have this evench)
        //sleep(100000000);
        //outtakeMotorR.setVelocity(blockBtn ? outtakeVelocityShort : outtakeVelocityShort);
        //outtakeMotorL.setVelocity(blockBtn ? outtakeVelocityShort : outtakeVelocityShort);
        outtakeMotorR.setVelocity(blockBtn ? outtakeVelocityShort : outtakeVelocityIdle);
        outtakeMotorL.setVelocity(blockBtn ? (!longRangeBtn ? outtakeVelocityShort : outtakeVelocityLong) : outtakeVelocityIdle);
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
}