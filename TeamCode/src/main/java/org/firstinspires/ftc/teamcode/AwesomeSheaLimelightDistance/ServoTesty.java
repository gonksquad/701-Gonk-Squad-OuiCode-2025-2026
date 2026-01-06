package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class ServoTesty extends LinearOpMode{
    Servo servo;
    double servoPosition;

    public void runOpMode() {
        servo = hardwareMap.get(Servo.class, "limeServo");
        waitForStart();
        while (opModeIsActive()){
            for (int i = 0; i <= 1; i+=0.1){
                servo.setPosition(i);
                telemetry.addData("Position:", i);
                telemetry.update();
                sleep(300);
            }
            for (int j = 1; j >= 0; j-=0.1){
                servo.setPosition(j);
                telemetry.addData("Position:", j);
                telemetry.update();
                sleep(300);
            }
        }
    }
}