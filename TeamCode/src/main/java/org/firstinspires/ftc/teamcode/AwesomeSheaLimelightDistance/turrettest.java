package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class turrettest extends LinearOpMode {
    Servo turretServo;
    public double turretPosition = 0;
    @Override
    public void runOpMode() throws InterruptedException {
        turretServo = hardwareMap.get(Servo.class, "launcherYaw");
        waitForStart();
        turretServo.setPosition(turretPosition);
        while (opModeIsActive()) {
            for(double i=0; i<1; i+=0.1){
                turretServo.setPosition(i);
            }
            for(double i=1; i>0; i-=0.1){
                turretServo.setPosition(i);
            }
        }
    }
}