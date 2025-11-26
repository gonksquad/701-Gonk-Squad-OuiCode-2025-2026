package org.firstinspires.ftc.teamcode.scrimmagecode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple; //???


@Disabled
public class AJTest extends LinearOpMode {
    public DcMotor left;
    public DcMotor right;

    @Override
    public void runOpMode() {

        left = hardwareMap.get(DcMotor.class, "lm");
        right = hardwareMap.get(DcMotor.class, "rm");


        //left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        right.setDirection(DcMotorSimple.Direction.REVERSE);


        waitForStart();
        while (opModeIsActive()) {
            left.setPower(1);
            right.setPower(1);
        }
    }
}