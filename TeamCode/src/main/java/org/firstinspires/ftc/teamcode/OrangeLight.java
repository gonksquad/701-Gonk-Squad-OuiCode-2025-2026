package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//import edu.wpi.first.networktables.NetworkTable;
//import edu.wpi.first.networktables.NetworkTableInstance;


@TeleOp(name = "orangeLight")
public class OrangeLight extends LinearOpMode {
    Limelight3A limelight;
    //NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(90);
        limelight.pipelineSwitch(0);
        limelight.start();
        waitForStart();

        //telemetry.addData("Id", table.getEntry("tid").getDoubleArray(new double[6]));
        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();

            if(result != null && result.isValid()) {
                //findID();
                limelight.pipelineSwitch(1);
                telemetry.addData("tag found", null);
            }

            telemetry.update();

        }
    }
/*
    public void findID() {


        for(int i=1; i<=3;i++){
            limelight.pipelineSwitch(i);
            LLResult result = limelight.getLatestResult();

            if(result != null && result.isValid()) {
                telemetry.addData("Id", i);
                telemetry.update();
                break;
            }
        }
    }
*/
}