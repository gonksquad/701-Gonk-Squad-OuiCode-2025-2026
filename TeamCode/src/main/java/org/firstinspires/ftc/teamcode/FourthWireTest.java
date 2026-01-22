package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class FourthWireTest extends LinearOpMode {

    public CRServo servo;
    public AnalogInput sensor;

    private int revolution = 0;
    private double prevVoltage = 1.5;
    private double minVoltage = 3d;
    private double maxVoltage = 0d;

    @Override
    public void runOpMode() {
        servo = hardwareMap.get(CRServo.class, "axon");
        sensor = hardwareMap.get(AnalogInput.class, "fourth");

        waitForStart();
        while (opModeIsActive()) {

           if (gamepad1.b) servo.setPower(0);
           else if (gamepad1.a) servo.setPower(0.125);
           else if (gamepad1.y) servo.setPower(-0.125);

            double crntVoltage = sensor.getVoltage();
            double dVoltage = crntVoltage - prevVoltage;
            if (Math.abs(crntVoltage - prevVoltage) > 2d) revolution -= (int)Math.signum(dVoltage);

            if (crntVoltage < minVoltage) minVoltage = crntVoltage;
            if (crntVoltage > maxVoltage) maxVoltage = crntVoltage;

            double guess = 280d * ((crntVoltage - minVoltage) / (maxVoltage - minVoltage)) + 40d;

            telemetry.addData("Voltage: ", sensor.getVoltage());
            telemetry.addData("Direction: ", servo.getPower());
            telemetry.addData("Guess: ", guess);
            telemetry.addData("Overall Guess: ", guess + (double)(360 * revolution));
            telemetry.addData("Change: ", dVoltage);
            telemetry.addData("Revolution: ", revolution);
            telemetry.addData("Max Voltage: ", maxVoltage);
            telemetry.addData("Min Voltage: ", minVoltage);
            if (crntVoltage > maxVoltage - 0.01 || crntVoltage < minVoltage + 0.01) telemetry.addData("!!UNSURE!!", "");
            telemetry.update();

            prevVoltage = crntVoltage;
        }
    }
}
