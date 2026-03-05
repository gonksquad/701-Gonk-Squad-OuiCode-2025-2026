package org.firstinspires.ftc.teamcode.WorldsScripts;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="WorldsWoodenTele")
public class WoodenBotTele extends LinearOpMode {

    DcMotor driveFr, driveFl, driveBr, driveBl;
    DcMotor intakeMotor;
    DcMotorEx outtakeMotorR, outtakeMotorL;
    Servo blocker;
    boolean isBlocking = false;
    boolean intakeBtn, flushBtn, blockBtn;


    public void runOpMode() {

        //fr =
        //
        //
        //
        driveFr = hardwareMap.get(DcMotor.class, "fr");
            //driveFr.setDirection(DcMotorSimple.Direction.REVERSE);
        driveFl = hardwareMap.get(DcMotor.class, "fl");
            driveFl.setDirection(DcMotorSimple.Direction.REVERSE);
        driveBr = hardwareMap.get(DcMotor.class, "br");
            //driveBr.setDirection(DcMotorSimple.Direction.REVERSE);
        driveBl = hardwareMap.get(DcMotor.class, "bl");
            driveBl.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeMotor = hardwareMap.get(DcMotor.class, "intakel");
            intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        blocker = hardwareMap.get(Servo.class, "blocker");

        outtakeMotorR = hardwareMap.get(DcMotorEx.class, "outr");
            outtakeMotorR.setDirection(DcMotorSimple.Direction.REVERSE);
        outtakeMotorL = hardwareMap.get(DcMotorEx.class, "outl");

        waitForStart();
        while (opModeIsActive()) {
            intakeBtn = gamepad2.x;
            flushBtn = gamepad2.b;
            blockBtn = gamepad2.aWasPressed();
            SpinIntake(1); // x for intake, a for flush
            ToggleOuttaking(1, 0, 1200);
            SetMotorPowers(1);
        }
    }

    public void SpinIntake(float intakeSpeed) {
        intakeMotor.setPower(intakeBtn ? intakeSpeed : flushBtn ? -intakeSpeed : 0);
    }
    public void ToggleOuttaking(float open, float blocked, float outtakeVelocitas) {
        isBlocking = blockBtn ? !isBlocking : isBlocking;
        blocker.setPosition(isBlocking ? blocked : (outtakeMotorR.getVelocity() >=  outtakeVelocitas) ? open : blocked);
        // sets the blocker position to open once motors are at certain speed (wont work but we'll have this evench)
        //sleep(100000000);
        outtakeMotorR.setVelocity(isBlocking ? outtakeVelocitas : 0);
        outtakeMotorL.setVelocity(isBlocking ? outtakeVelocitas : 0);

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