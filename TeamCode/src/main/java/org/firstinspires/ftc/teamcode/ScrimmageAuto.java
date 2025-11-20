package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
@Autonomous (name="ScrimmageAUTO")
public class ScrimmageAuto extends LinearOpMode{
    public DcMotor frontLeft, frontRight, backLeft, backRight;
    public DcMotor intakeMotor, outtakeMotor1, outtakeMotor2;
    public Servo transferServo;


    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.get(DcMotor.class, "fl"); //controlhub0
        frontRight = hardwareMap.get(DcMotor.class, "fr"); // expanis ionhub0
        backLeft = hardwareMap.get(DcMotor.class, "bl"); //ch1
        backRight = hardwareMap.get(DcMotor.class, "br"); // eh1

        intakeMotor = hardwareMap.get(DcMotor.class, "in"); //ch2
        outtakeMotor1 = hardwareMap.get(DcMotor.class, "out1"); //ch3
        outtakeMotor2 = hardwareMap.get(DcMotor.class, "out2"); //eh2

        transferServo = hardwareMap.get(Servo.class, "servo"); //

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        outtakeMotor2.setDirection(DcMotor.Direction.REVERSE);
        waitForStart();

        Forward(150);
        Outtake(1);
        sleep(5000);
        Servo();
        sleep(500);
        Outtake(-0.25);
        sleep(2000);
        Intake(0.75);
        sleep(2500);
        Outtake(1);
        sleep(2000);
        Intake(0);
        sleep(1000);
        Servo();
        sleep(1000);
        Intake(0);
        Outtake(0);
        Forward(350);
        Outtake(0);

    }

    public void Outtake(double speed){
        outtakeMotor1.setPower(speed);
        outtakeMotor2.setPower(speed);
    }

    public void Intake(double speed) {
        intakeMotor.setPower(speed);
    }

    public void Servo(){
        transferServo.setPosition(1);
        sleep(500);
        transferServo.setPosition(0);
    }

    public void Forward(int time) {
        frontLeft.setPower(0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(0.5);
        backRight.setPower(0.5);
        sleep(time);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }


}
