package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
public class notimeBACKred extends LinearOpMode {

    RRHardware rrHardware;


    @Override
    public void runOpMode() throws InterruptedException {
        RRHardware rrHardware;
        rrHardware = new RRHardware(hardwareMap);
        rrHardware.init();

        String motif = "null";
        int id = 0;

        waitForStart();
        rrHardware.launcherTurn.setPower(0d);
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
//                }
//            } else {
//                telemetry.addLine("No apriltag found yet");
//            }
//        }

//        sleep(5000);

        rrHardware.doDrive(0,-.5,.05);
        sleep(170);
        rrHardware.doDrive(0,0,0);

//        if(id == 21) {
//            rrHardware.shootgppclose();
//        } else if(id == 22) {
//            rrHardware.shootpgpclose();
//        } else if(id == 23) {
//            rrHardware.shootppgclose();
//        } else {
//            rrHardware.shootgppclose();
//            telemetry.addLine("no apriltag found, using default");
//        }
        rrHardware.shootpgpclose();

        sleep(5000);
        rrHardware.doDrive(0,-.5,-.05);
        sleep(600);
        rrHardware.doDrive(0,0,0);
    }
}
