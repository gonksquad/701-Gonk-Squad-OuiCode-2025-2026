package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
//import org.firstinspires.ftc.teamcode.mechanisms.TestBench;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@TeleOp(name = "LLArtifactDistanceSensor")
public class SheaLLArtifactDistaneSensor extends LinearOpMode {
    Limelight3A limelight;
    Servo servo;
//    TestBench bench = new TestBench();
    private double distance;

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        servo = hardwareMap.get(Servo.class, "servo");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(1);
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
            }else {
                telemetry.addLine("No valid target detected.");

            }
            telemetry.update();

//            double limelightMountAngleDegrees = 0.0; // Angle Limelight is mounted
//            double limelightLensHeightInches = 0.0; // Height of Limelight lens
//            double goalHeightInches = 60.0; // Height of the target
//
//            double angleToGoalDegrees = limelightMountAngleDegrees + result.getTx();
//            //double angleToGoalDegrees = result.getTy();
//            double angleToGoalRadians = Math.toRadians(angleToGoalDegrees);
//            double distanceFromLimelightToGoalInches = (goalHeightInches - limelightLensHeightInches) / Math.tan(angleToGoalRadians);
//            //double distanceFromLimelightToGoalInches = (goalHeightInches) / Math.tan(angleToGoalRadians);

            if (result != null && result.isValid()) {
                double tx = result.getTx();
                if(tx > 20) { // tag is on the right
                    servo.setPosition(servo.getPosition()+tx/10);
                    telemetry.addLine("moving right (positive power) ");
                } else if(tx < -20) { // tag is on the left
                    servo.setPosition(servo.getPosition()+tx/10);
                    telemetry.addLine("moving left (negative power) ");
                }
            }
        }
    }
}