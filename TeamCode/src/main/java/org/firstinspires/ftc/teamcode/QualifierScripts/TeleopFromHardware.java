package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="QualTele")
public class TeleopFromHardware extends LinearOpMode {

    boolean prevLt = false;

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            // toggle intake
            /*if (gamepad2.left_trigger>0.05) {
                if (!prevLt) {
                    prevLt = true;
                    if (hardware.intaking) {
                        hardware.stopIntake();
                    } else {
                        hardware.tryIntake(true);
                    }
                }
            } else {
                prevLt = false;
            }*/
            /*if (gamepad2.a) {
                hardware.sorter.setPosition(Math.abs(hardware.sorter.getPosition() - 1));
                sleep(250);
            }
            //hardware.tryLaunchGreen(gamepad2.x);
            //hardware.tryLaunchPurple(gamepad2.b);
            //hardware.aimTurret("red"); // if we're on red side (ID 24)
            //if (gamepad2.y && !(gamepad2.x || gamepad2.b)) {
              //  hardware.stopLaunch();
            //}*/
            hardware.doDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        }
    }
}
