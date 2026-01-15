package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class SpeedTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);
        int testTps = 1700;

        waitForStart();

        hardware.launcherTurn.setPower(0d);

        while (opModeIsActive()) {
            if (gamepad2.xWasPressed()) {
                testTps -= 100;
            }

            if (gamepad2.y && hardware.outtakeTransferLeft.getPosition() != 0.2) {
                hardware.outtakeTransferLeft.setPosition(0.2);
                hardware.outtakeTransferRight.setPosition(0.2);
            } else if (hardware.outtakeTransferLeft.getPosition() != 0.9) {
                hardware.outtakeTransferLeft.setPosition(0.9);
                hardware.outtakeTransferRight.setPosition(0.9);
            }

            hardware.tryLaunch(gamepad2.right_bumper, 1, testTps);
            if (gamepad2.b && !gamepad2.right_bumper) {
                hardware.stopLaunch(0);
            }

            telemetry.addData("Launcher TPS: ", testTps);
            telemetry.update();
        }
    }
}
