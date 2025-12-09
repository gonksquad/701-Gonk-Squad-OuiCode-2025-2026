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
    public void intake1() { //gpp order
        sorter.setPosition(/*intake pos1*/);
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }
    public void intake2() { //gpp order
        sorter.setPosition(/*intake pos2*/);
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }
    public void intake3() { //gpp order
        sorter.setPosition(/*intake pos3*/);
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }
    public void shootgpp() { //gpp start, both because spike mark 1
        sorter.setPosition(/* outtake pos1*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
        sorter.setPosition(/* outtake pos2*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
        sorter.setPosition(/* outtake pos3*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
//        1,2,3(outtake servo pos)
        //black, grey, blue
    }
    public void shootpgp() { //gpp start, both because spike mark 1
        sorter.setPosition(/* outtake pos2*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
        sorter.setPosition(/* outtake pos1*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
        sorter.setPosition(/* outtake pos3*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
//        2,1,3(outtake servo pos)
    }
    public void shootppg() { //gpp start, both because spike mark 1
        sorter.setPosition(/* outtake pos2*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
        sorter.setPosition(/* outtake pos3*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
        sorter.setPosition(/* outtake pos1*/);
        outtakeTransfer.setPosition(1);
        sleep(500);
        outtakeTransfer.setPosition(0);
//        2,3,1
    }
}
