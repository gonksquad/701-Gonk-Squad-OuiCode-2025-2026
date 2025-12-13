package org.firstinspires.ftc.teamcode.QualifierScripts;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.List;

@Autonomous
public class backendAutoRed extends OpMode {
    int id = -1;
    String motif = "null";
    Hardware hardware = new Hardware(hardwareMap);

    public Hardware getHardware() {
        return hardware;
    }
    
    RRHardware rrHardware;

    private Limelight3A limelight;
    private Follower follower;
    private Timer pathTimer, actionTimer, opModeTimer;
    //private ElapsedTime runtime = new ElapsedTime();

    pathState pathState;
    public enum pathState {
        APRILTAGLOOKSIES,
        SHOOT,
        STARTTOBEFOREPICKUP,
        PICKUP1,
        PICKUP2,
        PICKUP3,
        PICKUPTOSHOOT,
        SHOOT2,
        SHOOTFORWARD

    }
//    float bounds_X = 4f;
//    String lastPos = "None";

    private PathChain start_driveToFirstSpike, firstSpike_firstArtifactCollect, firstSpike_secondArtifactCollect,
            firstSpike_thirdArtifactCollect, firstSpike_shoot, shoot_forward;

    //poses initialized
    private final Pose startPose = new Pose(48, 9, Math.toRadians(180));
    private final Pose forward = new Pose(48, 21, Math.toRadians(90));
    private final Pose beforeFirstSpike = new Pose(44,36, Math.toRadians(180));

    private final Pose firstSpike1 = new Pose(40.5,36, Math.toRadians(180)); //5.5 in artifact
    private final Pose firstSpike2 = new Pose(35,36, Math.toRadians(180));
    private final Pose firstSpike3 = new Pose(29.5,36, Math.toRadians(180));


    //path initializing
    public void buildPaths() {
        start_driveToFirstSpike = follower.pathBuilder()
                .addPath(new BezierCurve(startPose, beforeFirstSpike))
                .setLinearHeadingInterpolation(startPose.getHeading(), beforeFirstSpike.getHeading())
                .build();
        firstSpike_firstArtifactCollect = follower.pathBuilder()
                .addPath(new BezierLine(beforeFirstSpike, firstSpike1))
                .setLinearHeadingInterpolation(beforeFirstSpike.getHeading(), firstSpike1.getHeading())
                .build();
        firstSpike_secondArtifactCollect = follower.pathBuilder()
                .addPath(new BezierLine(firstSpike1, firstSpike2))
                .setLinearHeadingInterpolation(firstSpike1.getHeading(), firstSpike2.getHeading())
                .build();
        firstSpike_thirdArtifactCollect = follower.pathBuilder()
                .addPath(new BezierLine(firstSpike2, firstSpike3))
                .setLinearHeadingInterpolation(firstSpike2.getHeading(), firstSpike3.getHeading())
                .build();
        firstSpike_shoot = follower.pathBuilder()
                .addPath(new BezierCurve(firstSpike3, startPose))
                .setLinearHeadingInterpolation(firstSpike3.getHeading(), startPose.getHeading()).setReversed() //hopefully backwards drive
                .build();
        shoot_forward = follower.pathBuilder()
                .addPath(new BezierLine(startPose, forward))
                .setLinearHeadingInterpolation(90, forward.getHeading())
                .build();
    }

    public void statePathUpdate() {
        switch(pathState) {
            case APRILTAGLOOKSIES:
                hardware.limelightTurn.setPosition(.5);
                LLResult result = limelight.getLatestResult();
                //BoundingBox();
                if(result != null && result.isValid()) {
                    String motif = "null";
                    int id = 0;
                    if (result != null && result.isValid()) {
                        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                        for (LLResultTypes.FiducialResult fiducial : fiducials) {
                            id = fiducial.getFiducialId();
                        }

                        if (id == 21) {
                            motif = "gpp";
                        } else if (id == 22) {
                            motif = "pgp";
                        } else if (id == 23) {
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
                setPathState(pathState.SHOOT); //reset timer and make new state
                break;
            case SHOOT:
                if (!follower.isBusy()) {
                    if(id == 21) {
                        rrHardware.shootgppfar();
                    }
                    if(id == 22) {
                        rrHardware.shootpgpfar();
                    }
                    if(id == 23) {
                        rrHardware.shootppgfar();
                    }
                    else {
                        rrHardware.shootgppfar();
                    }
                }
                setPathState(pathState.STARTTOBEFOREPICKUP);
                break;
            case STARTTOBEFOREPICKUP:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 10) { //note: after shoot and change time to something for whole auto
                    follower.followPath(start_driveToFirstSpike, true);
                    //pathState = pathState.FIRSTSPIKEDRIVE;
                }
                setPathState(pathState.PICKUP1);
                break;
            case PICKUP1:
                if (!follower.isBusy()) {
                    rrHardware.intake1(); //make sure this doesn't stop other functions
                    follower.followPath(firstSpike_firstArtifactCollect, true);
                }
                setPathState(pathState.PICKUP2);
                break;
            case PICKUP2:
                if (!follower.isBusy()) {
                    rrHardware.intake2();
                    follower.followPath(firstSpike_secondArtifactCollect, true);
                }
                setPathState(pathState.PICKUP3);
                break;
            case PICKUP3:
                if (!follower.isBusy()) {
                    rrHardware.intake3();
                    follower.followPath(firstSpike_thirdArtifactCollect, true);
                }
                setPathState(pathState.PICKUPTOSHOOT);
                break;
            case PICKUPTOSHOOT:
                if (!follower.isBusy()) {
                    follower.followPath(firstSpike_shoot, true);
                }
                setPathState(pathState.SHOOT2);
                break;
            case SHOOT2:
                if (!follower.isBusy()) {
                    if(id == 21) {
                        rrHardware.shootgppfar();
                    }
                    if(id == 22) {
                        rrHardware.shootpgpfar();
                    }
                    if(id == 23) {
                        rrHardware.shootppgfar();
                    }
                    else {
                        rrHardware.shootppgfar();
                    }
                }
                setPathState(pathState.SHOOTFORWARD);
                break;
            case SHOOTFORWARD:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 25) { //note: change time to something for whole auto
                    follower.followPath(shoot_forward, true);
                }
                break;
            default:
                telemetry.addLine("Nothing runnnning");
                break;
        }
    }

    public void setPathState(pathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        //turretAxon = hardwareMap.get(CRServo.class, "axon");
        limelight.setPollRateHz(90);
        limelight.pipelineSwitch(1); // motif pipeline (ID=21,22,23)
        //limelight.pipelineSwitch(1); // this is for left goal (ID=20)

        limelight.start();

        pathState = pathState.APRILTAGLOOKSIES;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);

        buildPaths();
        follower.setPose(startPose);
    }
    @Override
    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);
    }

    @Override
    public void loop() {
        follower.update();
        statePathUpdate();

        telemetry.addData("path state", pathState.toString());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("path time", pathTimer.getElapsedTimeSeconds());
    }
}

/* process:
    1. Start pose
        - 180 deg angle
        - 2 purple and 1 green preloaded ball, order known and defined
            ex. slot 1,2,3 with servo positions = green, purple, purple
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

//sorter
//    public void sorter() {
//        int array[] = {1, 2, 3}; //where physical slots are
//        String colors[] = {"P", "G", "P"}; //color in each slot
//    }

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