package org.firstinspires.ftc.teamcode.StatesScripts;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
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
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.QualifierScripts.RRHardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

@Autonomous
public class backendAutoRED extends LinearOpMode {

    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcherLeft, launcherRight;
    public Servo sorter, outtakeTransferLeft, outtakeTransferRight, launcherTurn, limelightTurn;
    public Limelight3A limelight;
    public ColorSensor colorSensor;
    public AnalogInput floodgate;

    private int pathState;

    public final double[] intakePos = {1.0, 0.6, 0.2};//*/{0.4, 0.0, 0.8};// sorter servo positions for outtaking
    public final double[] outtakePos = {0.4, 0.0, 0.8};//*/{1.0, 0.6, 0.2}; // sorter servo positions for intaking
    public byte[] sorterContents = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green

    private boolean launchingPurple = false;
    private boolean launchingGreen = false;
    RRHardware hardware;
    odoteleop odoTeleop;
    private Follower follower;
    private byte launchProgress;
    private byte sorterPos;
    private ElapsedTime launchTimer, pathTimer;
    private Timer opModeTimer;
    private int obeliskId;
    String motif = "null";
    private int limelightAttempts;
    private ArrayList<String> log;
    private LLResult result;
    private int sorterInitial;
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance



    @Override
    public void runOpMode() throws InterruptedException{
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

        hardware = new RRHardware(hardwareMap);
        odoTeleop = new odoteleop(hardwareMap);

        pathState = 0;
        pathTimer = new ElapsedTime();
        opModeTimer = new Timer();
        launchTimer = new ElapsedTime();
        follower = Constants.createFollower(hardwareMap);

        log = new ArrayList<String>();

        log.add("Log:");

        sorterPos = 2;
        launchProgress = 0;
        limelightAttempts = 0;
        sorterInitial = 0;
        buildPaths();

        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        follower.setPose(startPose);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);

        waitForStart();
        hardware.sorterContents[0] = 1;
        hardware.sorterContents[1] = 1; //purple
        hardware.sorterContents[2] = 2; //green
        opModeTimer.resetTimer();
        obeliskId = 0;


