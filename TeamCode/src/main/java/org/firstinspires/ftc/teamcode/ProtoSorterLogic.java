package org.firstinspires.ftc.teamcode;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;



public class ProtoSorterLogic extends LinearOpMode{

    ColorSensor colorSensor;
    int red, green, blue;
    float[] hsvValues = new float[3];

    private ElapsedTime inouttakeTimer;
    Servo sortServo;
    DcMotor intakeMotor;
    double[] intakePos = {0, 0.4, 8};
    double[] outtakePos = {1, 0.6, 0.2};
    boolean[] posFilled = {false, false, false};
    boolean intakePressed = false;
    boolean outtakePressed = false;
    int currentPos;

    public void runOpMode() {
        sortServo = hardwareMap.get(Servo.class, "SorterServo");
        waitForStart();

        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();
        Color.RGBToHSV(red, green, blue, hsvValues);


    }

    ///////////////////////// psuedocode ////////////////////////////////
    /// What i need to keep track of
    // which pos is filled or not, which pos were at
    //
    // sorter stays in position to intake until outtake button is pressed
    // in which case it moves to the closest filled position. after outtaking
    // or intaking move to the closest free position, if no positions are free,
    // dont allow intake to spin. Also, if all positions filled, keep at an
    // outtake positon.

    // resting and not full -> sorter not filled pos
    // intake pressed and not full -> check if at intake pos then intake
        // spin sorter to next not filled position once succesfully intake
        /////////need to sense to check for successful intake
    // if outtake clicked -> intake off, closest filled pos, shoot, set current pos to not filled
    //

    public void sorterLogic() {

        /// need to remember to set intake/outtake to false after doing respective action
        if(gamepad2.left_bumper && inouttakeTimer.milliseconds() >= 250) {
            outtakePressed = false;
            intakePressed = !intakePressed; // does this so a second press turns it off
            inouttakeTimer.reset();
        }

        if(gamepad2.right_bumper && inouttakeTimer.milliseconds() >= 250) {
            outtakePressed = !outtakePressed;
            intakePressed = false;
            inouttakeTimer.reset();
        }

        if(!outtakePressed) {
            moveToEmptyPos();
        }

        if(intakePressed && !posFilled[currentPos]) {
            moveToEmptyPos();
            intakeMotor.setPower(0.8);
        } else if (intakePressed && posFilled[currentPos]) {
            intakePressed = false;
        }
    }

    public void moveToEmptyPos() {
        currentPos = getCurrentPos();

        if (posFilled[currentPos]) {
            for (int i=0; i<3; i++) {
                if(!posFilled[i]) {
                    sortServo.setPosition(intakePos[i]);
                }
            }
        }
    }
    public int getCurrentPos() {
        for (int i=0; i<3;i++) {
            if(Math.abs(sortServo.getPosition() - intakePos[i]) <= 0.05) {
                return i;
            } else if (Math.abs(sortServo.getPosition() - outtakePos[i]) <= 0.05) {
                return i;
            }
        }
        return -1;
    }

    public String detectArtifactColor() {
        boolean validColor = hsvValues[1] > 0.55;
        float hue = hsvValues[0];
        if(hue > 220 && hue < 230 && validColor) {
            return "purple";
        } else if(hue > 155 && hue < 175 && validColor) {
            return "green";
        }
        return null;
    }
}
