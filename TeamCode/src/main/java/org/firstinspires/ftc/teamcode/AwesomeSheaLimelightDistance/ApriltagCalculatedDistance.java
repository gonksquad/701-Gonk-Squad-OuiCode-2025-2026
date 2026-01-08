package org.firstinspires.ftc.teamcode.AwesomeSheaLimelightDistance;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import org.firstinspires.ftc.robotcore.external.toplevel.NetworkTableInstance;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@TeleOp
public class ApriltagCalculatedDistance extends LinearOpMode {
    private double distance;
    Limelight3A limelight;
    @Override
    public void runOpMode() throws InterruptedException {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        waitForStart();
        limelight.start();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                distance = getDistanceFromTag(result.getTa());
                telemetry.addData("Calculated Distance:", distance);
                telemetry.addData("Target Y Offset:", result.getTy());
                telemetry.addData("Target Area Offset:", result.getTa()); //%of field of view

                telemetry.addData("tx", result.getTx());
            } else {
                telemetry.addLine("No valid target detected.");
            }
            telemetry.update();
        }

    }
    public double getDistanceFromTag(double ta){
        double scale = 184.8972;
        double dist = (scale * (Math.pow(ta,-0.5056956)));
        return dist;
        //y = 184.8972*x^-0.5056956
    }
}