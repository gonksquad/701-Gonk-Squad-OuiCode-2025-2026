package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class LiftTest extends LinearOpMode {

    private Servo liftL;
    private Servo liftR;

    @Override
    public void runOpMode() {
        liftL = hardwareMap.get(Servo.class, "liftLeft");
        liftR = hardwareMap.get(Servo.class, "liftRight");

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.a && !gamepad1.b) {
                liftL.setPosition(0.2);
                liftR.setPosition(0.8);
            }
            if (gamepad1.b && !gamepad1.a) {
                liftL.setPosition(0.6);
                liftR.setPosition(0.4);
            }
        }
    }
}
