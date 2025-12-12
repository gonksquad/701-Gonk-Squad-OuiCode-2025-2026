package org.firstinspires.ftc.teamcode. QualifierScripts;

import com.pedropathing.follower. Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing. paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision. Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode. Autonomous;
import com.qualcomm.robotcore.eventloop. opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm. robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware. DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com. qualcomm.robotcore.hardware. Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires. ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode. pedroPathing.Constants;

import java.util.List;

@Autonomous
public class frontendAutoRed extends OpMode {

    String motif = "null";
    int id = 0;

    RRHardware rrhardware;

    private Follower follower;
    private Timer pathTimer, opModeTimer;

    pathState currentPathState;
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

    private PathChain start_driveToShoot, shoot_beforeThirdSpike, thirdSpike_firstArtifactCollect,
            thirdSpike_secondArtifactCollect, thirdSpike_thirdArtifactCollect, thirdSpike_shoot, shoot_forward;

    //poses initialized
    private final Pose startPose = new Pose(21, 123.3, Math.toRadians(324));
    private final Pose shootpose = new Pose(59, 85, Math.toRadians(324));
    private final Pose beforeThirdSpike = new Pose(44,84, Math.toRadians(180));

    private final Pose thirdSpike1 = new Pose(40.5,84, Math.toRadians(180));
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
                .setLinearHeadingInterpolation(shootpose. getHeading(), beforeThirdSpike.getHeading())
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
                    if (!follower.isBusy()) {
                        follower. followPath(start_driveToShoot);
                    }
                    setPathState(pathState.APRILTAGLOOKSIES);
                    telemetry.addLine("MOVETOSHOOT done");
                    telemetry. update();
                    break;
                case APRILTAGLOOKSIES:
                    if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 2) {
                        if (rrhardware != null && rrhardware.limelightTurn != null) {
                            rrhardware.limelightTurn.setPosition(.5);
                        } else {
                            telemetry.addLine("ERROR: limelightTurn is null");
                            telemetry.update();
                        }

                        if (rrhardware != null && rrhardware. limelight != null) {
                            LLResult result = rrhardware.limelight.getLatestResult();
                            if(result != null && result.isValid()) {
                                List<LLResultTypes. FiducialResult> fiducials = result.getFiducialResults();

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
                                telemetry. update();
                            } else {
                                telemetry.addLine("No apriltag found");
                                telemetry.update();
                            }
                        } else {
                            telemetry.addLine("ERROR: limelight is null");
                            telemetry.update();
                        }
                    }
                    setPathState(pathState.SHOOT);
                    telemetry. addLine("APRILTAGLOOKSIES done");
                    telemetry.update();
                    break;
                case SHOOT:
                    if (!follower. isBusy() && pathTimer.getElapsedTimeSeconds() > 6) {
                        if (rrhardware != null) {
                            if(id == 21) {
                                rrhardware.shootgpp();
                            } else if(id == 22) {
                                rrhardware.shootpgp();
                            } else if(id == 23) {
                                rrhardware.shootppg();
                            } else {
                                rrhardware.shootgpp();
                                telemetry.addLine("no apriltag found :(");
                                telemetry.update();
                            }
                        } else {
                            telemetry.addLine("ERROR: rrhardware is null in SHOOT");
                            telemetry.update();
                        }
                    }
                    setPathState(pathState.STARTTOBEFOREPICKUP);
                    telemetry.addLine("apriltag section done");
                    telemetry.update();
                    break;
                case STARTTOBEFOREPICKUP:
                    if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 10) {
                        follower.followPath(shoot_beforeThirdSpike, true);
                    }
                    setPathState(pathState. S1PICKUP1);
                    telemetry.addLine("before pickup");
                    telemetry.update();
                    break;
                case S1PICKUP1:
                    if (!follower.isBusy()) {
                        if (rrhardware != null) {
                            rrhardware.intake1();
                        }
                        follower.followPath(thirdSpike_firstArtifactCollect, true);
                    }
                    setPathState(pathState.S1PICKUP2);
                    telemetry.addLine(" done pickup 1");
                    telemetry.update();
                    break;
                case S1PICKUP2:
                    if (!follower.isBusy()) {
                        if (rrhardware != null) {
                            rrhardware.intake2();
                        }
                        follower.followPath(thirdSpike_secondArtifactCollect, true);
                    }
                    setPathState(pathState.S1PICKUP3);
                    telemetry.addLine(" done pickup 2");
                    telemetry.update();
                    break;
                case S1PICKUP3:
                    if (!follower.isBusy()) {
                        if (rrhardware != null) {
                            rrhardware.intake3();
                        }
                        follower.followPath(thirdSpike_thirdArtifactCollect, true);
                    }
                    setPathState(pathState. PICKUPTOSHOOT);
                    telemetry.addLine(" done pickup 3");
                    telemetry.update();
                    break;
                case PICKUPTOSHOOT:
                    if (!follower.isBusy()) {
                        follower.followPath(thirdSpike_shoot, true);
                    }
                    setPathState(pathState.SHOOT2);
                    telemetry.addLine("moved to shoot");
                    telemetry.update();
                    break;
                case SHOOT2:
                    if (!follower.isBusy()) {
                        if (rrhardware != null) {
                            if(id == 21) {
                                rrhardware.shootgpp();
                            } else if(id == 22) {
                                rrhardware. shootpgp();
                            } else if(id == 23) {
                                rrhardware.shootppg();
                            } else {
                                rrhardware.shootppg();
                            }
                        }
                    }
                    setPathState(pathState.SHOOTMOVE);
                    telemetry.addLine("done shooting");
                    telemetry.update();
                    break;
                case SHOOTMOVE:
                    if (! follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 5) {
                        follower. followPath(shoot_forward, true);
                    }
                    telemetry.addLine(" done!  :)");
                    telemetry.update();
                    break;
                default:
                    telemetry.addLine("Nothing running :(");
                    telemetry. update();
                    break;
            }
        } catch (Exception e) {
            telemetry.addLine("ERROR in statePathUpdate:");
            telemetry.addData("Exception", e.getMessage());
            telemetry.addData("State", currentPathState. toString());
            telemetry. update();
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
            telemetry.addLine("Creating RRHardware...");
            telemetry.update();
            rrhardware = new RRHardware(hardwareMap);

            telemetry.addLine("Calling rrhardware.init()...");
            telemetry.update();
            rrhardware.init();

            // Verify hardware was initialized
            if (rrhardware. limelight == null) {
                telemetry.addLine("WARNING: limelight is null after init!");
                telemetry.update();
            } else {
                telemetry. addLine("Configuring limelight...");
                telemetry.update();
                rrhardware.limelight.setPollRateHz(90);
                rrhardware. limelight.pipelineSwitch(0);
                rrhardware.limelight.start();
            }

            telemetry.addLine("Initializing path system...");
            telemetry. update();
            currentPathState = pathState.MOVETOSHOOT;
            pathTimer = new Timer();
            opModeTimer = new Timer();

            telemetry.addLine("Creating follower...");
            telemetry.update();
            follower = Constants.createFollower(hardwareMap);

            telemetry.addLine("Building paths...");
            telemetry.update();
            buildPaths();
            follower.setPose(startPose);

            telemetry.addLine("Init Complete!");
            telemetry.addData("rrhardware null? ", rrhardware == null);
            telemetry.addData("follower null?", follower == null);
            telemetry.update();
        } catch (Exception e) {
            telemetry. addLine("INIT ERROR:");
            telemetry.addData("Exception", e.getMessage());
            telemetry.addData("Stack trace", e.getStackTrace()[0].toString());
            telemetry.update();
        }
    }

    @Override
    public void start(){
        opModeTimer.resetTimer();
        setPathState(currentPathState);
    }

    @Override
    public void loop() {
        try {
            if (follower != null) {
                follower.update();
            }

            if (rrhardware == null) {
                telemetry.addLine("ERROR: rrhardware is NULL in loop!");
            }

            statePathUpdate();

            telemetry.addData("path state", currentPathState.toString());
            if (follower != null && follower.getPose() != null) {
                telemetry.addData("x", follower.getPose().getX());
                telemetry. addData("y", follower. getPose().getY());
                telemetry.addData("heading", follower.getPose().getHeading());
            }
            telemetry.addData("path time", pathTimer.getElapsedTimeSeconds());
            telemetry.update();
        } catch (Exception e) {
            telemetry.addLine("LOOP ERROR:");
            telemetry.addData("Exception", e. getMessage());
            telemetry. update();
        }
    }
}