package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MiscScripts.Hardware;

@TeleOp(name="Ms. Shields Drive")
public class OldSlowWheels extends LinearOpMode {
    public Hardware hardware;

    @Override
    public void runOpMode() {
        hardware = new Hardware(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            hardware.doDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, 0.5, 0.5, 0.5);

            hardware.tryIntake(gamepad1.a);

            hardware.tryLaunch(gamepad1.left_bumper, 1, 1150);
            hardware.tryLaunch(gamepad1.right_bumper, 2, 1150);
            hardware.tryLaunch(gamepad1.left_trigger > 0.125, 1, 1350);
            hardware.tryLaunch(gamepad1.right_trigger > 0.125, 2, 1350);

            if (gamepad1.b) {
                hardware.stopIntake();
                hardware.stopLaunch();
            }
        }
    }
}
