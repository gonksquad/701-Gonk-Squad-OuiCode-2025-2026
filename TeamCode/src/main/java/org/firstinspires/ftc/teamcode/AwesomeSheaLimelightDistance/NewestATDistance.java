package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

@TeleOp
public class NewestATDistance extends LinearOpMode{
    Limelight3A limelight;
    Servo servo;
    double servoPosition;
    public void runOpMode(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        servo = hardwareMap.get(Servo.class, "servo");
        waitForStart();
        limelight.start();
        limelight.pipelineSwitch(1);
        servo.setPosition(0.5);

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            double id = 0;
            if(result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    id = fiducial.getTargetXDegrees();
                }

                telemetry.addData("Tag Found, X Degrees:", id);
                telemetry.update();

                if (result.getTx() >5){
                    while (result.getTx() >5){
                        servoPosition = servo.getPosition() + 0.01;
                        if (servoPosition < 0){
                            servoPosition = 0.0;
                        }
                        servo.setPosition(servoPosition);
                        result = limelight.getLatestResult();
                        telemetry.addData("Should be moving left... Servo Position:", servo.getPosition());
                        telemetry.update();
                        sleep(100);
                    }
                } else if (result.getTx() < 5){
                    while (result.getTx() <5){
                        servoPosition = servo.getPosition() - 0.01;
                        if (servoPosition > 1){
                            servoPosition = 1.0;
                        }
                        servo.setPosition(servoPosition);
                        result = limelight.getLatestResult();
                        telemetry.addData("Should be moving right... Servo Position:", servo.getPosition());
                        telemetry.update();
                        sleep(100);
                    }
                }
                telemetry.update();

            } else {
                telemetry.addLine("No apriltag found");
                telemetry.update();
                servo.setPosition(0.5);
            }
        }
    }
}

