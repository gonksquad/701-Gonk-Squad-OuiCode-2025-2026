package org.firstinspires.ftc.teamcode.WorldsScripts;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name="Testing")
public class TestingScript extends LinearOpMode {
    //DcMotor launcherL, launcherR;
    Servo blocker;
    boolean settingPowers = false;

    public void runOpMode() {
       /*launcherL = hardwareMap.get(DcMotor.class, "LauncherL");
            launcherL.setDirection(DcMotorSimple.Direction.FORWARD);
        launcherR = hardwareMap.get(DcMotor.class, "LauncherR");
            launcherR.setDirection(DcMotorSimple.Direction.REVERSE);
        */blocker = hardwareMap.get(Servo.class, "blocker");

        waitForStart();
        while (opModeIsActive()){
            //SetPowers(1, false, 1150, launcherL, launcherR);
            blocker.setPosition(gamepad1.a ?  1 : 0);
            telemetry.addData("block pos", blocker.getPosition());
            telemetry.update();
        }
    }

    void SetPowers(double power, boolean useTps, double tps, DcMotor motor1, DcMotor motor2) {
        if(gamepad1.xWasPressed()){
            settingPowers = !settingPowers;
        }
        if(settingPowers){
            motor1.setPower(useTps ? tps : power); // shortened if statement
            motor2.setPower(useTps ? tps : power); // same as if (useTps){tps}else{power}
        }
    }
}
