package org.firstinspires.ftc.teamcode.MiscScripts;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;

@Disabled
public class FourthWireTest extends LinearOpMode {

    public CRServo servo;
    public AnalogInput sensor, limitL, limitR;

    private int revolution = 1;
    private double prevVoltage = 1.5;
    private double minVoltage = 3d;
    private double maxVoltage = 0d;
    private byte limit = 0; //  0 none  /  1 left  /  2 right
    double target = 70d;


    @Override
    public void runOpMode() {
        servo = hardwareMap.get(CRServo.class, "axon");
        sensor = hardwareMap.get(AnalogInput.class, "fourth");
        limitL = hardwareMap.get(AnalogInput.class, "left");
        limitR = hardwareMap.get(AnalogInput.class, "right");

        waitForStart();

        // find the voltage limits
        telemetry.addLine("Calibrating...");
        telemetry.update();
        servo.setPower(0.125);
        while (limitL.getVoltage() < 1 && opModeIsActive()) {
            double crntVoltage = sensor.getVoltage();
            if (crntVoltage < minVoltage) minVoltage = crntVoltage;
            if (crntVoltage > maxVoltage) maxVoltage = crntVoltage;
        }
        servo.setPower(-0.125);
        while (limitR.getVoltage() < 1 && opModeIsActive()) {
            double crntVoltage = sensor.getVoltage();
            if (crntVoltage < minVoltage) minVoltage = crntVoltage;
            if (crntVoltage > maxVoltage) maxVoltage = crntVoltage;
        }
        servo.setPower(0.125);
        sleep(500);

        while (opModeIsActive()) {
            double crntVoltage = sensor.getVoltage();

            while (gamepad1.b) sleep(100);

            double dVoltage = crntVoltage - prevVoltage;
            if (Math.abs(crntVoltage - prevVoltage) > (maxVoltage - minVoltage) / 2) revolution -= (int)Math.signum(dVoltage);

            double guess = 90d * ((crntVoltage - minVoltage) / (maxVoltage - minVoltage)) + 45d;
            double overallGuess = guess + (double)(120 * revolution);

            if (gamepad1.yWasPressed()) target += 10d;
            if (gamepad1.aWasPressed()) target -= 10d;
            if (target < 0d) target = 0d;
            if (target > 180d) target = 180d;

//            if (overallGuess > target - 8d) {
//                servo.setPower(0.0625);
//            } else if (overallGuess < target + 8d) {
//                servo.setPower(-0.0625);
//            } else {
//                servo.setPower(0);
//            }

            if (limitL.getVoltage() > 1d) {
                limit = 1;
                revolution = -1;
                servo.setPower(0);
                target = 60d;
            } else if (limitR.getVoltage() > 1d) {
                limit = 2;
                revolution = 1;
                servo.setPower(0);
                target = 120d;
            }

            telemetry.addData("Voltage: ", sensor.getVoltage());
            telemetry.addData("Direction: ", servo.getPower());
            telemetry.addData("Guess: ", guess);
            telemetry.addData("Overall Guess: ", overallGuess);
            telemetry.addData("Target: ", target);
            telemetry.addData("Change: ", dVoltage);
            telemetry.addData("Revolution: ", revolution);
            telemetry.addData("Max Voltage: ", maxVoltage);
            telemetry.addData("Min Voltage: ", minVoltage);
            telemetry.addData("LimitL: ", limitL.getVoltage());
            telemetry.addData("LimitR: ", limitR.getVoltage());
            if (crntVoltage > maxVoltage - 0.02 || crntVoltage < minVoltage + 0.02) telemetry.addData("!!UNSURE!!    ", "(");
            telemetry.update();

            prevVoltage = crntVoltage;
        }
    }
}
