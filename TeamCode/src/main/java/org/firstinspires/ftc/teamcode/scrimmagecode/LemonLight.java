package org.firstinspires.ftc.teamcode.scrimmagecode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


public class LemonLight extends OpMode {

    private Limelight3A limelight;

    @Override
    //throws interrupted exception excluded
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        telemetry.setMsTransmissionInterval(11); //very frequent updates - every 11 ms

        limelight.pipelineSwitch(1);

        /*
        0 = apriltag (apriltag)
        1 = greenblob (color/ retroreflective)
        2 = detectartifacts (neural detector)
        */

    }

    @Override
    public void start() {
        limelight.start();
    }

    @Override
    public void loop() {
        LLResult llResult = limelight.getLatestResult();
        if (llResult != null & llResult.isValid()) {
            telemetry.addData("Target X offset", llResult.getTx());
            telemetry.addData("Target Y offset", llResult.getTy());
            telemetry.addData("Target area offset", llResult.getTa());
        }
    }
}