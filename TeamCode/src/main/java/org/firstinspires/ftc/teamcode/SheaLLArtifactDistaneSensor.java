package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
//import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name = "LLArtifactDistanceSensor")
public class SheaLLArtifactDistaneSensor extends LinearOpMode {
    Limelight3A limelight;
    CRServo servo;
//    TestBench bench = new TestBench();
    private double distance;

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        servo = hardwareMap.get(CRServo.class, "servo");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(9); // for ball detection
        //limelight.start();
        waitForStart();
        limelight.start(); //uses lots of energy, can be put before start if there is delay but prob fine

        //telemetry.addData("Id", table.getEntry("tid").getDoubleArray(new double[6]));
        while (opModeIsActive()) {
            //YawPitchRollAngles orientation =

            LLResult result = limelight.getLatestResult();
            if(result != null && result.isValid()) {
                Pose3D botpose = result.getBotpose_MT2();
                telemetry.addData("Calculated Distance:", result.getTx());
                telemetry.addData("Target Y Offset:", result.getTy());
                telemetry.addData("Target Area Offset:", result.getTa()); //%of field of view
            }
            else {
                telemetry.addLine("No valid target detected.");
            }
            telemetry.update();

            double limelightMountAngleDegrees = 25.0; // Angle Limelight is mounted
            double limelightLensHeightInches = 20.0; // Height of Limelight lens
            double goalHeightInches = 60.0; // Height of the target

            double angleToGoalDegrees = limelightMountAngleDegrees + result.getTy();
            double angleToGoalRadians = Math.toRadians(angleToGoalDegrees);
            double distanceFromLimelightToGoalInches = (goalHeightInches - limelightLensHeightInches) / Math.tan(angleToGoalRadians);

            servo.setPower(3.0);
        }
    }
}