// used to be TeleopFromHardware
package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.Hardware;

import java.util.List;

@TeleOp //(name="QualTele")
public class QualTele extends LinearOpMode {

    boolean manual = false;
    float llsP;
    boolean prevManual = false;
    double prevOffset = 0d;
    final double offsetAmount = 0d;
    boolean prevSorterL = false;
     boolean prevSorterR = false;
    boolean prevA = false;
    boolean prevB = false;

    /// touch sensor stuff is temp, just for testing
    TouchSensor limitLeft, limitRight;

    @Override
    public void runOpMode() throws InterruptedException {
        llsP = 0.005f;
        Hardware hardware = new Hardware(hardwareMap);
        /// touch sensor stuff is temp, just for testing
        limitLeft = hardwareMap.touchSensor.get("limitLeft");
        limitRight = hardwareMap.touchSensor.get("limitRight");

        hardware.limelight.pipelineSwitch(0);
        waitForStart();
        hardware.limelight.start();
        hardware.outtakeTransferLeft.setPosition(0.85);
        hardware.outtakeTransferRight.setPosition(0.85);
        hardware.sorter.setPosition(0.8);
     //   hardware.launcherTurn.setPower(0d); SHOULDNT BECOMMENTED

        while (opModeIsActive()) {
            if (gamepad2.guide) { // toggle manual override
                if (!prevManual) {
                    manual = !manual;
                    if (manual) {
                        gamepad2.rumble(200);
                    } else {
                        gamepad2.rumble(200);
                        gamepad2.rumble(200);
                    }
                }
                prevManual = true;
            } else {
                prevManual = false;
            }

            if(gamepad1.dpad_up) {
                llsP += 0.00001f;
            }

            if(gamepad1.dpad_down) {
                llsP -= 0.00001f;
            }
            // AUTOAIMING RUNS IN THIS TELEMETRY
            // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
         //   telemetry.addData("yaw speed", hardware.launcherTurn.getPower()); SHOULDNT BE COMMENTED

            //telemetry.addData("April tag found at degree", hardware.autoAimTurret(true, llsP));

//            telemetry.addData("April tag found at degree", hardware.autoAimTurret(true, llsP));

            telemetry.addData("lllll", llsP);

//            if(gamepad1.xWasPressed()) {
//                hardware.outtakeTransfer.setPosition(0.2);
//            }

//            double launchTurnPower = gamepad2.right_trigger - gamepad2.left_trigger;
//            if (Math.abs(launchTurnPower) > 0.125) {
//                hardware.launcherTurn.setPower(launchTurnPower * 0.5);
//            } else {
//                hardware.launcherTurn.setPower(0d);
//            }

            if (gamepad2.y && !gamepad2.a) {
                hardware.intaking = false;
                hardware.intake.setPower(-1);
            }

            if (manual) {
                if (gamepad2.a) {
                    hardware.intake.setPower(1d);
                } else if (gamepad2.b) {
                    hardware.intake.setPower(0d);
                }
                double  launchVelocity = gamepad2.left_trigger * 700 + 1000;
                if (launchVelocity < 1100) launchVelocity = 0;
                hardware.launcherRight.setVelocity(launchVelocity);
                hardware.launcherLeft.setVelocity(launchVelocity);

                if (gamepad1.dpad_up) {
                    if (!prevA) {
                        hardware.currentPos = (hardware.currentPos + 2) % 3;
                        hardware.sorter.setPosition(hardware.intakePos[hardware.currentPos]);
                    }
                    prevA = true;
                } else {
                    prevA = false;
                }

                if (gamepad1.dpad_down) {
                    if (!prevB) {
                        hardware.currentPos = (hardware.currentPos + 2) % 3;
                        hardware.sorter.setPosition(hardware.outtakePos[hardware.currentPos]);
                    }
                    prevB = true;
                } else {
                    prevB = false;
                }

                if (hardware.sorterOffset != prevOffset) { // don't set position if it is already set
                    prevOffset = hardware.sorterOffset;
                    hardware.sorter.setPosition(hardware.sorterOffset);
                }
            } else {
                hardware.tryIntake(gamepad2.a);
                if (gamepad2.b && !gamepad2.a && !gamepad2.right_bumper) {
                    hardware.stopIntake();
                    hardware.stopLaunch();
                }
                // 1 = purple, 2 = green. did this so that 0 can be either to help drivers
                hardware.tryLaunch(gamepad2.right_bumper, 1, 1150);
                hardware.tryLaunch(gamepad2.right_trigger > 0.125, 1, 1350);
                hardware.tryLaunch(gamepad2.left_bumper, 2, 1150);
                hardware.tryLaunch(gamepad2.left_trigger > 0.125, 2, 1350);
                hardware.tryLaunch(gamepad2.x, 0, 1150);
//                hardware.tryLaunchGreen(gamepad2.dpad_down);
//                if (gamepad2.dpad_right && !(gamepad2.dpad_up || gamepad2.dpad_down)) {
//                    hardware.stopLaunch();
//                }

                hardware.doDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, .75d, .75d, 0.5);

                // AUTOAIMING RUNS IN THIS TELEMETRY
                // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
            //    telemetry.addData("yaw speed", hardware.launcherTurn.getPower()); shOULDNT BE COMMENTED

                //telemetry.addData("April tag found at degree", hardware.autoAimTurret(true, llsP));

//                telemetry.addData("April tag found at degree", hardware.autoAimTurret(true, llsP));

                telemetry.addData("lllll", llsP);

                telemetry.addData("Position Value: ", hardware.sorter.getPosition());
//                telemetry.addData("Sorter Contents: ", "%d, %d, %d", hardware.sorterPos[0], hardware.sorterPos[1], hardware.sorterPos[2]); //should not be commented tbh
                telemetry.addData("Launch Speed: ", hardware.launcherLeft.getVelocity());

                /// touch sensor stuff is temp, just for testing
                telemetry.addData("limit left", limitLeft.isPressed());
                telemetry.addData("limit right", limitRight.isPressed());

                telemetry.update();
            }
        }
    }
}
