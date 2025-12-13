package org.firstinspires.ftc.teamcode. QualifierScripts;

import com.pedropathing.follower. Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry. BezierLine;
import com.pedropathing.geometry. Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;

import com.qualcomm. robotcore.eventloop.opmode. Autonomous;
import com.qualcomm.robotcore.eventloop. opmode.OpMode;

import org.firstinspires.ftc.teamcode. pedroPathing.Constants;

import java.util.List;

@Di
public class frontendAutoRed extends OpMode {

    String motif = "null";
    int id = 0;

    RRHardware rrhardware;

    private Follower follower;
    private Timer pathTimer, opModeTimer, actionTimer;

    pathState currentPathState;
    public enum pathState {
        MOVETOSHOOT,
        WAIT_MOVE_TO_SHOOT,
        MOVELOOK,
        APRILTAGLOOKSIES,
        SHOOT,
        WAIT_SHOOT,
        STARTTOBEFOREPICKUP,
        WAIT_BEFORE_PICKUP,
        S1PICKUP1,
        WAIT_PICKUP1,
        S1PICKUP2,
        WAIT_PICKUP2,
        S1PICKUP3,
        WAIT_PICKUP3,
        PICKUPTOSHOOT,
        WAIT_PICKUP_TO_SHOOT,
        SHOOT2,
        WAIT_SHOOT2,
        SHOOTMOVE,
        DONE
    }

    private PathChain start_driveToLook, look_shoot, shoot_beforeThirdSpike, thirdSpike_firstArtifactCollect,
            thirdSpike_secondArtifactCollect, thirdSpike_thirdArtifactCollect, thirdSpike_shoot, shoot_forward;

    //poses initialized
    private final Pose startPose = new Pose(21, 123.3, Math.toRadians(324));
    private final Pose limelightLook = new Pose(70, 79, Math.toRadians(90));
    private final Pose shootpose = new Pose(70, 79, Math.toRadians(324));
    private final Pose beforeThirdSpike = new Pose(44,84, Math.toRadians(180));

    private final Pose thirdSpike1 = new Pose(40.5,84, Math.toRadians(180));
    private final Pose thirdSpike2 = new Pose(35,84, Math.toRadians(180));
    private final Pose thirdSpike3 = new Pose(29.5,84, Math.toRadians(180));
    private final Pose forward = new Pose(44,80, Math.toRadians(180));

    //path initializing
    public void buildPaths() {
        start_driveToLook = follower.pathBuilder()
                .addPath(new BezierCurve(startPose, limelightLook))
                .setLinearHeadingInterpolation(startPose.getHeading(), limelightLook.getHeading())
                .build();

        look_shoot = follower.pathBuilder()
                .addPath(new BezierCurve(limelightLook, shootpose))
                .setLinearHeadingInterpolation(limelightLook.getHeading(), shootpose.getHeading())
                .build();

        shoot_beforeThirdSpike = follower.pathBuilder()
                .addPath(new BezierCurve(shootpose, beforeThirdSpike))
                .setLinearHeadingInterpolation(shootpose.getHeading(), beforeThirdSpike.getHeading())
                .build();
        thirdSpike_firstArtifactCollect = follower.pathBuilder()
                .addPath(new BezierLine(beforeThirdSpike, thirdSpike1))
                .setLinearHeadingInterpolation(beforeThirdSpike. getHeading(), thirdSpike1.getHeading())
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
                .setLinearHeadingInterpolation(thirdSpike3.getHeading(), shootpose.getHeading())
                .build();
        shoot_forward = follower.pathBuilder()
                .addPath(new BezierLine(shootpose, forward))
                .setLinearHeadingInterpolation(shootpose.getHeading(), forward.getHeading())
                .build();
    }

