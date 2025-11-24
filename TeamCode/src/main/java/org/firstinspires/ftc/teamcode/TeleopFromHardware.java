package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
public class TeleopFromHardware extends LinearOpMode {
    Hardware hardware = new Hardware(hardwareMap);

    boolean prevA = false;

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        while (opModeIsActive()) {
            // toggle intake
            if (gamepad2.a) {
                if (!prevA) {
                    prevA = true;
                    if (hardware.intaking) {
                        hardware.stopIntake();
                    } else {
                        hardware.tryIntake(true);
                    }
                }
            } else {
                prevA = false;
            }
            hardware.tryLaunchGreen(gamepad2.x);
            hardware.tryLaunchPurple(gamepad2.b);
            if (gamepad2.y && !(gamepad2.x || gamepad2.b)) {
                hardware.stopLaunch();
            }
            hardware.doDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        }
    }
}
