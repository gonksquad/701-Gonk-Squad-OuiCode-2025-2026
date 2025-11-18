package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@Disabled
//@TeleOp(name = "grapefruitLight")
public class GrapefruitLight_SheaTesting extends LinearOpMode {
    Limelight3A limelight;

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        //limelight.pipelineSwitch(0);
        limelight.start();
        waitForStart();

        //telemetry.addData("Id", table.getEntry("tid").getDoubleArray(new double[6]));
        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();

            if(result != null && result.isValid()) {
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