    public void statePathUpdate() {
        try {
            switch(currentPathState) {
                case MOVETOSHOOT:
                    // Start the path
                    follower.followPath(start_driveToLook);
                    setPathState(pathState.MOVELOOK);
                    telemetry.addLine("Started MOVETOSHOOT");
                    break;

//                case WAIT_MOVE_TO_SHOOT:
//                    // Wait for path to complete
//                    if (! follower.isBusy()) {
//                        telemetry.addLine("MOVETOSHOOT complete");
//                        setPathState(pathState.MOVELOOK);
//                    }
//                    break;

                case MOVELOOK:
                    if(!follower.isBusy()) {
                        follower.followPath(look_shoot);
                        setPathState(pathState.APRILTAGLOOKSIES);
                    }
                    break;

                case APRILTAGLOOKSIES:
                    // Look for AprilTag for 2 seconds
                    if (rrhardware != null && rrhardware.limelightTurn != null) {
                        rrhardware.limelightTurn.setPosition(.5);
                    }

                    if (rrhardware != null && rrhardware.limelight != null) {
                        LLResult result = rrhardware.limelight.getLatestResult();
                        if(result != null && result.isValid()) {
                            List<LLResultTypes. FiducialResult> fiducials = result.getFiducialResults();
                            if (fiducials != null && ! fiducials.isEmpty()) {
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
                                telemetry. addData("motif", motif);
                            }
                        } else {
                            telemetry.addLine("No apriltag found yet");
                        }
                    }

                    // After 2 seconds, move to shoot
                    if (pathTimer. getElapsedTimeSeconds() > 2) {
                        telemetry.addLine("APRILTAGLOOKSIES done");
                        setPathState(pathState.SHOOT);
                    }
                    break;

//                case SHOOT:
//                    // Execute shooting sequence
//                    if (rrhardware != null) {
//                        if(id == 21) {
//                            rrhardware.shootgppclose();
//                        } else if(id == 22) {
//                            rrhardware.shootpgpclose();
//                        } else if(id == 23) {
//                            rrhardware.shootppgclose();
//                        } else {
//                            rrhardware.shootgppclose();
//                            telemetry.addLine("no apriltag found, using default");
//                        }
//                    }
//                    setPathState(pathState.WAIT_SHOOT);
//                    telemetry.addLine("Shooting started");
//                    break;
//
//                case WAIT_SHOOT:
//                    // Wait for shooting to complete (1. 5 seconds for 3 shots)
//                    if (pathTimer.getElapsedTimeSeconds() > 2.0) {
//                        telemetry.addLine("Shooting complete");
//                        setPathState(pathState.STARTTOBEFOREPICKUP);
//                    }
//                    break;
//
//                case STARTTOBEFOREPICKUP:
//                    follower.followPath(shoot_beforeThirdSpike, true);
//                    setPathState(pathState.WAIT_BEFORE_PICKUP);
//                    telemetry.addLine("Moving to before pickup");
//                    break;
//
//                case WAIT_BEFORE_PICKUP:
//                    if (!follower.isBusy()) {
//                        telemetry.addLine("Reached before pickup");
//                        setPathState(pathState.S1PICKUP1);
//                    }
//                    break;
//
//                case S1PICKUP1:
//                    if (rrhardware != null) {
//                        rrhardware.intake1();
//                    }
//                    follower.followPath(thirdSpike_firstArtifactCollect, true);
//                    setPathState(pathState.WAIT_PICKUP1);
//                    telemetry.addLine("Pickup 1 started");
//                    break;
//
//                case WAIT_PICKUP1:
//                    if (! follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.5) {
//                        telemetry.addLine("Pickup 1 complete");
//                        setPathState(pathState.S1PICKUP2);
//                    }
//                    break;
//
//                case S1PICKUP2:
//                    if (rrhardware != null) {
//                        rrhardware.intake2();
//                    }
//                    follower.followPath(thirdSpike_secondArtifactCollect, true);
//                    setPathState(pathState. WAIT_PICKUP2);
//                    telemetry.addLine("Pickup 2 started");
//                    break;
//
//                case WAIT_PICKUP2:
//                    if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.5) {
//                        telemetry. addLine("Pickup 2 complete");
//                        setPathState(pathState.S1PICKUP3);
//                    }
//                    break;
//
//                case S1PICKUP3:
//                    if (rrhardware != null) {
//                        rrhardware.intake3();
//                    }
//                    follower.followPath(thirdSpike_thirdArtifactCollect, true);
//                    setPathState(pathState.WAIT_PICKUP3);
//                    telemetry.addLine("Pickup 3 started");
//                    break;
//
//                case WAIT_PICKUP3:
//                    if (!follower. isBusy() && pathTimer.getElapsedTimeSeconds() > 1.5) {
//                        telemetry.addLine("Pickup 3 complete");
//                        setPathState(pathState.PICKUPTOSHOOT);
//                    }
//                    break;
//
//                case PICKUPTOSHOOT:
//                    follower.followPath(thirdSpike_shoot, true);
//                    setPathState(pathState.WAIT_PICKUP_TO_SHOOT);
//                    telemetry.addLine("Moving back to shoot");
//                    break;
//
//                case WAIT_PICKUP_TO_SHOOT:
//                    if (!follower.isBusy()) {
//                        telemetry. addLine("Reached shoot position");
//                        setPathState(pathState.SHOOT2);
//                    }
//                    break;
//
//                case SHOOT2:
//                    if (rrhardware != null) {
//                        if(id == 21) {
//                            rrhardware.shootgppclose();
//                        } else if(id == 22) {
//                            rrhardware. shootpgpclose();
//                        } else if(id == 23) {
//                            rrhardware.shootppgclose();
//                        } else {
//                            rrhardware.shootppgclose();
//                        }
//                    }
//                    setPathState(pathState. WAIT_SHOOT2);
//                    telemetry.addLine("Second shooting started");
//                    break;
//
//                case WAIT_SHOOT2:
//                    if (pathTimer.getElapsedTimeSeconds() > 2.0) {
//                        telemetry. addLine("Second shooting complete");
//                        setPathState(pathState.SHOOTMOVE);
//                    }
//                    break;
//
//                case SHOOTMOVE:
//                    follower.followPath(shoot_forward, true);
//                    setPathState(pathState. DONE);
//                    telemetry. addLine("Moving forward");
//                    break;
//
//                case DONE:
//                    telemetry.addLine("Auto complete!  : )");
//                    break;
//
//                default:
//                    telemetry.addLine("Unknown state!");
//                    break;
            }
        } catch (Exception e) {
            telemetry.addLine("ERROR in statePathUpdate:");
            telemetry.addData("Exception", e.getMessage());
            telemetry.addData("State", currentPathState. toString());
        }
    }

