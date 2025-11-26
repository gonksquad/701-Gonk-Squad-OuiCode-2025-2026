package org.firstinspires.ftc.teamcode.scrimmagecode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

@Disabled
//@Autonomous
public class ProtoFloodGate extends LinearOpMode {

    private AnalogInput floodgate;

    @Override
    public void runOpMode() {

        floodgate = hardwareMap.get(AnalogInput.class, "floodgate");

        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Floodgate Connection: ", floodgate.getConnectionInfo());
            telemetry.addData("Floodgate Voltage: ", floodgate.getVoltage());
            telemetry.addData("Floodgate Manufacturer: ", floodgate.getManufacturer());
            telemetry.addData("Floodgate Max Voltage: ", floodgate.getMaxVoltage());

            telemetry.update();
        }
    }
}
