package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Hardware;

public class RRHardware extends Hardware {
    public RRHardware(HardwareMap hardwareMap) {
        super(hardwareMap);
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void intake() {
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);

    }
    public void shoot() { //gpp, both because spike mark 1
        
    }
    public void liftArtifact() {

    }
//    public void sorter?????() {
//
//    }
}
