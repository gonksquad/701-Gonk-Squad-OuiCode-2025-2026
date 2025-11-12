package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;

@Autonomous
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
