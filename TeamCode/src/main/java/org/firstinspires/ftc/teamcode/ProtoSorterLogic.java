package org.firstinspires.ftc.teamcode;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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
    String[] sorterPos = {null, null, null}; // previously currPoa
    boolean intakePressed, outtakePressed = false;
    int currentPos;
    DcMotor outtakeMotor;

    public void runOpMode() {
        sortServo = hardwareMap.get(Servo.class, "SortServo");
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtakeMotor");
        colorSensor = hardwareMap.get(ColorSensor.class, "inColorSens");
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



            telemetry.addData("timer", Math.round(changePosTimer.milliseconds()));

            telemetry.addData("current pos", getCurrentPos());
            telemetry.addData("servo pos", sortServo.getPosition());

            telemetry.addData("pos 0", sorterPos[0]);
            telemetry.addData("pos 1", sorterPos[1]);
            telemetry.addData("pos 2", sorterPos[2]);

            telemetry.update();
        }

    }

    public void sorterLogic() {

        if(gamepad2.right_bumper && inouttakeTimer.milliseconds() >= 250) {
            outtakePressed = !outtakePressed;
            intakePressed = false;
            inouttakeTimer.reset();
        }
        if(gamepad2.left_bumper && inouttakeTimer.milliseconds() >= 250) {
            intakePressed = !intakePressed;
            outtakePressed = false;
            inouttakeTimer.reset();
        }

        if(!outtakePressed) {
            detectFilled();
            if(changePosTimer.milliseconds() >= 1000) {
                nextPos = true;
            }
        }

        if(intakePressed) {
            moveToEmptyPos();
            if(changePosTimer.milliseconds() >= 500) {
                intakeMotor.setPower(0.8);
                detectFilled();
            }
        }

        if(outtakePressed) {
            moveToFilledPos();
            if(changePosTimer.milliseconds() >= 1000) {
              // do 4 bar
              outtakeMotor.setPower(0.8);
              sorterPos[currentPos] = null;
              // wait awhile to set outtakePressed to false somehow
            }
        }


    }

    public void moveToEmptyPos() {
        currentPos = getCurrentPos();

        if (sorterPos[currentPos] != null) {
            for (int i=0; i<sorterPos.length; i++) {
                if(sorterPos[i] == null) {
                    sortServo.setPosition(intakePos[i]);
                    changePosTimer.reset();
                }
            }
        }
    }
    public void moveToFilledPos() {
        currentPos = getCurrentPos();

        if (sorterPos[currentPos] == null) {
            for (int i=0; i<sorterPos.length; i++) {
                if(sorterPos[i] != null) {
                    sortServo.setPosition(intakePos[i]);
                    changePosTimer.reset();
                }
            }
        }
    }
    public int getCurrentPos() {
        for (int i=0; i<sorterPos.length;i++) {
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
            sorterPos[getCurrentPos()] = "purple";
            moveToEmptyPos();
            telemetry.addData("found color",null);
            nextPos = false;
            intakePressed = false;
            changePosTimer.reset();
        } else if(hue > 155 && hue < 175 && validColor && nextPos) {
            sorterPos[getCurrentPos()] = "green";
            moveToEmptyPos();
            telemetry.addData("found color", null);
            nextPos = false;
            intakePressed = false;
            changePosTimer.reset();
        }
    }
}
