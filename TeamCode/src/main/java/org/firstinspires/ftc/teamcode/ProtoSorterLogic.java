package org.firstinspires.ftc.teamcode;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import kotlin.math.UMathKt;


@TeleOp(name="ProtoSorterLogic")
public class ProtoSorterLogic extends LinearOpMode{

    ColorSensor colorSensor;
    int red, green, blue;
    float[] hsvValues = new float[3];
    boolean nextPos = true;

    private ElapsedTime inouttakeTimer, changePosTimer = new ElapsedTime();
    Servo sortServo;
    DcMotor intakeMotor;
    /// SET SERVO LIMIT TO 240
    //AnalogInput sortAngle;
    double[] outtakePos = {1, 0.6, 0.2};
    double[] intakePos = {0, 0.4, 0.8};
    String[] currPos = {null, null, null};
    boolean intakePressed, outtakePressed = false;
    int currentPos;

    public void runOpMode() {
        sortServo = hardwareMap.get(Servo.class, "SortServo");
        colorSensor = hardwareMap.get(ColorSensor.class, "inColorSens");
        //sortAngle = hardwareMap.get(AnalogInput.class, "sortAnalog");
        waitForStart();
        currentPos = 0;
        sortServo.setPosition(intakePos[0]);

        int index = 0;
        while(opModeIsActive()) {
            /// testing sortservo positions
            /*if((gamepad1.right_bumper || gamepad1.left_bumper) && inouttakeTimer.milliseconds() >= 250) {
                index = (index+1)%outtakePos.length;
                inouttakeTimer.reset();
            }
            if(gamepad1.right_bumper) {
                sortServo.setPosition(intakePos[index]);
                telemetry.addData("Pos", sortServo.getPosition());
                telemetry.addData("Index", index);
            }*/
            detectFilled();
            if(changePosTimer.milliseconds() >= 1000) {
                nextPos = true;
            }

            telemetry.addData("timer", Math.round(changePosTimer.milliseconds()));

            telemetry.addData("current pos", getCurrentPos());
            telemetry.addData("servo pos", sortServo.getPosition());

            telemetry.addData("pos 0", currPos[0]);
            telemetry.addData("pos 1", currPos[1]);
            telemetry.addData("pos 2", currPos[2]);
            /*double angle = AngleUnit.normalizeDegrees(
                    sortAngle.getVoltage() * 360.0 / 3.3
            );*/
            //telemetry.addData("angle", angle);
            telemetry.update();
        }

    }


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

        if(intakePressed && currPos[currentPos] == null) { // if intaking and at an empty spot
            moveToEmptyPos(); // make sure you're at an empty spot
            detectFilled();
            intakeMotor.setPower(0.8); // start intaking
        } else if (intakePressed && currPos[currentPos] != null) { // if the position gets filled
            intakePressed = false; // stop intaking
            intakeMotor.setPower(0);
            moveToEmptyPos();
        }


    }

    public void moveToEmptyPos() {
        currentPos = getCurrentPos();

        if (currPos[currentPos] != null) {
            for (int i=0; i<3; i++) {
                if(currPos[i] == null) {
                    sortServo.setPosition(intakePos[i]);
                    changePosTimer.reset();
                }
            }
        }
    }
    public void moveToFilledPos() {
        currentPos = getCurrentPos();

        if (currPos[currentPos] == null) {
            for (int i=0; i<3; i++) {
                if(currPos[i] != null) {
                    sortServo.setPosition(intakePos[i]);
                }
            }
        }
    }
    public int getCurrentPos() {
        for (int i=0; i<3;i++) {
            if(sortServo.getPosition() == intakePos[i] || sortServo.getPosition() == outtakePos[i]) {
                return i;
            }
        }
        return 1;
    }
// Dany Reilleh Was Not Here
    public void detectFilled() {

        red = colorSensor.red();
        green = colorSensor.green();
        blue = colorSensor.blue();
        Color.RGBToHSV(red, green, blue, hsvValues);

        boolean validColor = hsvValues[1] > 0.55;
        float hue = hsvValues[0];
        if(hue > 220 && hue < 230 && validColor && nextPos) {
            currPos[getCurrentPos()] = "purple";
            moveToEmptyPos();
            telemetry.addData("found color",null);
            nextPos = false;
            changePosTimer.reset();
        } else if(hue > 155 && hue < 175 && validColor && nextPos) {
            currPos[getCurrentPos()] = "green";
            moveToEmptyPos();
            telemetry.addData("found color", null);
            nextPos = false;
            changePosTimer.reset();
        }
    }
}
