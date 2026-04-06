package org.firstinspires.ftc.teamcode.WorldsScripts;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.StatesScripts.odoteleop;


@TeleOp(name="AAAWorldsTele")
//@Config
public class WorldsTele extends LinearOpMode {

   /* public static class Params {
        public double outtakevel;
    }

    public static WorldsTele.Params PARAMS = new WorldsTele.Params();*/

    DcMotor driveFr, driveFl, driveBr, driveBl;
    DcMotor intakeMotorLeft, intakeMotorRight;
    DcMotorEx outtakeMotorR, outtakeMotorL;
    Servo blocker, launcherTurn, hood;
    //boolean isBlocking = false;
    boolean intakeBtn, flushBtn, blockBtn, longRangeBtn;
    MecanumDrive drive;
    int outtakeVelocity;
    final int outtakeVelocityIdle = 1200;
    ElapsedTime hoodIncrementTimer = new ElapsedTime();
    double distanceToGoal;
    boolean isBlue = AutoToTeleData.side == 0;
    double hoodPos;

    public void runOpMode() {

       odoteleop odoteleop = new odoteleop( isBlue, true);
        //fr =
        //
        //
        //
        drive = new MecanumDrive(hardwareMap, new Pose2d(AutoToTeleData.AutoX, AutoToTeleData.AutoY, AutoToTeleData.AutoRot));//new Pose2d(7, -5, 0));
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
            blockBtn = gamepad1.a || gamepad2.a;
            intakeBtn = gamepad1.x || gamepad2.x;
            flushBtn = gamepad1.b || gamepad2.b;
            //longRangeBtn = gamepad2.right_trigger>0.05;
            distanceTracking(isBlue, odoteleop);
            SpinIntake(1); // x for intake, a for flush
            ToggleOuttaking();
            drive.localizer.update();
            telemetry.addLine(odoteleop.odoAimTurret(true, isBlue, drive.localizer.getPose(), launcherTurn));
            telemetry.addData("X", AutoToTeleData.AutoX);
            telemetry.addData("Y", AutoToTeleData.AutoY);
            telemetry.addData("Rot", Math.toDegrees(AutoToTeleData.AutoRot));
            telemetry.addData("Side", Math.toDegrees(AutoToTeleData.side));
            SetMotorPowers(1);
            if(gamepad1.yWasPressed()) {
                resetOdoPos();
            }
            telemetry.update();
        }
    }

    public void SpinIntake(float intakeSpeed) {
        intakeMotorLeft.setPower(intakeBtn || blocker.getPosition() == 0 ? intakeSpeed*0.8 : flushBtn ? -intakeSpeed : 0);
        intakeMotorRight.setPower(intakeBtn || blocker.getPosition() == 0 ? intakeSpeed : flushBtn ? -intakeSpeed : 0);
    }
    public void ToggleOuttaking() {
        blocker.setPosition(blockBtn && (/*velIncrementTimer.milliseconds() > 3000 || */outtakeMotorL.getVelocity() - 150 >  Math.min(outtakeVelocity, 2000))? 0 : 1);
        if(blocker.getPosition() == 1)
        {
            //if blocking
            hoodIncrementTimer.reset();
        }

        telemetry.addData("speedR", outtakeMotorR.getVelocity());
        telemetry.addData("speedL", outtakeMotorL.getVelocity());
        telemetry.addData("OUTTAKEVEL:", outtakeVelocity);
        telemetry.addData("hoodpos:", hood.getPosition());
        //if not being held down, reset the velocity incrementer
        // sets the blocker position to open once motors are at certain speed (wont work but we'll have this evench)
        //if(PARAMS.outtakevel <= 0) {
            outtakeMotorR.setVelocity(blockBtn ? outtakeVelocity : outtakeVelocityIdle);
            outtakeMotorL.setVelocity(blockBtn ? outtakeVelocity : outtakeVelocityIdle);

        //} else {
        //    outtakeMotorR.setVelocity(blockBtn ? PARAMS.outtakevel : outtakeVelocityIdle);
        //    outtakeMotorL.setVelocity(blockBtn ? PARAMS.outtakevel : outtakeVelocityIdle);
        //}

        //outtakeMotorR.setVelocity(blockBtn ? PARAMS.outtakevel : outtakeVelocityIdle);
        //outtakeMotorL.setVelocity(blockBtn ? PARAMS.outtakevel : outtakeVelocityIdle);
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
        outtakeVelocity = (int)Math.round(0.000314673*Math.pow(distanceToGoal, 3)-0.0479308*Math.pow(distanceToGoal, 2)+6.70251*distanceToGoal+791.79-150);
        hoodPos = (0.0000123126*Math.pow(distanceToGoal,2))-(0.00720119*distanceToGoal)+0.976098;
        hood.setPosition(hoodPos);
        if(distanceToGoal > 120) {
            hood.setPosition(hood.getPosition() - Math.min(500, hoodIncrementTimer.milliseconds())/2500);
        }
        telemetry.addData("Sistance to gaol", distanceToGoal);
       // }
    }

    void resetOdoPos() {
        if(isBlue) {
            drive.localizer.setPose(new Pose2d(118, -13, Math.toRadians(50)));
        } else {
            drive.localizer.setPose(new Pose2d(118, -118, Math.toRadians(-44)));
        }
        //drive.localizer.setPose(new Pose2d(AutoToTeleData.AutoX, AutoToTeleData.AutoY, AutoToTeleData.AutoRot));
    }
}