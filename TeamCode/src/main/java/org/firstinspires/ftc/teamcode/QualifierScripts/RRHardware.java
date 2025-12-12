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
    public void intake1() { //g
        sorter.setPosition(.2); //blue
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }
    public void intake2() { //p
        sorter.setPosition(.6); //black
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }
    public void intake3() { //p
        sorter.setPosition(1); //grey
        intake.setPower(1);
        sleep(1000);
        intake.setPower(0);
    }

    /*

    intake pos: .2, .6, 1.0
    outtake pos: 0, .4, .8
    between blue/black: .25 (p)
    between black/grey: .65 (g)
    between grey/blue: 1.05 (p)
     */
    public void shootgpp() { //gpp start, both because spike mark 1
        sorter.setPosition(.4);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.8);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(0);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
//        1,2,3(outtake servo pos)
        //black, grey, blue
    }
    public void shootpgp() { //gpp start, both because spike mark 1
        sorter.setPosition(0);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.4);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.8);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
//        2,1,3(outtake servo pos)
    }
    public void shootppg() { //gpp start, both because spike mark 1
        sorter.setPosition(0);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.8);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
        sorter.setPosition(.4);
        outtakeTransfer.setPosition(0);
        sleep(500);
        outtakeTransfer.setPosition(.9);
//        2,3,1
    }
}
