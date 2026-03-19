// used to be TeleopFromHardware
package org.firstinspires.ftc.teamcode.StatesScripts;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.MiscScripts.Hardware;

@Disabled
//@TeleOp (name="ONECONTROLLER-Worlds")
public class StatesTeleOneController extends LinearOpMode {

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
        odoteleop odoteleop = new odoteleop(hardwareMap, true, false);
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
            if (gamepad1.psWasPressed()) { // toggle manual override
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

            if (gamepad1.y && !(gamepad1.a || gamepad1.b)) {
                hardware.intaking = false;
                hardware.intake.setPower(-1);
            }

            if (manual) {
                if (gamepad1.a) {
                    hardware.intake.setPower(1d);
                } else if (gamepad1.b) {
                    hardware.intake.setPower(0d);
                }
                double  launchVelocity = gamepad1.left_trigger * 700 + 1000;
                if (launchVelocity < 1100) launchVelocity = 0;
                hardware.launcherRight.setVelocity(launchVelocity);
                hardware.launcherLeft.setVelocity(launchVelocity);

                if (hardware.sorterOffset != prevOffset) { // don't set position if it is already set
                    prevOffset = hardware.sorterOffset;
                    hardware.sorter.setPosition(hardware.sorterOffset);
                }
            } else {

                if (gamepad1.dpadDownWasPressed()) {
                    hardware.forgetIntake();
                }
                if (gamepad1.dpadUpWasPressed()) {
                    hardware.forgetLaunch();
                }

                hardware.tryIntake(gamepad1.a);
                if (gamepad1.b && !gamepad1.a && !gamepad1.right_bumper) {
                    hardware.stopIntake();
                    hardware.stopLaunch();
                }
                if(gamepad1.xWasPressed()){
                    odoteleop.resetOdoPos(true);
                }
                // 1 = purple, 2 = green. did this so that 0 can be either to help drivers
                hardware.tryLaunch(gamepad1.right_trigger > 0.125, 0, 1350);
                hardware.tryLaunch(gamepad1.left_trigger > 0.125, 0, 1150);

                //odo auto aiming
                telemetry.addData("limelightpos", hardware.limelightTurn.getPosition());
                //hardware.launchTimer.reset(); ///// THIS SINGLE LINE WAS WHY LAUNCHING DIDNT WORK :sob: :cool:
                telemetry.addData("YURRRRR:    ", odoteleop.odoAimTurret(autoAim, true, false));
                //
//
                telemetry.addData("Sorter Position: ", hardware.sorter.getPosition());
                telemetry.addData("Sorter Contents: ", "%d, %d, %d", hardware.sorterContents[0], hardware.sorterContents[1], hardware.sorterContents[2]);
                telemetry.addData("Launch Speed: ", hardware.launcherLeft.getVelocity());

                /// touch sensor stuff is temp, just for testing
                //telemetry.addData("limit left", limitLeft.isPressed());
                //telemetry.addData("limit right", limitRight.isPressed());

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