        setPathState(pathState);
        while (opModeIsActive()) {
            follower.update(); // Update Pedro Pathing
            statePathUpdate(); // Update autonomous state machine

            // Log values to Panels and Driver Station

            panelsTelemetry.debug("Path State", pathState);
            panelsTelemetry.debug("X", follower.getPose().getX());
            panelsTelemetry.debug("Y", follower.getPose().getY());
            panelsTelemetry.debug("Heading", follower.getPose().getHeading());
            panelsTelemetry.debug("Obelisk Id", obeliskId);
            for (String str : log) {
                panelsTelemetry.addLine(str);
            }
            panelsTelemetry.update(telemetry);
        }
    }
    private PathChain start_driveToFirstSpike, firstSpike_firstArtifactCollect, firstSpike_secondArtifactCollect,
            firstSpike_thirdArtifactCollect, firstSpike_shoot, shoot_forward;

    //poses initialized
    private final Pose startPose = new Pose(144-56, 9, Math.toRadians(90));
    private final Pose mid = new Pose(144-58, 44);
    private final Pose beforeFirstSpike = new Pose(144-60,48, Math.toRadians(0));

    private final Pose firstSpike1 = new Pose(144-45,44, Math.toRadians(0)); //5.5 in artifact
    private final Pose firstSpike2 = new Pose(144-40,44, Math.toRadians(0));
    private final Pose firstSpike3 = new Pose(144-30,44, Math.toRadians(0));
    private final Pose startPose2 = new Pose(144-56, 10, Math.toRadians(90));

    private final Pose forward = new Pose(144-56, 36, Math.toRadians(90));

    //path initializing
    public void buildPaths() {
        start_driveToFirstSpike = follower.pathBuilder()
                .addPath(new BezierLine(startPose, beforeFirstSpike))
                .setLinearHeadingInterpolation(startPose.getHeading(), beforeFirstSpike.getHeading())
                .build();
        firstSpike_firstArtifactCollect = follower.pathBuilder()
                .addPath(new BezierCurve(beforeFirstSpike, mid, firstSpike1))
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
    //public boolean running = false;
    public int b = 1400;
    public void statePathUpdate() {
        switch(pathState) {
            case 0:
                limelight.pipelineSwitch(0);
                limelightTurn.setPosition(.7);
                launcherTurn.setPosition(1-0.28);
                intake.setPower(1);
                if (pathTimer.milliseconds() < 500) break;
                if (limelightAttempts == 0) {
                    log.add("Attempting to Fetch Result...");
                    hardware.limelight.start();
                }
                result = hardware.limelight.getLatestResult();
                if (result != null && result.isValid()) { //add time elapsed too?
                    log.add("Found April Tags");
                    List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                    for (LLResultTypes.FiducialResult fiducial : fiducials) {
                        int id = fiducial.getFiducialId();
                        log.add("AprilTag Detected: " + id);
                        if (id >= 21 && id <= 23) {
                            obeliskId = id;
                            limelightAttempts = 99;
                            break;
                        }
                    }
                } else {
                    log.add("Failed to Find April Tags. Result is " + (result == null ? "null" : "invalid"));
                }
                limelightAttempts++;
                if (limelightAttempts > 16) {
                    hardware.limelight.stop();
                    sorterPos = (byte)((obeliskId + 2) % 3);
                    setPathState(1);
                }
                break;
            case 1: //ready to launch
                hardware.launcherLeft.setVelocity(b);
                hardware.launcherRight.setVelocity(b);
                hardware.sorter.setPosition(hardware.outtakePos[sorterPos]);
                launchProgress = 0;
                setPathState(2);
                break;
            case 2: //launch
                if (sorterPos == obeliskId % 3) {
                    launchAndSetPathState(3);
                } else {
                    launch();
                }
                break;
            case 3: //align to intake
                launcherLeft.setVelocity(0);
                launcherRight.setVelocity(0);
                follower.followPath(start_driveToFirstSpike, true);
                hardware.intake.setPower(1);
                setPathState(4);
                break;
            case 4: //intake first
                if (hardware.sorter.getPosition() != hardware.intakePos[2]) {
                    hardware.sorter.setPosition(hardware.intakePos[2]);
                    hardware.sorterContents[2] = 2;
                } else if (pathTimer.milliseconds() > 1250) {
                    follower.followPath(firstSpike_firstArtifactCollect, true);
                    setPathState(5);
                }
                break;
            case 5: //intake second
                if (hardware.sorter.getPosition() != hardware.intakePos[1]  && pathTimer.milliseconds() > 1250) {
                    hardware.sorter.setPosition(hardware.intakePos[1]);
                    hardware.sorterContents[1] = 1;
                } else if (pathTimer.milliseconds() > 2500) {
                    follower.followPath(firstSpike_secondArtifactCollect, true);
                    setPathState(6);
                }
                break;
            case 6: //intake third
                if(hardware.sorter.getPosition() != hardware.intakePos[0] && pathTimer.milliseconds() > 1250) {
                    hardware.sorter.setPosition(hardware.intakePos[0]);
                    hardware.sorterContents[0] = 1;
                } else if (pathTimer.milliseconds() > 2500) {
                    follower.followPath(firstSpike_thirdArtifactCollect);
                    setPathState(7);
                }
                break;
            case 7: //move to launch
                if(pathTimer.milliseconds() > 1250) {
                    hardware.sorter.setPosition(hardware.outtakePos[sorterPos]);
                    launcherTurn.setPosition(0.7);
                    hardware.launcherLeft.setVelocity(b + 20);
                    hardware.launcherRight.setVelocity(b + 20);
                    follower.followPath(firstSpike_shoot);
                    setPathState(8);
                }
                break;
            case 8: //launch
                if (sorterPos == obeliskId % 3) {
                    launchAndSetPathState(9);
                } else {
                    launch();
                }
                break;
            case 9: //move forward to end
                hardware.launcherLeft.setVelocity(0);
                hardware.launcherRight.setVelocity(0);
                follower.followPath(shoot_forward, true);
                if (!follower.isBusy()) {
                    setPathState(10);
                }
                break;
            case 10:
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
            default:
                requestOpModeStop();
                //telemetry.addLine("death");
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.reset();
    }

    public void launch() {
        if (hardware.launcherLeft.getVelocity() < 1390) return;
        switch (launchProgress) {
            case 0:
                hardware.intake.setPower(1);
                hardware.sorter.setPosition(hardware.outtakePos[sorterPos]);
                launchTimer.reset();
                launchProgress = 1;
                break;
            case 1:
                if (launchTimer.milliseconds() > 1300) {
                    hardware.intake.setPower(0);
                    hardware.outtakeTransferLeft.setPosition(hardware.liftPos[1]);
                    hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[1]);
                    launchTimer.reset();
                    launchProgress = 2;
                }
                break;
            case 2:
                if (launchTimer.milliseconds() > 750) {
                    hardware.outtakeTransferLeft.setPosition(hardware.liftPos[0]);
                    hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[0]);
                    launchTimer.reset();
                    launchProgress = 3;
                }
                break;
            case 3:
                if (launchTimer.milliseconds() > 400) {
                    sorterPos += 2;
                    sorterPos %= 3;
                    launchProgress = 0;
                }
                break;
        }
    }

    public void launchAndSetPathState(int state) {
        if (hardware.launcherLeft.getVelocity() < 1320) return;
        switch (launchProgress) {
            case 0:
                hardware.intake.setPower(1);
                hardware.sorter.setPosition(hardware.outtakePos[sorterPos]);
                launchTimer.reset();
                launchProgress = 1;
                break;
            case 1:
                if (launchTimer.milliseconds() > 1500) {
                    hardware.intake.setPower(0);
                    hardware.outtakeTransferLeft.setPosition(hardware.liftPos[1]);
                    hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[1]);
                    launchTimer.reset();
                    launchProgress = 2;
                }
                break;
            case 2:
                if (launchTimer.milliseconds() > 1000) {
                    hardware.outtakeTransferLeft.setPosition(hardware.liftPos[0]);
                    hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[0]);
                    launchTimer.reset();
                    launchProgress = 3;
                }
                break;
            case 3:
                if (launchTimer.milliseconds() > 400) {
                    sorterPos += 2;
                    sorterPos %= 3;
                    launchProgress = 0;
                    setPathState(state);
                }
                break;
        }
    }
}

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