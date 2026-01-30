//package org.firstinspires.ftc.teamcode.QualifierScripts;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.PathChain;
//import com.pedropathing.util.Timer;
//import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.LLResultTypes;
//import com.qualcomm.hardware.limelightvision.Limelight3A;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.hardware.AnalogInput;
//import com.qualcomm.robotcore.hardware.ColorSensor;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.Servo;
//
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//
//import java.util.List;
//
//@Autonomous
//public class backendAutoBLUE extends OpMode {
//    int id = -1;
//    String motif = "null";
//
//    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
//    public DcMotorEx launcherLeft, launcherRight;
//    public Servo sorter, outtakeTransferLeft, outtakeTransferRight, launcherTurn, limelightTurn;
//    public Limelight3A limelight;
//    public ColorSensor colorSensor;
//    public AnalogInput floodgate;
//
//    int red, green, blue;
//    float[] hsvValues = new float[3];
//    boolean nextPos = true;
//    public final double[] intakePos = {1.0, 0.6, 0.2};//*/{0.4, 0.0, 0.8};// sorter servo positions for outtaking
//    public final double[] outtakePos = {0.4, 0.0, 0.8};//*/{1.0, 0.6, 0.2}; // sorter servo positions for intaking
//    public double sorterOffset = 0d;
//    public byte[] sorterContents = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green
//    public double[] liftPos = {0.6, 0.2};
//
//    public int currentPos = 0; // 0-2
//    int targetTps = 0; // protect launcher tps from override
//
//    // initialize flags
//    public boolean intaking = false;
//    private boolean launchingPurple = false;
//    private boolean launchingGreen = false;
//    RRHardware rrHardware;
//
//
//    private Follower follower;
//    private Timer pathTimer, actionTimer, opModeTimer;
//    //private ElapsedTime runtime = new ElapsedTime();
//
//    pathState pathState;
//    public enum pathState {
//        APRILTAGLOOKSIES,
//        SHOOT,
//        STARTTOBEFOREPICKUP,
//        PICKUP1,
//        PICKUP2,
//        PICKUP3,
//        PICKUPTOSHOOT,
//        SHOOT2,
//        SHOOTFORWARD,
//        END
//
//    }
//    float bounds_X = 4f;
//    String lastPos = "None";
//
//    private PathChain start_forward, forward_firstpickup1, fp1_secondpickup1,
//            sp1_thirdpickup1, tp1_start, start_forward2;
//
//    //poses initialized
//    private final Pose startPose = new Pose(56, 9, Math.toRadians(90));
//    private final Pose forward = new Pose(56, 12, Math.toRadians(90));
//    //private final Pose beforeFirstSpike = new Pose(46,25, Math.toRadians(135));
//
//    private final Pose firstSpike1 = new Pose(38,36, Math.toRadians(180)); //5.5 in artifact
//    private final Pose firstSpike2 = new Pose(33,36, Math.toRadians(180));
//    private final Pose firstSpike3 = new Pose(28,36, Math.toRadians(180));
//
//
//    //path initializing
//    public void buildPaths() {
//        start_forward = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, forward))
//                .setLinearHeadingInterpolation(startPose.getHeading(), forward.getHeading())
//                .build();
//        forward_firstpickup1 = follower.pathBuilder()
//                .addPath(new BezierLine(forward, firstSpike1))
//                .setLinearHeadingInterpolation(forward.getHeading(), firstSpike1.getHeading())
//                .build();
//        fp1_secondpickup1 = follower.pathBuilder()
//                .addPath(new BezierLine(firstSpike1, firstSpike2))
//                .setLinearHeadingInterpolation(firstSpike1.getHeading(), firstSpike2.getHeading())
//                .build();
//        sp1_thirdpickup1 = follower.pathBuilder()
//                .addPath(new BezierLine(firstSpike2, firstSpike3))
//                .setLinearHeadingInterpolation(firstSpike2.getHeading(), firstSpike3.getHeading())
//                .build();
//        tp1_start = follower.pathBuilder()
//                .addPath(new BezierLine(firstSpike3, startPose))
//                .setLinearHeadingInterpolation(firstSpike3.getHeading(), startPose.getHeading()) //.setReversed() //hopefully backwards drive
//                .build();
//        start_forward2 = follower.pathBuilder()
//                .addPath(new BezierLine(startPose, forward))
//                .setLinearHeadingInterpolation(90, forward.getHeading())
//                .build();
//    }
//
//    public void aprilTagOuttake() {
//        launcherTurn.setPosition(0.15);
//        if (id == 21) { //gpp (1150 close, 1350 far)
//            rrHardware.tryLaunch(true, 2, 1350);
//            rrHardware.tryLaunch(true, 1, 1350);
//            rrHardware.tryLaunch(true, 1, 1350);
//        }
//        if (id == 22) { //pgp
//            rrHardware.tryLaunch(true, 1, 1350);
//            rrHardware.tryLaunch(true, 2, 1350);
//            rrHardware.tryLaunch(true, 1, 1350);
//        }
//        if (id == 23) { //ppg
//            rrHardware.tryLaunch(true, 1, 1350);
//            rrHardware.tryLaunch(true, 1, 1350);
//            rrHardware.tryLaunch(true, 2, 1350);
//        } else { //gpp
//            rrHardware.tryLaunch(true, 2, 1350);
//            rrHardware.tryLaunch(true, 1, 1350);
//            rrHardware.tryLaunch(true, 1, 1350);
//        }
//    }
//
//    public void statePathUpdate() {
//        switch(pathState) {
//            case APRILTAGLOOKSIES:
//                LLResult result = limelight.getLatestResult();
//                //BoundingBox();
//                if (result != null && result.isValid()) { //add time elapsed too?
//                    String motif = "null";
//                    int id = 0;
//                    if (result != null && result.isValid()) {
//                        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
//
//                        for (LLResultTypes.FiducialResult fiducial : fiducials) {
//                            id = fiducial.getFiducialId();
//                        }
//
//                        if (id == 21) {
//                            motif = "gpp";
//                        } else if (id == 22) {
//                            motif = "pgp";
//                        } else if (id == 23) {
//                            motif = "ppg";
//                        }
//
//                        telemetry.addData("tag found", id);
//                        telemetry.addData("motif", motif);
//
//                        telemetry.update();
//                    } else {
//                        telemetry.addLine("No apriltag found");
//                        telemetry.update();
//                    }
//                }
//                telemetry.addData("tag mighta been found", id);
//                telemetry.addData("motif", motif);
//                setPathState(pathState.SHOOT); //reset timer and make new state
//                break;
//            case SHOOT:
//                if (!follower.isBusy()) {
//                    aprilTagOuttake();
//                    setPathState(pathState.STARTTOBEFOREPICKUP);
//                }
//                break;
//            case STARTTOBEFOREPICKUP:
//                if (!follower.isBusy()) { //note: after shoot and change time to something for whole auto
//                    follower.followPath(star, true);
//                    sleep(1000); //for testing
//                    setPathState(pathState.PICKUP1);
//                }
//                telemetry.addLine(" done to pickup");
//                break;
//            case PICKUP1:
//                if (!follower.isBusy()) {
//                    rrHardware.doIntakeGreen();
//                    sleep(4000);
//                    // rrHardware.intake1(); //make sure this doesn't stop other functions
//                    follower.followPath(firstSpike_firstArtifactCollect, true);
//                    setPathState(pathState.PICKUP2);
//                }
//                telemetry.addLine(" done pickup 1");
//                break;
//            case PICKUP2:
//                if (!follower.isBusy()) {
//                    rrHardware.doIntakePurple1();
//                    sleep(4000);
//                    follower.followPath(firstSpike_secondArtifactCollect, true);
//                    setPathState(pathState.PICKUP3);
//                }
//                telemetry.addLine(" done pickup 2");
//                break;
//            case PICKUP3:
//                if (!follower.isBusy()) {
//                    rrHardware.doIntakePurple2();
//                    sleep(4000);
//                    follower.followPath(firstSpike_thirdArtifactCollect, true);
//                    rrHardware.dontFallOut();
//                    setPathState(pathState.PICKUPTOSHOOT);
//                }
//                telemetry.addLine(" done pickup 3");
//                break;
//            case PICKUPTOSHOOT:
//                if (!follower.isBusy()) {
//                    follower.followPath(firstSpike_shoot, true);
//                    setPathState(pathState.SHOOT2);
//                }
//                telemetry.addLine(" done shooting");
//                break;
//            case SHOOT2:
//                if (!follower.isBusy()) {
//                    aprilTagOuttake();
////                    sleep(5000);
//                    setPathState(pathState.SHOOTFORWARD);
//                }
//                break;
//            case SHOOTFORWARD:
//                if (!follower.isBusy()) { //note: change time to something for whole auto
//                    follower.followPath(shoot_forward, true);
//                    setPathState(pathState.END);
//                }
//                telemetry.addLine(" done! :)");
//                break;
//
//            case END:
//                telemetry.addLine("Nothing running");
//                break;
//
//            default:
//                telemetry.addLine("death");
//                break;
//        }
//    }
//
//    public final void sleep(long milliseconds) {
//        try {
//            Thread.sleep(milliseconds);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//    public void setPathState(pathState newState) {
//        pathState = newState;
//        pathTimer.resetTimer();
//    }
//
//    @Override
//    public void init() {
//        frontLeft = hardwareMap.get(DcMotor.class, "fl");
//        frontRight = hardwareMap.get(DcMotor.class, "fr");
//        backLeft = hardwareMap.get(DcMotor.class, "bl");
//        backRight = hardwareMap.get(DcMotor.class, "br");
//
//        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//
//
//        launcherLeft = hardwareMap.get(DcMotorEx.class, "launcherL");
//        launcherRight = hardwareMap.get(DcMotorEx.class, "launcherR");
//
//        launcherTurn = hardwareMap.get(Servo.class, "launcherYaw");
//        limelightTurn = hardwareMap.get(Servo.class, "limeservo");
//
//        intake = hardwareMap.get(DcMotor.class, "intake"); // e2
//        sorter = hardwareMap.get(Servo.class, "sorter"); //c2
//        outtakeTransferLeft = hardwareMap.get(Servo.class, "liftLeft"); // c5
//        outtakeTransferRight = hardwareMap.get(Servo.class, "liftRight"); // c5
//        limelight = hardwareMap.get(Limelight3A.class, "limelight");
//        colorSensor = hardwareMap.get(ColorSensor.class, "colorSens");
//
//        floodgate = hardwareMap.get(AnalogInput.class, "floodgate");
//
//
//        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        launcherRight.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
//        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        intake.setDirection(DcMotorSimple.Direction.REVERSE);
//
//        limelight = hardwareMap.get(Limelight3A.class, "limelight");
//
//        //turretAxon = hardwareMap.get(CRServo.class, "axon");
//        limelight.setPollRateHz(90);
//        limelight.pipelineSwitch(1); // motif pipeline (ID=21,22,23)
//        //limelight.pipelineSwitch(1); // this is for left goal (ID=20)
//
//        limelight.start();
//
//        rrHardware = new RRHardware(hardwareMap);
//
//        rrHardware.sorterContents[0] = 1;
//        rrHardware.sorterContents[1] = 1; //purple
//        rrHardware.sorterContents[2] = 2; //green
//
//        pathState = pathState.APRILTAGLOOKSIES;
//        pathTimer = new Timer();
//        opModeTimer = new Timer();
//        follower = Constants.createFollower(hardwareMap);
//
//        buildPaths();
//        follower.setPose(startPose);
//    }
//    @Override
//    public void start(){
//        opModeTimer.resetTimer();
//        setPathState(pathState);
//    }
//
//    @Override
//    public void loop() {
//        if (getRuntime() < 30) {
//            follower.update();
//            statePathUpdate();
//
//            if (pathState == pathState.END) {
//                return;
//            }
//
//            telemetry.addData("path state", pathState.toString());
//            telemetry.addData("x", follower.getPose().getX());
//            telemetry.addData("y", follower.getPose().getY());
//            telemetry.addData("heading", follower.getPose().getHeading());
//            telemetry.addData("path time", pathTimer.getElapsedTimeSeconds());
//        }
//        else {
//            requestOpModeStop();
//        }
//    }
//}
//
///* process:
//    1. Start pose
//        - 90 deg angle
//        - 2 purple and 1 green preloaded ball, order known and defined
//            ex. slot 1,2,3 with servo positions = green, purple, purple
//    2. View april tag and detect pattern
//        - # corresponds to each pattern, when detected set: number = shooting order as key
//    3. Shoot the correct order of preloaded balls
//        - slot 1,2,3 order pulled from apriltag detected,
//        - shooting motors on (stay on whole time?),
//        - servo up,
//        - repeat 1/3
//    4. move to collect 3 balls on spike mark in GPP order
//    5. move back to initial position, repeat step 3 with new sorter store
//        - note: color sensor not needed (edit: we're using it)
//
//also how to get extra (?)ranking points like moving off the baselines?
//*/