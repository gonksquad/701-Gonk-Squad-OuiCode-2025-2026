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
public class frontendAutoRed extends OpMode {
    String motif = "null"; //will this still work here?
    int id = 0; //same ?
    Hardware hardware = new Hardware(hardwareMap);

    public Hardware getHardware() {
        return hardware;
    }

    RRHardware rrHardware;

    private Limelight3A limelight;
    private Follower follower;
    private Timer pathTimer, opModeTimer;
    //private ElapsedTime runtime = new ElapsedTime();

    pathState pathState;
    public enum pathState {
        APRILTAGLOOKSIES,
        MOVETOSHOOT,
        SHOOT,
        STARTTOBEFOREPICKUP,
        S1PICKUP1,
        S1PICKUP2,
        S1PICKUP3,
        PICKUPTOSHOOT,
        SHOOT2,
        SHOOTMOVE

    }
//    float bounds_X = 4f;
//    String lastPos = "None";

    private PathChain start_driveToShoot, shoot_beforeThirdSpike, thirdSpike_firstArtifactCollect,
            thirdSpike_secondArtifactCollect, thirdSpike_thirdArtifactCollect, thirdSpike_shoot, shoot_forward;

    //poses initialized
    private final Pose startPose = new Pose(21, 123.3, Math.toRadians(324));
    private final Pose shootpose = new Pose(59, 85, Math.toRadians(324));
    private final Pose beforeThirdSpike = new Pose(44,84, Math.toRadians(180));

    private final Pose thirdSpike1 = new Pose(40.5,84, Math.toRadians(180)); //5.5 in. artifact
    private final Pose thirdSpike2 = new Pose(35,84, Math.toRadians(180));
    private final Pose thirdSpike3 = new Pose(29.5,84, Math.toRadians(180));
    private final Pose forward = new Pose(44,80, Math.toRadians(180));



    //path initializing
    public void buildPaths() {
        start_driveToShoot = follower.pathBuilder()
                .addPath(new BezierCurve(startPose, shootpose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootpose.getHeading())
                .build();
        shoot_beforeThirdSpike = follower.pathBuilder()
                .addPath(new BezierCurve(shootpose, beforeThirdSpike))
                .setLinearHeadingInterpolation(shootpose.getHeading(), beforeThirdSpike.getHeading())
                .build();
        thirdSpike_firstArtifactCollect = follower.pathBuilder()
                .addPath(new BezierLine(beforeThirdSpike, thirdSpike1))
                .setLinearHeadingInterpolation(beforeThirdSpike.getHeading(), thirdSpike1.getHeading())
                .build();
        thirdSpike_secondArtifactCollect = follower.pathBuilder()
                .addPath(new BezierLine(thirdSpike1, thirdSpike2))
                .setLinearHeadingInterpolation(thirdSpike1.getHeading(), thirdSpike2.getHeading())
                .build();
        thirdSpike_thirdArtifactCollect = follower.pathBuilder()
                .addPath(new BezierLine(thirdSpike2, thirdSpike3))
                .setLinearHeadingInterpolation(thirdSpike2.getHeading(), thirdSpike3.getHeading())
                .build();
        thirdSpike_shoot = follower.pathBuilder()
                .addPath(new BezierCurve(thirdSpike3, shootpose))
                .setLinearHeadingInterpolation(thirdSpike3.getHeading(), shootpose.getHeading()) //hopefully backwards drive
                .build();
        shoot_forward = follower.pathBuilder()
                .addPath(new BezierLine(shootpose, forward))
                .setLinearHeadingInterpolation(shootpose.getHeading(), forward.getHeading())
                .build();
    }

    public void statePathUpdate() {
        switch(pathState) {
            case MOVETOSHOOT:
                if (!follower.isBusy()) {
                    follower.followPath(start_driveToShoot);
                }
                setPathState(pathState.APRILTAGLOOKSIES);
                break;
            case APRILTAGLOOKSIES:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 5) {
                    hardware.limelightTurn.setPosition(.5);
                    LLResult result = hardware.limelight.getLatestResult();
                    //BoundingBox();
                    if(result != null && result.isValid()) {
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
                }
                setPathState(pathState.SHOOT); //reset timer and make new state
                break;
            case SHOOT:
                if (!follower.isBusy()) {
                    if(id == 21) {
                        rrHardware.shootgpp();
                    }
                    if(id == 22) {
                        rrHardware.shootpgp();
                    }
                    if(id == 23) {
                        rrHardware.shootppg();
                    }
                    else {
                        rrHardware.shootgpp();
                        telemetry.addLine("no apriltag found :(");
                    }
                }
                setPathState(pathState.STARTTOBEFOREPICKUP);
                telemetry.addLine("apriltag section done");
                break;
            case STARTTOBEFOREPICKUP:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 10) { //note: after shoot and change time to something for whole auto
                    follower.followPath(shoot_beforeThirdSpike, true);
                }
                setPathState(pathState.S1PICKUP1);
                telemetry.addLine("before pickup");
                break;
            case S1PICKUP1:
                if (!follower.isBusy()) {
                    rrHardware.intake1(); //make sure this doesn't stop other functions
                    follower.followPath(thirdSpike_firstArtifactCollect, true);
                }
                setPathState(pathState.S1PICKUP2);
                telemetry.addLine(" done pickup 1");
                break;
            case S1PICKUP2:
                if (!follower.isBusy()) {
                    rrHardware.intake2();
                    follower.followPath(thirdSpike_secondArtifactCollect, true);
                }
                setPathState(pathState.S1PICKUP3);
                telemetry.addLine(" done pickup 2");
                break;
            case S1PICKUP3:
                if (!follower.isBusy()) {
                    rrHardware.intake3();
                    follower.followPath(thirdSpike_thirdArtifactCollect, true);
                }
                setPathState(pathState.PICKUPTOSHOOT);
                telemetry.addLine(" done pickup 3");
                break;
            case PICKUPTOSHOOT:
                if (!follower.isBusy()) {
                    follower.followPath(thirdSpike_shoot, true);
                }
                setPathState(pathState.SHOOT2);
                telemetry.addLine("moved to shoot");
                break;
            case SHOOT2:
                if (!follower.isBusy()) {
                    if(id == 21) {
                        rrHardware.shootgpp();
                    }
                    if(id == 22) {
                        rrHardware.shootpgp();
                    }
                    if(id == 23) {
                        rrHardware.shootppg();
                    }
                    else {
                        rrHardware.shootppg();
                    }
                }
                setPathState(pathState.SHOOTMOVE);
                telemetry.addLine("done shooting");
                break;
            case SHOOTMOVE:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 5) { //note: change time to something for whole auto
                    follower.followPath(shoot_forward, true);
                }
                telemetry.addLine(" done! :)");
                break;
            default:
                telemetry.addLine("Nothing running :(");
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
        limelight.pipelineSwitch(0); // motif pipeline (ID=21,22,23)
        //limelight.pipelineSwitch(1); // this is for left goal (ID=20)

        limelight.start();

        pathState = pathState.MOVETOSHOOT;
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