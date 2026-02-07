package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp
public class Distance extends LinearOpMode{
    Limelight3A limelight;
    public double distance;

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        waitForStart();
        limelight.start(); //uses lots of energy, can be put before start if there is delay but prob fine

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if(result != null && result.isValid()) {
                distance = findDistance(result);
                telemetry.addData("Calculated Distance:", distance);
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
        }
    }

    public double findDistance(LLResult result){
        double dist = 176.3168 * (Math.pow(result.getTx(),-0.4998937));
        return dist;
    }

    //y = 176.3168*x^-0.4998937
}
