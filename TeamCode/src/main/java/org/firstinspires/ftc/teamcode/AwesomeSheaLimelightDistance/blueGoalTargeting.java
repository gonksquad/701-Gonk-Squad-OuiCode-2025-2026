package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

@TeleOp
public class blueGoalTargeting extends LinearOpMode{
    Limelight3A limelight;
    Servo servo;
    Servo fakeTurret;
    double servoPosition;

    public void runOpMode(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        servo = hardwareMap.get(Servo.class, "servo");
        fakeTurret = hardwareMap.get(Servo.class, "fakeTurret");
        limelight.pipelineSwitch(6);

        waitForStart();
        limelight.start();
        servo.setPosition(0.5);
        servo.setPosition(0.0);

        while (opModeIsActive()){
            LLResult result = limelight.getLatestResult();
            double id = -1;

            if (result != null && result.isValid()){
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    id = fiducial.getFiducialId();
                }

                telemetry.addLine("Blue Goal Found.");
                telemetry.update();

                if (result.getTx() >5){
                    while (result.getTx() >5){
                        servoPosition = servo.getPosition() + 0.01;
                        if (servoPosition < 0){
                            servoPosition = 0.0
                        }
                        servo.setPosition(servoPosition);
                        result = limelight.getLatestResult();
                        telemetry.addData("Should be moving left... Servo Position:", servo.getPosition());
                        telemetry.update();
                        sleep(100);
                    }
                } else if (result.getTx() < -5){
                    while (result.getTx() <-5){
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
            }
        }
    }
}