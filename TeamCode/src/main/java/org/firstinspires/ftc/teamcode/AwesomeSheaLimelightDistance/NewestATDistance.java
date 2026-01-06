package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;

import java.util.List;

@TeleOp//(name="a")

public class NewestATDistance extends LinearOpMode{
    Limelight3A limelight;
    CRServo servo, llServo;
    float lastSpeed = 0;
    boolean tracking = false;
    public void runOpMode(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        llServo = hardwareMap.get(CRServo.class, "servo");
        servo = hardwareMap.get(CRServo.class, "fakeTurret");
        waitForStart();
        limelight.start();
        limelight.pipelineSwitch(1);
        float speed = 1;
        while (opModeIsActive()) {
            if(gamepad2.a) {
                speed++;
                telemetry.addData("spd", speed);
                sleep(500);
            } else if(gamepad2.b) {
                speed--;
                telemetry.addData("spd", speed);
                sleep(500);
            }
            LLResult result = limelight.getLatestResult();
            double id = -1;
            //doesn't work for detecting if its true and it doesnt cause
            //errors not having it so if it aint broke
            //if(true){//result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    id = fiducial.getTargetXDegrees();
                }
                    telemetry.addData("Tag Found, X Degrees:", result.getTx());
                    setPos(0.03 * result.getTx());
                    result = limelight.getLatestResult();
                    telemetry.addData("Should be tracking... Servo Position:", llServo.getPower());
                    telemetry.update();


            //} else {
                //telemetry.addLine("No apriltag found");
                //servo.setPosition(0.5);
            //}
        }
    }

    void setPos(double posChange) {
        servo.setPower(3*posChange);
        llServo.setPower(posChange);
    }
}

