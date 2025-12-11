package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="SorterTest")
public class SorterTest extends LinearOpMode {

    boolean prevA = false;
    boolean prevB = false;
    int nextPos = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            hardware.tryIntake(gamepad1.a);
            if (gamepad1.b && !gamepad1.a) {
                hardware.stopIntake();
            }
//
//            hardware.tryLaunchPurple(gamepad1.x);
//            if (gamepad1.y && !gamepad1.x) {
//                hardware.stopLaunch();
//            }
//            hardware.doDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            if (gamepad1.dpad_up) {
                if (!prevA) {
                    hardware.sorter.setPosition(hardware.intakePos[nextPos]);
                    nextPos ++;
                    if (nextPos >= 3) nextPos -= 3;
                }
                prevA = true;
            } else {
                prevA = false;
            }

            if (gamepad1.dpad_down) {
                if (!prevB) {
                    hardware.sorter.setPosition(hardware.outtakePos[nextPos]);
                    nextPos ++;
                    if (nextPos >= 3) nextPos -= 3;
                }
                prevB = true;
            } else {
                prevB = false;
            }

            telemetry.addData("Position: ", nextPos);
            telemetry.addData("Position Value: ", hardware.sorter.getPosition());
            telemetry.update();
        }
    }
}
