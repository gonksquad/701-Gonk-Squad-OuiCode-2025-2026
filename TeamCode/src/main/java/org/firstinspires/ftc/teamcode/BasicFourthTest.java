package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;

@Disabled
public class BasicFourthTest extends LinearOpMode {

    public CRServo servo;
    public AnalogInput sensor;

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

        waitForStart();

        // find the voltage limits
        telemetry.addLine("Calibrating...");
        telemetry.update();
        servo.setPower(0);

        while (opModeIsActive()) {
            double crntVoltage = sensor.getVoltage();

            if (crntVoltage > maxVoltage) maxVoltage = crntVoltage;
            if (crntVoltage < minVoltage) minVoltage = crntVoltage;

            while (gamepad1.b) sleep(100);

            double dVoltage = crntVoltage - prevVoltage;
            if (Math.abs(crntVoltage - prevVoltage) > (maxVoltage - minVoltage) / 2) revolution -= (int)Math.signum(dVoltage);

            double guess = 90d * ((crntVoltage - minVoltage) / (maxVoltage - minVoltage)) + 45d;
            double overallGuess = guess + (double)(120 * revolution);

            if (gamepad1.y) {
                servo.setPower(-1);
            } else if (gamepad1.a) {
                servo.setPower(1);
            } else {
                servo.setPower(0);
            }

//            if (overallGuess > target - 8d) {
//                servo.setPower(0.0625);
//            } else if (overallGuess < target + 8d) {
//                servo.setPower(-0.0625);
//            } else {
//                servo.setPower(0);
//            }

            telemetry.addData("Voltage: ", sensor.getVoltage());
            telemetry.addData("Direction: ", servo.getPower());
            telemetry.addData("Guess: ", guess);
            telemetry.addData("Overall Guess: ", overallGuess);
            telemetry.addData("Target: ", target);
            telemetry.addData("Change: ", dVoltage);
            telemetry.addData("Revolution: ", revolution);
            telemetry.addData("Max Voltage: ", maxVoltage);
            telemetry.addData("Min Voltage: ", minVoltage);

            if (crntVoltage > maxVoltage - 0.02 || crntVoltage < minVoltage + 0.02) telemetry.addData("!!UNSURE!!    ", "(");
            telemetry.update();

            prevVoltage = crntVoltage;
        }
    }
}
