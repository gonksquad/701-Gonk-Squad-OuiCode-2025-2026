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

    @Override
    public void runOpMode() {
        servo = hardwareMap.get(CRServo.class, "axon");
        sensor = hardwareMap.get(AnalogInput.class, "axonWire");

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.b) {
                servo.setPower(0);
            } else if (gamepad1.a) {
                servo.setPower(0.2);
            } else if (gamepad1.y) {
                servo.setPower(-0.2);
            }

            telemetry.addData("Reading: ", sensor.getVoltage());
            telemetry.addData("Writing: ", servo.getPower());
            telemetry.addData("Guessing: ", 360.0 - sensor.getVoltage() * 270d / (2.56 - 0.12));
            telemetry.update();
        }
    }
}
