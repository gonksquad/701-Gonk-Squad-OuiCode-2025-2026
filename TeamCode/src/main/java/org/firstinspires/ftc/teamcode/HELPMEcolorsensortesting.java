package org.firstinspires.ftc.teamcode;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp (name="colorSensing")
public class HELPMEcolorsensortesting extends LinearOpMode {

    private ColorSensor colorSensor;
    int red;
    int green;
    int blue;
    float[] hsvValues = new float[3];

    @Override
    public void runOpMode() {
        colorSensor = hardwareMap.get(ColorSensor.class, "color_sensor");
        //colorSensor.enableLed(true); //on automatically i think but still
        waitForStart();
        while (opModeIsActive()) {
            red = colorSensor.red();
            green = colorSensor.green();
            blue = colorSensor.blue();
            Color.RGBToHSV(red, green, blue, hsvValues);
            //telemetry.addData("Red", red);
            //telemetry.addData("Green", green);
            //telemetry.addData("Blue", blue);
            telemetry.addData("Hue", hsvValues[0]);
            telemetry.addData("Saturation", hsvValues[1]);
            telemetry.addData("Value", hsvValues[2]);
            //telemetry.addData("alpha: ", colorSensor.alpha());
            //telemetry.addData("argb", Color.argb(colorSensor.alpha(), colorSensor.red(), colorSensor.green(), colorSensor.blue()));
            telemetry.addData("detecting", detectArtifactColor());
            telemetry.update();
        }
    }

    public String detectArtifactColor() {
        boolean validColor = hsvValues[1] > 0.55;
        float hue = hsvValues[0];
        if(hue > 220 && hue < 230 && validColor) {
            return "purple";
        } else if(hue > 155 && hue < 175 && validColor) {
            return "green";
        } else if(hue > 145 && hue < 155 && validColor) {
            return "eamonn"; // stop this is real and you did write this not eamonn
        }
        return null;
    }
}