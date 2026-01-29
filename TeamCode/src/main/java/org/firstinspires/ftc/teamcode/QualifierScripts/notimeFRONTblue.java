package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Disabled

public class notimeFRONTblue extends LinearOpMode {

    RRHardware rrHardware;


    @Override
    public void runOpMode() throws InterruptedException {
        RRHardware rrHardware;
        rrHardware = new RRHardware(hardwareMap);
      //  rrHardware.init();

        String motif = "null";
        int id = 0;

        waitForStart();
        rrHardware.launcherTurn.setPower(0d);
        rrHardware.doDrive(0,-.5,0);
        sleep(1300);
        rrHardware.doDrive(0,0,0);
        rrHardware.doDrive(0,0,-.5);
        sleep(1050);
        rrHardware.doDrive(0,0,0);

//        if (rrHardware != null && rrHardware.limelight != null) {
//            LLResult result = rrHardware.limelight.getLatestResult();
//            if(result != null && result.isValid()) {
//                List<LLResultTypes. FiducialResult> fiducials = result.getFiducialResults();
//                if (fiducials != null && ! fiducials.isEmpty()) {
//                    for (LLResultTypes.FiducialResult fiducial : fiducials) {
//                        id = fiducial.getFiducialId();
//                    }
//
//                    if (id == 21) {
//                        motif = "gpp";
//                    } else if (id == 22) {
//                        motif = "pgp";
//                    } else if (id == 23) {
//                        motif = "ppg";
//                    }
//
//                    telemetry.addData("tag found", id);
//                    telemetry. addData("motif", motif);
//                    telemetry.update();
//                }
//            } else {
//                telemetry.addLine("No apriltag found yet");
//                telemetry.update();
//            }
//        }
//
//        sleep(5000);
//
//        if(id == 21) {
//            rrHardware.shootgppclose();
//        } else if(id == 22) {
//            rrHardware.shootpgpclose();
//        } else if(id == 23) {
//            rrHardware.shootppgclose();
//        } else {
//            rrHardware.shootgppclose();
//            telemetry.addLine("no apriltag found, using default");
//            telemetry.update();
//        }

//        rrHardware.shootpgpclose(); aah

        sleep(5000);
        rrHardware.doDrive(0,0,-.5);
        sleep(800);
        rrHardware.doDrive(0,-.5,0);
        sleep(800);
        rrHardware.doDrive(0,0,0);


    }
}
