package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="SorterTest")
public class SorterTest extends LinearOpMode {

    boolean prevA = false;
    boolean prevB = false;
    boolean prevC = false;
    int nextPos, nextPos1 = 0;
    double[] liftPos;

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);
        liftPos = hardware.liftPos;
        waitForStart();
        while (opModeIsActive()) {
            /*hardware.limelightTurn.setPower(0.5);
            if (gamepad1.b && !gamepad1.a) {
                hardware.limelightTurn.setPower(0);
            }*/
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
                    //hardware.sorter.setPosition(hardware.outtakePos[nextPos]);
                    hardware.outtakeTransferLeft.setPosition(liftPos[nextPos]);
                    hardware.outtakeTransferRight.setPosition(liftPos[nextPos]);
                    telemetry.addLine("sigma");

                    nextPos ++;
                    if (nextPos >= 2) nextPos -= 2;
                }
                prevB = true;
            } else {
                prevB = false;
            }

            if (gamepad1.left_bumper) {
                if (!prevC) {
                }
                prevC = true;
            } else {
                prevC = false;
            }

            hardware.launcherTurn.setPower(gamepad1.left_stick_y);

            //telemetry.addData("limelight", hardware.limelightTurn.getPower());
            telemetry.addData("Position: ", nextPos1);
            telemetry.addData("Position Value: ", hardware.outtakeTransferLeft.getPosition());
            telemetry.update();
        }
    }
}
