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
//        turretServo.setPosition(turretPosition);

        while (opModeIsActive()) {
            if (gamepad1.a){
                turretPosition += 0.05;
                turretServo.setPosition(betweenOneAndZero(turretPosition));
            } else if (gamepad1.b){
                turretPosition -= 0.05;
                turretServo.setPosition(betweenOneAndZero(turretPosition));
            }
            sleep(200);
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                if (result.getTx() >5){
                    while (result.getTx() >5){
                        servoPosition = limelightServo.getPosition() + 0.05;
                        if (servoPosition < 0){
                            servoPosition = 0.0;
                        }
                        limelightServo.setPosition(servoPosition);
                        result = limelight.getLatestResult();
                        telemetry.addData("Should be moving left... Servo Position:", limelightServo.getPosition());
                        telemetry.update();
                        sleep(100);
                    }
                } else if (result.getTx() < -5){
                    while (result.getTx() <-5){
                        servoPosition = limelightServo.getPosition() - 0.01;
                        if (servoPosition > 1){
                            servoPosition = 1.0;
                        }
                        limelightServo.setPosition(servoPosition);
                        result = limelight.getLatestResult();
                        telemetry.addData("Should be moving right... Servo Position:", limelightServo.getPosition());
                        telemetry.update();
                        sleep(100);
                    }
                }
                distance = getDistanceFromTag(result.getTa());
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
//            result = limelight.getLatestResult();
//            turretPosition = getTurretAngle(result, distance);
//            if((turretPosition >= 0) && (turretPosition <= 1)){
//                turretServo.setPosition(turretPosition);
//            }
//            else if (turretPosition<0){
//                turretPosition = 0;
//                turretServo.setPosition(turretPosition);
//            } else if (turretPosition>1){
//                turretPosition = 1;
//                turretServo.setPosition(turretPosition);
//            }
//            telemetry.addData("turretPosition: then sleep ", turretPosition);
//            telemetry.update();
//            sleep(500);
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
//    public double getTurretAngle (LLResult result,double calcDist){
//        //double turretAngle = Math.atan((calcDist*(Math.sin(result.getTx())))/(27.023-(calcDist*(Math.cos(result.getTx())))));
//        double turretAngle = Math.atan(  (Math.sqrt (Math.pow(calcDist,2)-Math.pow(calcDist*Math.cos((result.getTx()+servoPosition)),2)) )  /  (27.023-(calcDist*Math.cos(result.getTx()+servoPosition)))  );
////        turretAngle = (turretAngle/180)*100;
////        if ((Math.abs(turretAngle-turretPosition))>10){
////            return turretAngle;
////        }
////        return turretPosition;
//        return turretAngle;
//    }
}