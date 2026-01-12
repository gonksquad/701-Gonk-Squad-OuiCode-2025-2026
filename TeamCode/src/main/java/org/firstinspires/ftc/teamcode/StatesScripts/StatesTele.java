// used to be TeleopFromHardware
package org.firstinspires.ftc.teamcode.StatesScripts;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp (name="StatesTele")
public class StatesTele extends LinearOpMode {

    boolean manual = false;
    boolean prevManual = false;
    double prevOffset = 0d;
    final double offsetAmount = 0d;
    boolean prevSorterL = false;
     boolean prevSorterR = false;
    boolean prevA = false;
    boolean prevB = false;

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);
        odoteleop odoteleop = new odoteleop(hardwareMap);
        hardware.limelight.pipelineSwitch(0);
        waitForStart();
        hardware.limelight.start();
        hardware.outtakeTransferLeft.setPosition(hardware.liftPos[0]); //down position
        hardware.outtakeTransferRight.setPosition(hardware.liftPos[0]); // down position
        hardware.sorter.setPosition(0.8);
        hardware.launcherTurn.setPower(0d);

        while (opModeIsActive()) {
            if (gamepad1.guide) { // toggle manual override
                if (!prevManual) {
                    manual = !manual;
                    if (manual) {
                        gamepad1.rumble(200);
                    } else {
                        gamepad1.rumble(200);
                        gamepad1.rumble(200);
                    }
                }
                prevManual = true;
            } else {
                prevManual = false;
            }

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
                    hardware.stopLaunch(0);
                }
                // 1 = purple, 2 = green. did this so that 0 can be either to help drivers
                hardware.tryLaunch(gamepad2.right_bumper, 1, 1150);
                hardware.tryLaunch(gamepad2.right_trigger > 0.125, 1, 1350);
                hardware.tryLaunch(gamepad2.left_bumper, 2, 1150);
                hardware.tryLaunch(gamepad2.left_trigger > 0.125, 2, 1350);
                hardware.tryLaunch(gamepad2.x, 0, 1150);
                if(gamepad2.dpad_down){
                    hardware.stopLaunch(0);
                }
//                hardware.tryLaunchGreen(gamepad2.dpad_down);
//                if (gamepad2.dpad_right && !(gamepad2.dpad_up || gamepad2.dpad_down)) {
//                    hardware.stopLaunch();
//                }

                hardware.doDrive(-gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x, .75d, .75d, 0.5);

                //odo auto aiming
                odoteleop.odoAimTurret(true);
                telemetry.addData("robotX", odoteleop.getOdoData(org.firstinspires.ftc.teamcode.StatesScripts.odoteleop.odoDataTypes.X));
                telemetry.addData("robotY", odoteleop.getOdoData(org.firstinspires.ftc.teamcode.StatesScripts.odoteleop.odoDataTypes.Y));
                telemetry.addData("robotRot", odoteleop.getOdoData(org.firstinspires.ftc.teamcode.StatesScripts.odoteleop.odoDataTypes.HEADING));

                //camera auto aiming
                /*telemetry.addData("yaw speed", hardware.launcherTurn.getPower());
                // AUTOAIMING RUNS IN THIS TELEMETRY
                // VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
                telemetry.addData("April tag found at degree", hardware.autoAimTurret(true, 0.005f));
                */

                telemetry.addData("Position Value: ", hardware.sorter.getPosition());
                telemetry.addData("Sorter Contents: ", "%d, %d, %d", hardware.sorterPos[0], hardware.sorterPos[1], hardware.sorterPos[2]);
                telemetry.addData("Launch Speed: ", hardware.launcherLeft.getVelocity());
                telemetry.update();
            }
        }
    }
}
