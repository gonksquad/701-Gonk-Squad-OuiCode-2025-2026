package org.firstinspires.ftc.teamcode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

//import edu.wpi.first.networktables.NetworkTable;
//import edu.wpi.first.networktables.NetworkTableInstance;


@TeleOp(name = "orangeLight")
public class OrangeLight extends LinearOpMode {
    Limelight3A limelight;
    CRServo turretAxon;
    float bounds_X = 4f;
    String lastPos = "None";
    private ElapsedTime outOfRange = new ElapsedTime();
    //NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

    public void runOpMode() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        turretAxon = hardwareMap.get(CRServo.class, "axon");
        limelight.setPollRateHz(90);
        //limelight.pipelineSwitch(0); // ive set this pipeline to search for
        limelight.pipelineSwitch(1); // this is for left goal (ID=20)

        limelight.start();
        int id = -1;
        String motif = "null";
        waitForStart();

        //telemetry.addData("Id", table.getEntry("tid").getDoubleArray(new double[6]));
        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            BoundingBox();
            if(result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
                //findID();
                //limelight.pipelineSwitch(1); // this is pipeline for motif
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

    public void BoundingBox() {
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            double tx = result.getTx();

            if(tx > 0.1f) {
                turretAxon.setPower(-0.8);
            } else if(tx < -0.1) {
                turretAxon.setPower(0.8);
            } else {
                turretAxon.setPower(0);
            }
            /*if (Math.abs(tx) <= bounds_X ) {
                telemetry.addData("Within bounds", "");
                turretAxon.setPower(0);
                lastPos = "None";
                outOfRange.reset();
            } else {
                if (tx > bounds_X || (lastPos == "Right" && outOfRange.milliseconds() <= 250)) {
                    telemetry.addData("Move right", "");
                    lastPos = "Right";
                    turretAxon.setPower(0.8);
                } else if (tx < -bounds_X || (lastPos == "Left" && outOfRange.milliseconds() <= 250)) {
                    telemetry.addData("Move left", "");
                    lastPos = "Left";
                    turretAxon.setPower(-0.8);
                }
            }*/

        }
    }

}