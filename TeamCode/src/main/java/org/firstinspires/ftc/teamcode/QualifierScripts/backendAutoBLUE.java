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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.StatesScripts.odoteleop;
import org.firstinspires.ftc.teamcode.colorSequence;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.List;

@Autonomous
public class backendAutoBLUE extends LinearOpMode {

    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcherLeft, launcherRight;
    public Servo sorter, outtakeTransferLeft, outtakeTransferRight, launcherTurn, limelightTurn;
    public Limelight3A limelight;
    public ColorSensor colorSensor;
    public AnalogInput floodgate;

    int red, green, blue;
    float[] hsvValues = new float[3];
    boolean nextPos = true;
    public final double[] intakePos = {1.0, 0.6, 0.2};//*/{0.4, 0.0, 0.8};// sorter servo positions for outtaking
    public final double[] outtakePos = {0.4, 0.0, 0.8};//*/{1.0, 0.6, 0.2}; // sorter servo positions for intaking
    public double sorterOffset = 0d;
    public byte[] sorterContents = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green
    public double[] liftPos = {0.6, 0.2};

    public int currentPos = 0; // 0-2
    int targetTps = 0; // protect launcher tps from override

    // initialize flags
    public boolean intaking = false;
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;
    RRHardware rrHardware;
    odoteleop odoTeleop;

    private Follower follower;
    private Timer pathTimer, actionTimer, opModeTimer;
    //private ElapsedTime runtime = new ElapsedTime();

    pathState pathState;
    public enum pathState {
        APRILTAGLOOKSIES,
        a,
        SHOOT,
        b,
        STARTTOBEFOREPICKUP,
        c,
        PICKUP1,
        d,
        PICKUP2,
        e,
        PICKUP3,
        f,
        PICKUPTOSHOOT,
        g,
        SHOOT2,
        h,
        SHOOTFORWARD,
        i,
        END

    }
    float bounds_X = 4f;
    String lastPos = "None";

    String motif = "null";
    int id = 0;

    private PathChain start_driveToFirstSpike, firstSpike_firstArtifactCollect, firstSpike_secondArtifactCollect,
            firstSpike_thirdArtifactCollect, firstSpike_shoot, shoot_forward;

    //poses initialized
    private final Pose startPose = new Pose(56, 9, Math.toRadians(90));
    private final Pose beforeFirstSpike = new Pose(60,34, Math.toRadians(180));

    private final Pose firstSpike1 = new Pose(55,34, Math.toRadians(180)); //5.5 in artifact
    private final Pose firstSpike2 = new Pose(49,34, Math.toRadians(180));
    private final Pose firstSpike3 = new Pose(43,34, Math.toRadians(180));
    private final Pose startPose2 = new Pose(56, 10, Math.toRadians(90));

    private final Pose forward = new Pose(56, 21, Math.toRadians(90));

    //path initializing
    public void buildPaths() {
        start_driveToFirstSpike = follower.pathBuilder()
                .addPath(new BezierLine(startPose, beforeFirstSpike))
                .setLinearHeadingInterpolation(startPose.getHeading(), beforeFirstSpike.getHeading())
                .build();
        firstSpike_firstArtifactCollect = follower.pathBuilder()
                .addPath(new BezierCurve(beforeFirstSpike, firstSpike1))
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
                .addPath(new BezierLine(firstSpike3, startPose2))
                .setLinearHeadingInterpolation(firstSpike3.getHeading(), startPose2.getHeading()) //.setReversed() //hopefully backwards drive
                .build();
        shoot_forward = follower.pathBuilder()
                .addPath(new BezierLine(startPose2, forward))
                .setLinearHeadingInterpolation(startPose2.getHeading(), forward.getHeading())
                .build();
    }

