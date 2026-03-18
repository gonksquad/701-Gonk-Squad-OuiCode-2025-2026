package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="limelighttest")
public class PomeloLight extends LinearOpMode {

    Hardware hardware = new Hardware(hardwareMap);
    String side = "blue";

    @Override
    public void runOpMode() throws InterruptedException {
        switch (side) {
            case "blue":
                hardware.limelight.pipelineSwitch(1); // pretend this detects april tag id 20
                break;
            case "red":
                hardware.limelight.pipelineSwitch(2); // pretend this detects april tag id 24
                break;
        }
        waitForStart();
       while(opModeIsActive()) {
        aimTurret();
       }
    }

    // uh jacob what do u want from me :(
    public void aimTurret() {


        LLResult result = hardware.limelight.getLatestResult();
        telemetry.addData("result", result);
        if (result != null && result.isValid()) {
            double tx = result.getTx();
            double ratio = 90d/274d; // approximate teeth of servo to teeth of launcher
            if(tx > 4f) { // tag is on the right
                //hardware.launcherTurn.setPower(0.8 * ratio);
              //  hardware.limelightTurn.setPower(0.8);
            } else if(tx < -4) { // tag is on the left
                //hardware.launcherTurn.setPower(-0.8 * ratio);
               // hardware.limelightTurn.setPower(-0.8);
            } else { // tag is within left and right bounds
                //hardware.launcherTurn.setPower(0);
               // hardware.limelightTurn.setPower(0);

            }

        }
    }
}
