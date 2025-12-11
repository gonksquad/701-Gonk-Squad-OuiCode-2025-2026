package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="QualTele")
public class TeleopFromHardware extends LinearOpMode {

    boolean manual = false;
    boolean prevManual = false;
    double prevOffset = 0d;
    final double offsetAmount = 6d / 71d;
    boolean prevSorterL = false;
    boolean prevSorterR = false;

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);

        waitForStart();
        hardware.sorter.setPosition(0d);

        while (opModeIsActive()) {
            if (gamepad2.guide) { // toggle manual override
                if (!prevManual) {
                    manual = !manual;
                    if (manual) {
                        gamepad2.rumble(10);
                    } else {
                        gamepad2.rumble(10);
                        gamepad2.rumble(10);
                    }
                }
                prevManual = true;
            } else {
                prevManual = false;
            }

            if (gamepad2.left_bumper) {
                if (!prevSorterL) {
                    hardware.sorterOffset += offsetAmount;
                    if (hardware.sorterOffset > 1d) hardware.sorterOffset -= 1d;
                }
                prevSorterL = true;
            } else {
                prevSorterL = false;
            }

            if (gamepad2.right_bumper) {
                if (!prevSorterR) {
                    hardware.sorterOffset -= offsetAmount;
                    if (hardware.sorterOffset > 1d) hardware.sorterOffset -= 1d;
                }
                prevSorterR = true;
            } else {
                prevSorterR = false;
            }

            if (manual) {
                hardware.intake.setPower(gamepad2.a ? 1d : 0d);
                double launchVelocity = gamepad2.left_trigger * 1400 + 1400;
                if (launchVelocity < 2000) launchVelocity = 0;
                hardware.launcherRight.setVelocity(launchVelocity);
                hardware.launcherLeft.setVelocity(launchVelocity);

               // if ()

                if (hardware.sorterOffset != prevOffset) { // don't set position if it is already set
                    prevOffset = hardware.sorterOffset;
                    hardware.sorter.setPosition(hardware.sorterOffset);
                }
            } else {
                hardware.tryIntake(gamepad2.a);
                if (gamepad2.b && !gamepad2.a) {
                    hardware.stopIntake();
                }

                hardware.tryLaunchPurple(gamepad2.dpad_up);
                hardware.tryLaunchGreen(gamepad2.dpad_down);
                if (gamepad2.dpad_right && !(gamepad2.dpad_up || gamepad2.dpad_down)) {
                    hardware.stopLaunch();
                }

                hardware.doDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, .75d, .75d, 0.5);
            }
        }
    }
}
