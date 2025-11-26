package org.firstinspires.ftc.teamcode.scrimmagecode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "ScrimHardwareTeleOp")
public class ScrimmageTeleOp extends LinearOpMode {
    ScrimmageHardware hardware;

    @Override
    public void runOpMode() {
        hardware = new ScrimmageHardware(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            hardware.doIntake(gamepad2.left_trigger > 0.125, gamepad2.y);
            hardware.doLaunch(gamepad2.right_trigger > 0.125, gamepad2.right_bumper, gamepad2.a);
            hardware.doDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        }
    }
}
