package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp (name = "bestTeleOp")
public class ChassisV1  extends LinearOpMode{

    public DcMotor frontLeft, frontRight, backLeft, backRight;

    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        waitForStart();
        while(opModeIsActive()) {
            chassisMovement();
            telemetry.addData("Front Left", frontLeft.getPower());
            telemetry.addData("Front Right", frontRight.getPower());
            telemetry.addData("Back Left", backLeft.getPower());
            telemetry.addData("Back Right", backRight.getPower());

            telemetry.update();
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

        //sets the powers based on an equation I chatgpt'd
        // ^^ WHY JACOB 😭😭😭  https://bluemoji.io/emoji/desperate
        frontLeft.setPower(Math.max(-1,Math.min(1,(green+turn)/2)));
        backRight.setPower(Math.max(-1,Math.min(1,(green-turn)/2)));

        frontRight.setPower(Math.max(-1,Math.min(1,(blue-turn)/2)));
        backLeft.setPower(Math.max(-1,Math.min(1,(blue+turn)/2)));
        //(research smartDashboard.putNumber();
    }

}
