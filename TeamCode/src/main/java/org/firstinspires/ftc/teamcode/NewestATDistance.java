package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

@TeleOp
public class NewestATDistance extends LinearOpMode{
    Limelight3A limelight;
    Servo servo;
    public void runOpMode(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        servo = hardwareMap.get(Servo.class, "servo");
        waitForStart();
        limelight.start();
        limelight.pipelineSwitch(1);
        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            double id = 0;
            if(result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    id = fiducial.getTargetXDegrees();
                }

                telemetry.addData("tag found - x degrees:", id);
                telemetry.addData("tx:", result.getTx());
                telemetry.update();

//                while (result.getTx() >5){
//                    servo.setPosition(servo.getPosition()-0.1);
//                }
//                while (result.getTx() <-5){
//                    servo.setPosition(servo.getPosition()+0.1);
//                }
            } else {
                telemetry.addLine("No apriltag found");
                telemetry.update();
            }
        }
    }
}

