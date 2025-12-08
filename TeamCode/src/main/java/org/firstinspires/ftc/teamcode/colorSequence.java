package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

@TeleOp
public class colorSequence extends LinearOpMode{
    Limelight3A limelight;
    public void runOpMode(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        waitForStart();
        limelight.start();
        limelight.pipelineSwitch(1);
        while (opModeIsActive()) {
            String motif = "null";
            LLResult result = limelight.getLatestResult();
            int id = 0;
            if(result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    id = fiducial.getFiducialId();
                }
//                switch(id) {
//                    case 21:
//                        motif = "gpp";
//                        break;
//                    case 22:
//                        motif = "pgp";
//                        break;
//                    case 23:
//                        motif = "ppg";
//                        break;
//                    default:
//                        motif = "null";
//                }

                if (id == 21){
                    motif = "gpp";
                } else if (id == 22){
                    motif = "pgp";
                } else if (id == 23){
                    motif = "ppg";
                }

                telemetry.addData("tag found", id);
                telemetry.addData("motif", motif);

                telemetry.update();
            } else {
                telemetry.addLine("No apriltag found");
                telemetry.update();
            }
        }
    }
}


//package org.firstinspires.ftc.teamcode;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.LLResultTypes;
//import com.qualcomm.hardware.limelightvision.LLStatus;
//import com.qualcomm.hardware.limelightvision.Limelight3A;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//@TeleOp
//public class colorSequence extends LinearOpMode{
//    Limelight3A limelight;
//    public void runOpMode(){
//        limelight = hardwareMap.get(Limelight3A.class, "limelight");
//        waitForStart();
//        limelight.start();
//        telemetry.addLine("runOpMode started");
//        while (opModeIsActive()) {
//            telemetry.addLine("opModeIsActive started");
//            limelight.pipelineSwitch(2);
//            LLResult result = limelight.getLatestResult();
//            if (result != null && result.isValid()) {
//                telemetry.addLine("pgp");
//            }
//            else {
//                limelight.pipelineSwitch(3);
//                if (result != null && result.isValid()) {
//                    telemetry.addLine("gpp");
//                }
//                else{
//                    limelight.pipelineSwitch(4);
//                    if (result != null && result.isValid()) {
//                        telemetry.addLine("ppg");
//                    }
//                    else{
//                        telemetry.addLine("No apriltag detected.");
//                    }
//                }
//            }
//        }
//    }
//}
