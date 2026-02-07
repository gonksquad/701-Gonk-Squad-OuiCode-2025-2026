// used to be TeleopFromHardware
package org.firstinspires.ftc.teamcode.StatesScripts;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp (name="AAA-REDCLOSE-StatesTele")
public class StatesTeleRedClose extends LinearOpMode {

    boolean manual = false;
    double prevOffset = 0d;
    final double offsetAmount = 0d;
    boolean prevSorterL = false;
    boolean prevSorterR = false;
    boolean prevA = false;
    boolean prevB = false;
    boolean slowMode = false;
    boolean autoAim = true;


    /// touch sensor stuff is temp, just for testing
    TouchSensor limitLeft, limitRight;

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);
        odoteleop odoteleop = new odoteleop(hardwareMap, false, false);
        /// touch sensor stuff is temp, just for testing
        limitLeft = hardwareMap.touchSensor.get("limitLeft");
        limitRight = hardwareMap.touchSensor.get("limitRight");

        hardware.limelight.pipelineSwitch(0);
        waitForStart();
        hardware.limelight.start();
        hardware.outtakeTransferLeft.setPosition(hardware.liftPos[0]); //down position
        hardware.outtakeTransferRight.setPosition(1-hardware.liftPos[0]); // down position
        hardware.sorter.setPosition(0.8);

        while (opModeIsActive()) {
            if (gamepad2.psWasPressed()) { // toggle manual override
                manual = !manual;
                hardware.stopLaunch();
                hardware.stopIntake();
                if (manual) {
                    gamepad1.rumble(200);
                } else {
                    gamepad1.rumble(200);
                    gamepad1.rumble(200);
                }
            }

            if (gamepad1.leftBumperWasPressed()) {

            }

            if (gamepad2.y && !(gamepad2.a || gamepad2.b)) {
                hardware.intaking = false;
                hardware.intake.setPower(-1);
            }

            slowMode = gamepad1.left_bumper;
            if (slowMode) {
                hardware.doDrive(-gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x, .75, .75, 0.5);
            } else {
                hardware.doDrive(-gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x, 1d, 1d, 0.67);
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

                if (gamepad2.dpadDownWasPressed()) {
                    hardware.forgetIntake();
                }
                if (gamepad2.dpadUpWasPressed()) {
                    hardware.forgetLaunch();
                }

                if(gamepad1.yWasPressed()){
                    autoAim=!autoAim;
                }
                if(gamepad1.aWasPressed()){
                    odoteleop.resetOdoPos(false);
                }
                hardware.tryIntake(gamepad2.a);
                if (gamepad2.b && !gamepad2.a && !gamepad2.right_bumper) {
                    hardware.stopIntake();
                    hardware.stopLaunch();
                }
                // 1 = purple, 2 = green. did this so that 0 can be either to help drivers
                hardware.tryLaunch(gamepad2.right_bumper,          1, 1150);
                hardware.tryLaunch(gamepad2.right_trigger > 0.125, 1, 1350);
                hardware.tryLaunch(gamepad2.left_bumper,           2, 1150);
                hardware.tryLaunch(gamepad2.left_trigger > 0.125,  2, 1350);
                hardware.tryLaunch(gamepad2.x,                     0, 1150);
//                hardware.tryLaunchGreen(gamepad2.dpad_down);
//                if (gamepad2.dpad_right && !(gamepad2.dpad_up || gamepad2.dpad_down)) {
//                    hardware.stopLaunch();
//                }

                //odo auto aiming
                telemetry.addData("limelightpos", hardware.limelightTurn.getPosition());
                //hardware.launchTimer.reset();
                hardware.launcherTurn.setPosition(hardware.launcherTurn.getPosition()+gamepad2.left_stick_x/100f);
                telemetry.addData("YURRRRR:    ", odoteleop.odoAimTurret(autoAim, false, true));
                telemetry.addData("robotX", odoteleop.getOdoData(org.firstinspires.ftc.teamcode.StatesScripts.odoteleop.odoDataTypes.X));
                telemetry.addData("robotY", odoteleop.getOdoData(org.firstinspires.ftc.teamcode.StatesScripts.odoteleop.odoDataTypes.Y));
                telemetry.addData("robotRot", odoteleop.getOdoData(org.firstinspires.ftc.teamcode.StatesScripts.odoteleop.odoDataTypes.HEADING));
//
//
                telemetry.addData("Sorter Position: ", hardware.sorter.getPosition());
                telemetry.addData("Sorter Contents: ", "%d, %d, %d", hardware.sorterContents[0], hardware.sorterContents[1], hardware.sorterContents[2]);
                telemetry.addData("Launch Speed: ", hardware.launcherLeft.getVelocity());

                /// touch sensor stuff is temp, just for testing
                telemetry.addData("limit left", limitLeft.isPressed());
                telemetry.addData("limit right", limitRight.isPressed());

                telemetry.addData("lift pos left", hardware.outtakeTransferLeft.getPosition());
                telemetry.addData("launch timer", hardware.launchTimer.milliseconds());

                double fgVolts = hardware.floodgate.getVoltage();

                telemetry.addData("Battery Voltage", fgVolts);
                telemetry.addData("Battery Amperage", fgVolts * 80.0 / 3.3);
                telemetry.update();
            }
        }
    }
}
