package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

//import edu.wpi.first.networktables.NetworkTable;
//import edu.wpi.first.networktables.NetworkTableInstance;


@TeleOp(name = "orangeLight")
public class OrangeLight extends LinearOpMode {
    Limelight3A limelight;
    //NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(90);
        limelight.pipelineSwitch(0); // ive set this pipeline to search for
        limelight.start();
        int id = -1;
        String motif = "null";
        waitForStart();

        //telemetry.addData("Id", table.getEntry("tid").getDoubleArray(new double[6]));
        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();

            if(result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
                //findID();
                limelight.pipelineSwitch(1);
                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    id = fiducial.getFiducialId();
                }
                telemetry.addData("tag found", id);
                telemetry.addData("motif", motif);

                switch(id) {
                    case 21:
                        motif = "gpp";
                        break;
                    case 22:
                        motif = "pgp";
                        break;
                    case 23:
                        motif = "ppg";
                        break;
                    default:
                        motif = "null";
                }
                        telemetry.update();
            }


        }
    }

}