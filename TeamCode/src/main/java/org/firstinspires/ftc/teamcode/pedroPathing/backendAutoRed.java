package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.scrimmagecode.OrangeLight;

import java.util.List;

@Autonomous
public class backendAutoRed extends OpMode {
    Hardware hardware;
    private Limelight3A limelight;
    private Follower follower;
    private Timer pathTimer, actionTimer;
    private ElapsedTime runtime = new ElapsedTime();
    private int pathState;
    float bounds_X = 4f;
    String lastPos = "None";

    //poses initialized
    private final Pose startPose = new Pose(48, 9, Math.toRadians(180));
    private final Pose firstSpikeStart = new Pose(48,36, Math.toRadians(180));
    private final Pose firstSpikeEnd = new Pose(24,36, Math.toRadians(180));

    //path initializing

    //sorter
    public void sorter() {
        int array[] = {1, 2, 3}; //where physical slots are
        String colors[] = {"P", "G", "P"}; //color in each slot
    }

//    //apriltag
//    public void AprilTagIdentifier() {
//        //get apriltag id!!!
//            //id 21 = GPP
//            //id 22 = PGP
//            //id 23 = PPG
//        //start 1 = P, 2 = P, 3 = G
//        if (apriltagid = 21) {
//            first = 3;
//            second = 2;
//            third = 1;
//        }
//        if (apriltagid = 22) {
//            first = 2;
//            second = 3;
//            third = 1;
//        }
//        if (apriltagid = 23) {
//            first = 1;
//            second = 2;
//            third = 3;
//        }
//    }

    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        //turretAxon = hardwareMap.get(CRServo.class, "axon");
        limelight.setPollRateHz(90);
        limelight.pipelineSwitch(0); // motif pipeline (ID=21,22,23)
        //limelight.pipelineSwitch(1); // this is for left goal (ID=20)

        limelight.start();

    }
    @Override
    public void start(){
        int id = -1;
        String motif = "null";

        LLResult result = limelight.getLatestResult();
        //BoundingBox();
        if(result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            //findID();
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

        runtime.reset();
    }

    @Override
    public void loop() {

    }
}

/* process:
    1. Start pose
        - 180 deg angle
        - 2 purple and 1 green preloaded ball, order known and defined
            ex. slot 1,2,3 with servo positions = purple, purple, green
    2. View april tag and detect pattern
        - # corresponds to each pattern, when detected set: number = shooting order as key
    3. Shoot the correct order of preloaded balls
        - slot 1,2,3 order pulled from apriltag detected,
        - shooting motors on (stay on whole time?),
        - servo up,
        - repeat 1/3
    4. move to collect 3 balls on spike mark in GPP order
    5. move back to initial position, repeat step 3 with new sorter store
        - note: color sensor not needed

also how to get extra (?)ranking points like moving off the baselines?
*/