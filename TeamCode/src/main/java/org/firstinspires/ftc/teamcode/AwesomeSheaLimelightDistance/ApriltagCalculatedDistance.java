// CHANGE TURRET AND CAM ZERO LIMIT TO BE FACING EACH OTHER






package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import org.firstinspires.ftc.robotcore.external.toplevel.NetworkTableInstance;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ApriltagCalculatedDistance extends LinearOpMode {
    private double distance;
    double camHeight = 25; //cm
    double tagHeight = 75; //cm
    Limelight3A limelight;
    Servo limelightServo;
    Servo turretServo;
    double servoPosition = 0.5;
    public double turretPosition = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelightServo = hardwareMap.get(Servo.class, "limeservo");
        turretServo = hardwareMap.get(Servo.class, "launcherYaw");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        waitForStart();
        limelight.start();
        limelightServo.setPosition(servoPosition);
        turretServo.setPosition(turretPosition);

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                if (result.getTx() >5){
                    while (result.getTx() >5){
                        servoPosition = betweenOneAndZero(limelightServo.getPosition() + 0.05);
                        limelightServo.setPosition(servoPosition);
                        telemetry.addData("Should be moving left... Servo Position:", limelightServo.getPosition());
                        telemetry.update();
                        result = limelight.getLatestResult();
                        sleep(100);
                    }
                } else if (result.getTx() < -5){
                    while (result.getTx() <-5){
                        servoPosition = betweenOneAndZero(limelightServo.getPosition() - 0.01);
                        limelightServo.setPosition(servoPosition);
                        telemetry.addData("Should be moving right... Servo Position:", limelightServo.getPosition());
                        telemetry.update();
                        result = limelight.getLatestResult();
                        sleep(100);
                    }
                }

                distance = getDistanceFromTag(result.getTa());
                turretServo.setPosition(betweenOneAndZero(getTurretAngle(distance, limelightServo.getPosition())));
                telemetry.addData("Calculated Distance:", distance);
                telemetry.addData("ty:", result.getTy());
                telemetry.addData("ta:", result.getTa());
                telemetry.addData("tx", result.getTx());
                telemetry.addData("Limelight position:", limelightServo.getPosition());
                telemetry.addData("Turret position:", turretServo.getPosition());
                telemetry.update();
            } else {
                telemetry.addLine("No valid target detected.");
            }

        }
    }
    public double getDistanceFromTag(double ta){
        double scale = 176.3168;
        double dist = (scale * (Math.pow(ta,-0.4998937)));
        return dist;
        //y = 184.8972*x^-0.5056956
        //y = 176.3168*x^-0.4998937
    }
    public double betweenOneAndZero(double number){
        if (number>1){
            number = 1;
        } else if (number < 0){
            number = 0;
        }
        return number;
    }

    public double getTurretAngle (double calcDist, double camPosition){
        camPosition = camPosition*270; //270 or whatever range of motion
        double turretAngle = Math.atan((Math.sqrt(Math.pow(distance,2)-Math.pow(distance*Math.cos(camPosition),2)))/(27.023-(distance*Math.cos(camPosition))));
        turretAngle = turretAngle/120; //120 or whatever range of motion
        return turretAngle;
    }
}