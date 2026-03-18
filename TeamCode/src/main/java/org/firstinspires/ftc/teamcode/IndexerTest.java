package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class IndexerTest extends LinearOpMode {
    byte crntPos = 0;

    @Override
    public void runOpMode() {
        Hardware hardware = new Hardware(hardwareMap);

        waitForStart();

        hardware.sorter.setPosition(hardware.outtakePos[crntPos]);

        while (opModeIsActive()) {
            if (gamepad1.dpadUpWasPressed()) {
                crntPos = (byte)((crntPos + 1) % 3);
                hardware.sorter.setPosition(hardware.outtakePos[crntPos]);
            } else if (gamepad1.dpadDownWasPressed()) {
                crntPos = (byte)((crntPos + 1) % 3);
                hardware.sorter.setPosition(hardware.intakePos[crntPos]);
            }

            telemetry.addData("Current Index", crntPos);
            telemetry.addData("Current Position", hardware.sorter.getPosition());
            telemetry.update();
        }
    }
}