    public boolean running = false;
    public void statePathUpdate() {
        switch(pathState) {
            case APRILTAGLOOKSIES:
                limelight.pipelineSwitch(0);
                limelightTurn.setPosition(.4);
                LLResult result = limelight.getLatestResult();

                if (result != null && result.isValid()) { //add time elapsed too?
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

                telemetry.addData("tag mighta been found", id);
                telemetry.addData("motif", motif);
                setPathState(pathState.a); //reset timer and make new state
                break;
            case a:
                if (!follower.isBusy()) {
                    setPathState(pathState.SHOOT);
                }
                break;
            case SHOOT:
                aprilTagOuttake();
                setPathState(pathState.b);

                break;
            case b:
                if (!follower.isBusy()) {
                    setPathState(pathState.STARTTOBEFOREPICKUP);
                }
                break;
            case STARTTOBEFOREPICKUP:
                follower.followPath(start_driveToFirstSpike, true);
                setPathState(pathState.c);
                telemetry.addLine(" done to pickup");
                break;
            case c:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUP1);
                }
                break;
            case PICKUP1:
                follower.followPath(firstSpike_firstArtifactCollect, true);
                if (pathTimer.getElapsedTimeSeconds() > 2) {
                    rrHardware.doIntakeGreen();
                }
                setPathState(pathState.d);
                telemetry.addLine(" done pickup 1");
                break;
            case d:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUP2);
                }
                break;
            case PICKUP2:
                follower.followPath(firstSpike_secondArtifactCollect, true);
                if (pathTimer.getElapsedTimeSeconds() > 2) {
                    rrHardware.doIntakePurple1();
                }
                setPathState(pathState.e);
                telemetry.addLine(" done pickup 2");
                break;
            case e:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUP3);
                }
                break;
            case PICKUP3:
                follower.followPath(firstSpike_thirdArtifactCollect, true);
                if (pathTimer.getElapsedTimeSeconds() > 2) {
                    rrHardware.doIntakePurple2();
                    rrHardware.dontFallOut();
                }
                setPathState(pathState.f);
                telemetry.addLine(" done pickup 3");
                break;
            case f:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUPTOSHOOT);
                }
                break;
            case PICKUPTOSHOOT:
                follower.followPath(firstSpike_shoot, true);
                setPathState(pathState.g);
                telemetry.addLine(" done shooting");
                break;
            case g:
                if (!follower.isBusy()) {
                    setPathState(pathState.SHOOT2);
                }
                break;
            case SHOOT2:
                odoTeleop.odoAimTurret(true);
                aprilTagOuttake();
                follower.followPath(shoot_forward, true);
                setPathState(pathState.h);
                break;
            case h:
                if (!follower.isBusy()) {
                    setPathState(pathState.SHOOTFORWARD);
                }
                break;
            case SHOOTFORWARD:
                follower.followPath(shoot_forward, true);
                setPathState(pathState.i);
                telemetry.addLine(" done! :)");
                break;
            case i:
                if (!follower.isBusy()) {
                    setPathState(pathState.END);
                }
                break;
            case END:
                telemetry.addLine("Nothing running");
                break;
            default:
                telemetry.addLine("death");
                break;
        }
    }

    public void setPathState(pathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }


    public double gg = 1300;
    public double pp = 1300;

    public void aprilTagOuttake() {
        rrHardware.sorterContents[0] = 1;
        rrHardware.sorterContents[1] = 1; //purple
        rrHardware.sorterContents[2] = 2; //green

        if (motif == "gpp") { //gpp (1150 close, 1350 far)
            rrHardware.tryLaunch(true, 2, (int)gg);
            rrHardware.tryLaunch(true, 1, (int)pp);
            rrHardware.tryLaunch(true, 1, (int)pp);

        }
        else if (motif == "pgp") { //pgp
            rrHardware.tryLaunch(true, 1, (int)pp);
            rrHardware.tryLaunch(true, 2, (int)gg);
            rrHardware.tryLaunch(true, 1, (int)pp);

        }
        else if (motif == "ppg") { //ppg
            rrHardware.tryLaunch(true, 1, (int)pp);
            rrHardware.tryLaunch(true, 1, (int)pp);
            rrHardware.tryLaunch(true, 2, (int)gg);

        } else { //gpp
            telemetry.addData("yeah", motif);
            telemetry.addData("uh", sorterContents);
            rrHardware.tryLaunch(true, 2, (int)gg);
            rrHardware.tryLaunch(true, 1, (int)pp);
            rrHardware.tryLaunch(true, 1, (int)pp);
        }
    }

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        launcherLeft = hardwareMap.get(DcMotorEx.class, "launcherL");
        launcherRight = hardwareMap.get(DcMotorEx.class, "launcherR");

        launcherTurn = hardwareMap.get(Servo.class, "launcherYaw");
        limelightTurn = hardwareMap.get(Servo.class, "limeservo");

        intake = hardwareMap.get(DcMotor.class, "intake"); // e2
        sorter = hardwareMap.get(Servo.class, "sorter"); //c2
        outtakeTransferLeft = hardwareMap.get(Servo.class, "liftLeft"); // c5
        outtakeTransferRight = hardwareMap.get(Servo.class, "liftRight"); // c5
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSens");

        floodgate = hardwareMap.get(AnalogInput.class, "floodgate");


        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherRight.setDirection(DcMotorSimple.Direction.REVERSE);

        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        //turretAxon = hardwareMap.get(CRServo.class, "axon");
        limelight.setPollRateHz(90);
        // limelight.pipelineSwitch(0); // motif pipeline (ID=21,22,23)

        limelight.start();

        rrHardware = new RRHardware(hardwareMap);
        odoTeleop = new odoteleop(hardwareMap);

        pathState = pathState.APRILTAGLOOKSIES;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);

        buildPaths();
        follower.setPose(startPose);

        waitForStart();

        opModeTimer.resetTimer();
        setPathState(pathState);
        while (opModeIsActive()) {
        if (getRuntime() < 30) {
            follower.update();
            statePathUpdate();
            odoTeleop.odoAimTurret(true);
            telemetry.addData("path state", pathState.toString());
            telemetry.addData("x", follower.getPose().getX());
            telemetry.addData("y", follower.getPose().getY());
            telemetry.addData("heading", follower.getPose().getHeading());
            telemetry.addData("path time", pathTimer.getElapsedTimeSeconds());

            telemetry.update();

            if (pathState == pathState.END) {
                return;
            }

         }}}}

/* process:
    1. Start pose
        - 90 deg angle
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
        - note: color sensor not needed (edit: we're using it)

also how to get extra (?)ranking points like moving off the baselines?
*/