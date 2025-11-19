package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name = "LLArtifactDistanceSensor")
public class SheaLLArtifactDistaneSensor extends LinearOpMode {
    Limelight3A limelight;
    TestBench
    private double distance;

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(9); // for
        //limelight.start();
        waitForStart();
        limelight.start(); //uses lots of energy, can be put before start if there is delay by prob fine

        //telemetry.addData("Id", table.getEntry("tid").getDoubleArray(new double[6]));
        while (opModeIsActive()) {
            YawPitchRollAngles orientation =

            LLResult result = limelight.getLatestResult();
            if(result != null && result.isValid()) {
                Pose3D botpose = result.getBotpose_MT2();
                telemetry.addData("Calculated Distance:", result.getTx());
                telemetry.addData("Target Y Offset:", result.getTy());
                telemetry.addData("Target Area Offset:", result.getTa()); //%of field of view
            }
            telemetry.update();
        }
    }
}