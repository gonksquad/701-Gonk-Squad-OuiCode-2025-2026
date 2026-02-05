package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.StatesScripts.odoteleop;

@TeleOp
public class SpeedTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware hardware = new Hardware(hardwareMap);
        odoteleop odoteleop = new odoteleop(hardwareMap);
        int testTps = 1700;
        waitForStart();

       // hardware.launcherTurn.setPower(0d); SHOULDNT BE  COMMENTED

        while (opModeIsActive()) {
            if (gamepad2.aWasPressed()) {
                testTps -= 100;
            }
            if(gamepad2.yWasPressed()) {
                testTps += 100;
            }

            /*if (gamepad2.y && hardware.outtakeTransferLeft.getPosition() != 0.2) {
                hardware.outtakeTransferLeft.setPosition(0.2);
                hardware.outtakeTransferRight.setPosition(0.2);
            } else if (hardware.outtakeTransferLeft.getPosition() != 0.9) {
                hardware.outtakeTransferLeft.setPosition(0.9);
                hardware.outtakeTransferRight.setPosition(0.9);
            }*/

            hardware.tryLaunch(gamepad2.x, 1, testTps);
            hardware.sorterContents[0] = 1;
            if (gamepad2.b && !gamepad2.right_bumper) {
                hardware.stopLaunch();
            }

            telemetry.addData("Launcher TPS: ", testTps);
            telemetry.addData("", odoteleop.getMotorPower(testTps/100, true));
            telemetry.update();
        }
    }
}
