package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
//import org.firstinspires.ftc.robotcore.external.toplevel.NetworkTableInstance;
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

@TeleOp(name = "Limelight Servo Tracker")
public class LLATDist extends LinearOpMode {

    private Servo camServo;
    Limelight3A limelight;

    // Servo limits (tune these for your mount)
    private final double SERVO_MIN = 0.2;
    private final double SERVO_MAX = 0.8;

    // How aggressively the servo responds to tx
    private final double kP = 0.005;  // proportional gain

    @Override
    public void runOpMode() throws InterruptedException {

        camServo = hardwareMap.get(Servo.class, "servo");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(1);

        // Start centered
        double servoPos = 0.5;
        camServo.setPosition(servoPos);

        waitForStart();

        limelight.start();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if(result != null && result.isValid()) {
                telemetry.addData("Calculated Distance:", result.getTx());
                telemetry.addData("Target Y Offset:", result.getTy());
                telemetry.addData("Target Area Offset:", result.getTa()); //%of field of view

                servoPos -= result.getTx() * kP;
                servoPos = Math.max(SERVO_MIN, Math.min(SERVO_MAX, servoPos));
                camServo.setPosition(servoPos);
                telemetry.addData("tx", result.getTx());
                telemetry.addData("Servo Pos", servoPos);
            }else {
                servoPos = 0.5;
                camServo.setPosition(servoPos);
                telemetry.addLine("No valid target detected. Centering servo.");
            }


            telemetry.update();
        }
    }
}