    public void setPathState(pathState newState) {
        currentPathState = newState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        try {
            telemetry.addLine("Starting init.. .");
            telemetry.update();

            // Initialize RRHardware FIRST
            rrhardware = new RRHardware(hardwareMap);
            rrhardware. init();

            // Verify and configure limelight
            if (rrhardware.limelight != null) {
                rrhardware.limelight.setPollRateHz(90);
                rrhardware. limelight.pipelineSwitch(0);
                rrhardware. limelight.start();
            }

            // Initialize timers
            pathTimer = new Timer();
            opModeTimer = new Timer();
            actionTimer = new Timer();

            // Initialize follower
            follower = Constants.createFollower(hardwareMap);
            buildPaths();
            follower.setPose(startPose);

            currentPathState = pathState.MOVETOSHOOT;

            telemetry.addLine("Init Complete!");
            telemetry.update();
        } catch (Exception e) {
            telemetry.addLine("INIT ERROR:");
            telemetry.addData("Exception", e.getMessage());
            telemetry.update();
        }
    }

    @Override
    public void start(){
        opModeTimer.resetTimer();
        pathTimer.resetTimer();
    }

    @Override
    public void loop() {
        try {
            if (follower != null) {
                follower. update();
            }

            statePathUpdate();

            telemetry.addData("State", currentPathState.toString());
            telemetry.addData("State Time", pathTimer.getElapsedTimeSeconds());
            telemetry.addData("Follower Busy", follower.isBusy());
            telemetry.addData("AprilTag ID", id);
            telemetry.addData("Motif", motif);
            if (follower != null && follower.getPose() != null) {
                telemetry.addData("x", follower.getPose().getX());
                telemetry. addData("y", follower. getPose().getY());
                telemetry.addData("heading", Math.toDegrees(follower.getPose().getHeading()));
            }
            telemetry.update();
        } catch (Exception e) {
            telemetry.addLine("LOOP ERROR:");
            telemetry.addData("Exception", e. getMessage());
            telemetry. update();
        }
    }
}