package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="ScrimmageTele")

public class ScrimmageBot extends LinearOpMode {

    public DcMotor frontLeft, frontRight, backLeft, backRight;
    public DcMotor intakeMotor, outtakeMotor1, outtakeMotor2;
    public Servo transferServo;
    public ElapsedTime transferTimer = new ElapsedTime();

    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "fl"); //controlhub0
        frontRight = hardwareMap.get(DcMotor.class, "fr"); // expanis ionhub0
        backLeft = hardwareMap.get(DcMotor.class, "bl"); //ch1
        backRight = hardwareMap.get(DcMotor.class, "br"); // eh1

        intakeMotor = hardwareMap.get(DcMotor.class, "in"); //ch2
        outtakeMotor1 = hardwareMap.get(DcMotor.class, "out1"); //ch3
        outtakeMotor2 = hardwareMap.get(DcMotor.class, "out2"); //eh2

        transferServo = hardwareMap.get(Servo.class, "servo"); //

        //frontLeft.setDirection(DcMotor.Direction.REVERSE);
        //backLeft.setDirection(DcMotor.Direction.REVERSE);
        outtakeMotor2.setDirection(DcMotor.Direction.REVERSE);
        waitForStart();
        while(opModeIsActive()) {
           chassisMovement();
           inouttake();
        }
    }

    public void chassisMovement() {
        double x = gamepad1.left_stick_x;
        double y = gamepad1.left_stick_y;

        // how far the joystick is pushed
        double power = Math.hypot(x, y);

        double turn = gamepad1.right_stick_x;

        // Naming blue based on GoBuilda mecanum wheel direction diagram
        double blue; // Motors front right and back left
        double green; // motors front left and back right

        // gets the angle the joystick is reletive to its resting position
        // and converts from radians to degrees
        double angle = Math.atan2(-y, x) * (180/Math.PI);
        telemetry.addData("Angle", angle);

        // bunch of math that could be shortened with trig but lets you move in any direction p much
        if(angle >= 0  && angle < 90) {
            // top right
            blue = angle/45 - 1;
            green = 1;
        } else if (angle >= 90  && angle < 180) {
            // top left
            blue = 1;
            green = -((angle-180)/45 + 1);
        } else if(angle >= -180  && angle < -90) {
            // bottom left
            blue = (Math.abs(angle) - 180)/45 + 1;
            green = -1;
        } else if(angle >= -90  && angle < 0) {
            // bottom right
            blue = -1;
            green = angle/45 + 1;
        } else {
            green = 0;
            blue = 0;
        }
        // multiplies direction by how far the joystick is pushed
        green *= power;
        blue *= power;

        //sets the powers based on an equation I came up with in my head (NOT CHATGPT)
        // ^^ WHY JACOB 😭😭😭  https://bluemoji.io/emoji/desperate
            frontLeft.setPower(Math.max(-1,Math.min(1,(green+turn))));
            backRight.setPower(Math.max(-1,Math.min(1,(green-turn))));

            frontRight.setPower(Math.max(-1,Math.min(1,(blue-turn))));
            backLeft.setPower(Math.max(-1,Math.min(1,(blue+turn))));
    }
    public void inouttake() {
        double outSpeed = Math.round((gamepad2.right_trigger/5)*10)/10 + 0.8;

        if(gamepad2.left_trigger>0.05) {
            double inSpeed = 1;

            intakeMotor.setPower(inSpeed);
            outtakeMotor1.setPower(outSpeed);
            outtakeMotor2.setPower(outSpeed);

        } else if(gamepad2.right_trigger<=0.05 && gamepad2.right_trigger<=0.05) {
            outtakeMotor1.setPower(0);
            outtakeMotor2.setPower(0);
        }
        if(gamepad2.right_trigger>0.05) {

            outtakeMotor1.setPower(outSpeed);
            outtakeMotor2.setPower(outSpeed);
        } else if(gamepad2.right_trigger<=0.05 && gamepad2.right_trigger<=0.05){
            outtakeMotor1.setPower(0);
            outtakeMotor2.setPower(0);
        }

        if(gamepad2.a && transferTimer.milliseconds() >= 250) {
            transferServo.setPosition(1);
            transferTimer.reset();
        } else if(transferTimer.milliseconds() >= 500) {
            transferServo.setPosition(0);
        }

    }

}
