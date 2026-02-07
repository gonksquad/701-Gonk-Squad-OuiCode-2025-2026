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
//        limelightServo.setPosition(servoPosition);
//        turretServo.setPosition(turretPosition);

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
                    servoPosition = betweenOneAndZero(limelightServo.getPosition() - 0.05);
                    limelightServo.setPosition(servoPosition);
                    telemetry.addData("Should be moving right... Servo Position:", limelightServo.getPosition());
                    telemetry.update();
                    result = limelight.getLatestResult();
                    sleep(100);
                }
            }

            distance = getDistanceFromTag(result.getTa());
            turretPosition =betweenLimAndLim(getTurretAngle(distance, limelightServo.getPosition()),1,0);
            if (!Double.isNaN(turretPosition)){
                turretServo.setPosition(turretPosition); //change limTwo from 0 to measured limit with protractor, .3??
            }
            //turretServo.setPosition(1-servoPosition);
            telemetry.addData("Calculated Distance:", distance);
            telemetry.addData("Limelight position:", limelightServo.getPosition());
            telemetry.addData("Turret position:", turretServo.getPosition());
            telemetry.update();
        } else {
            telemetry.addLine("No valid target detected.");
            telemetry.update();
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

    public double betweenLimAndLim(double number, double limOne, double limTwo){
        if (number>limOne){
            number = limOne;
        } else if (number < limTwo){
            number = limTwo;
        }
        return number;
    }

    public double getTurretAngle (double calcDist, double camPosition){
        double measuredTurretOffspace = 120; //replace
        camPosition = Math.toRadians(camPosition*270); //270 or whatever range of motion
        double turretAngle = Math.atan((Math.sqrt(Math.pow(calcDist,2)-Math.pow(calcDist*Math.cos(camPosition),2)))/(27.023-(calcDist*Math.cos(camPosition))));
        turretAngle = (Math.toRadians(turretAngle) + Math.toRadians(measuredTurretOffspace))/270; //120 or whatever range of motion
        turretAngle = betweenOneAndZero(turretAngle);
        return turretAngle;
    }
}