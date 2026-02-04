package org.firstinspires.ftc.teamcode.StatesScripts;

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
    private byte launchProgress;
    private byte sorterPos;
    private ElapsedTime launchTimer, pathTimer;
    private Timer actionTimer, opModeTimer;
    //private ElapsedTime runtime = new ElapsedTime();

    pathState pathState;
    public enum pathState {
        APRILTAGLOOKSIES,
        a,
        MOVETOSHOOT,
        k,
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
    private final Pose mid = new Pose(58, 36);
    private final Pose beforeFirstSpike = new Pose(60,34, Math.toRadians(180));

    private final Pose firstSpike1 = new Pose(50,34, Math.toRadians(180)); //5.5 in artifact
    private final Pose firstSpike2 = new Pose(45,34, Math.toRadians(180));
    private final Pose firstSpike3 = new Pose(40,34, Math.toRadians(180));
    private final Pose startPose2 = new Pose(58, 10, Math.toRadians(90));

    private final Pose forward = new Pose(56, 21, Math.toRadians(90));

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
//
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
                    setPathState(pathState.MOVETOSHOOT);

                }
                break;
            case MOVETOSHOOT:
                if (pathTimer.milliseconds() > 1250) {
                    launcherLeft.setVelocity(1320);
                    launcherRight.setVelocity(1320);
                    //rrHardware.sorter.setPosition(rrHardware.outtakePos[2]);
                    //sorterPos = 2;
                    setPathState(pathState.k);
                }
                break;
            case k:
                if (!follower.isBusy()) {
                    setPathState(pathState.SHOOT);
                }
                break;
            case SHOOT:
                launchAndSetPathState(pathState.b);
                break;
            case b:
                if (!follower.isBusy() && yas) {
                    setPathState(pathState.STARTTOBEFOREPICKUP);
                }
                break;
            case STARTTOBEFOREPICKUP:
                launcherLeft.setVelocity(0);
                launcherRight.setVelocity(0);
                follower.followPath(start_driveToFirstSpike, true);
                rrHardware.intake.setPower(1);
                setPathState(pathState.c);
                telemetry.addLine(" done to pickup");
                break;
            case c:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUP1);
                }
                break;
            case PICKUP1:
                if (rrHardware.sorter.getPosition() != rrHardware.intakePos[0]) {
                    rrHardware.sorter.setPosition(rrHardware.intakePos[0]);
                    rrHardware.sorterContents[0] = 2;
                } else if (pathTimer.milliseconds() > 1250) {
                    follower.followPath(firstSpike_firstArtifactCollect, true);
                    setPathState(pathState.d);
                }
                break;
            case d:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUP2);
                }
                break;
            case PICKUP2:
                if (rrHardware.sorter.getPosition() != rrHardware.intakePos[1]  && pathTimer.milliseconds() > 1250) {
                    rrHardware.sorter.setPosition(rrHardware.intakePos[1]);
                    rrHardware.sorterContents[1] = 1;
                } else if (pathTimer.milliseconds() > 2500) {
                    follower.followPath(firstSpike_secondArtifactCollect, true);
                    setPathState(pathState.e);
                }
                break;
            case e:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUP3);
                }
                break;
            case PICKUP3:
                if(rrHardware.sorter.getPosition() != rrHardware.intakePos[2] && pathTimer.milliseconds() > 1250) {
                    rrHardware.sorter.setPosition(rrHardware.intakePos[2]);
                    rrHardware.sorterContents[2] = 1;
                } else if (pathTimer.milliseconds() > 2500) {
                    follower.followPath(firstSpike_thirdArtifactCollect);
                    setPathState(pathState.f);
                }
                break;
            case f:
                if (!follower.isBusy()) {
                    setPathState(pathState.PICKUPTOSHOOT);
                }
                break;
            case PICKUPTOSHOOT:
                if(pathTimer.milliseconds() > 1250) {
                    rrHardware.sorter.setPosition(rrHardware.outtakePos[2]);
                    rrHardware.launcherLeft.setVelocity(gg + 20);
                    rrHardware.launcherRight.setVelocity(gg + 20);
                    follower.followPath(firstSpike_shoot);
                    setPathState(pathState.g);
                }
                break;
            case g:
                if (!follower.isBusy()) {
                    setPathState(pathState.SHOOT2);
                }
                break;
            case SHOOT2:
                i = 1;
                launchAndSetPathState(pathState.h);
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
                    rrHardware.launcherLeft.setVelocity(0);
                    rrHardware.launcherRight.setVelocity(0);
                    setPathState(pathState.END);
                }
                break;
            case END:
                telemetry.addLine("Nothing running");
                break;
            default:
                requestOpModeStop();
                telemetry.addLine("death");
                break;
        }
    }

    public void setPathState(pathState newState) {
        pathState = newState;
        pathTimer.reset();
    }

    public void launch() {
        if (rrHardware.launcherLeft.getVelocity() < gg) return;
        switch (launchProgress) {
            case 0:
                rrHardware.intake.setPower(0.5);
                if (p == 1) {
                    if (motif == "gpp") {
                        rrHardware.tryLaunch(2, (int)gg);
                    }
                    else if (motif == "pgp" || motif == "ppg") {
                        rrHardware.tryLaunch(1, (int)gg);
                    }
                    else {
                        telemetry.addData("not operable1", p);
                    }
                    //1st if motif = gpp - shoot g, else if motif = pgp or ppg - shoot p
                }
                else if (p == 2) {
                    if (motif == "pgp") {
                        rrHardware.tryLaunch(2, (int)gg);
                    }
                    else if (motif == "gpp" || motif == "ppg") {
                        rrHardware.tryLaunch(1, (int)gg);
                    }
                    else {
                        telemetry.addData("not operable2", p);
                    }
                    //2nd if motif = pgp - shoot g, else if motif = gpp or ppg - shoot p
                }
                else if (p == 3) {
                    if (motif == "ppg") {
                        rrHardware.tryLaunch(2, (int)gg);
                    }
                    else if (motif == "gpp" || motif == "pgp") {
                        rrHardware.tryLaunch(1, (int)gg);
                    }
                    else {
                        telemetry.addData("not operable3", p);
                    }
                    //3rd if motif = ppg - shoot g, else if motif = gpp or pgp - shoot p
                }

                launchTimer.reset();
                launchProgress = 1;
                break;
            case 1:
                if (launchTimer.milliseconds() > 1250) {
                    rrHardware.outtakeTransferLeft.setPosition(rrHardware.liftPos[1]);
                    rrHardware.outtakeTransferRight.setPosition(1 - rrHardware.liftPos[1]);
                    launchTimer.reset();
                    launchProgress = 2;
                }
                break;
            case 2:
                if (launchTimer.milliseconds() > 750) {
                    rrHardware.intake.setPower(0);
                    rrHardware.outtakeTransferLeft.setPosition(rrHardware.liftPos[0]);
                    rrHardware.outtakeTransferRight.setPosition(1 - rrHardware.liftPos[0]);
                    launchTimer.reset();
                    launchProgress = 3;
                }
                break;
            case 3:
                if (launchTimer.milliseconds() > 400) {
//                    sorterPos += 2;
//                    sorterPos %= 3;
                    p++;
                    telemetry.addData("number p", p);
                    telemetry.update();
                    launchProgress = 0;
                }
                break;
        }
    }

    public int p = 1;
    public int i = 1;

    public void launchAndSetPathState(pathState pathState) {
        if (rrHardware.launcherLeft.getVelocity() < gg) return;
        switch (launchProgress) {
            case 0:
                rrHardware.intake.setPower(0.5);
                if (i == 1) {
                    if (motif == "gpp") {
                        rrHardware.tryLaunch(2, (int)gg);
                    }
                    else if (motif == "pgp" || motif == "ppg") {
                        rrHardware.tryLaunch(1, (int)gg);
                    }
                    else {
                        telemetry.addData("not operable1", i);
                    }
                    //1st if motif = gpp - shoot g, else if motif = pgp or ppg - shoot p
                }
                else if (i == 2) {
                    if (motif == "pgp") {
                        rrHardware.tryLaunch(2, (int)gg);
                    }
                    else if (motif == "gpp" || motif == "ppg") {
                        rrHardware.tryLaunch(1, (int)gg);
                    }
                    else {
                        telemetry.addData("not operable2", i);
                    }
                    //2nd if motif = pgp - shoot g, else if motif = gpp or ppg - shoot p
                }
                else if (i == 3) {
                    if (motif == "ppg") {
                        rrHardware.tryLaunch(2, (int)gg);
                    }
                    else if (motif == "gpp" || motif == "pgp") {
                        rrHardware.tryLaunch(1, (int)gg);
                    }
                    else {
                        telemetry.addData("not operable3", i);
                    }
                    //3rd if motif = ppg - shoot g, else if motif = gpp or pgp - shoot p
                }

                /*
                motif = gpp pgp ppg
                */
                launchTimer.reset();
                launchProgress = 1;
                break;
            case 1:
                if (launchTimer.milliseconds() > 1500) {
                    rrHardware.outtakeTransferLeft.setPosition(rrHardware.liftPos[1]);
                    rrHardware.outtakeTransferRight.setPosition(1 - rrHardware.liftPos[1]);
                    launchTimer.reset();
                    launchProgress = 2;
                    break;
                }
            case 2:
                if (launchTimer.milliseconds() > 1000) {
                    rrHardware.intake.setPower(0);
                    rrHardware.outtakeTransferLeft.setPosition(rrHardware.liftPos[0]);
                    rrHardware.outtakeTransferRight.setPosition(1 - rrHardware.liftPos[0]);
                    launchTimer.reset();
                    launchProgress = 3;
                    break;
                }
            case 3:
                if (launchTimer.milliseconds() > 400) {
                   //dec sorterpos by 1
//                    sorterPos += 2;
//                    sorterPos %= 3;
                    telemetry.addData("number i", i);
                    telemetry.update();
                    if (i == 1) {
                        i++;
                        launchProgress = 0;
                    }
                    else if (i == 2) {
                        i++;
                        launchProgress = 0;
                    }
                    else if (i == 3) {
                        i = 1;
                        yas = true;
                        setPathState(pathState);
                    }
                    break;
                }
        }
    }
    public boolean yas = false;
    public double gg = 1300;
    public double pp = 1300;


    public void aprilTagOuttake() {

        if (motif == "gpp") { //gpp (1150 close, 1350 far)
            rrHardware.tryLaunch(2, (int)gg);
            rrHardware.tryLaunch(1, (int)pp);
            rrHardware.tryLaunch(1, (int)pp);

        }
        else if (motif == "pgp") { //pgp
            rrHardware.tryLaunch(1, (int)pp);
            rrHardware.tryLaunch(2, (int)gg);
            rrHardware.tryLaunch(1, (int)pp);

        }
        else if (motif == "ppg") { //ppg
            rrHardware.tryLaunch( 1, (int)pp);
            rrHardware.tryLaunch( 1, (int)pp);
            rrHardware.tryLaunch( 2, (int)gg);

        } else { //gpp
            telemetry.addData("yeah", motif);
            telemetry.addData("uh", sorterContents);
            rrHardware.tryLaunch( 2, (int)gg);
            rrHardware.tryLaunch( 1, (int)pp);
            rrHardware.tryLaunch( 1, (int)pp);
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
        pathTimer = new ElapsedTime();
        opModeTimer = new Timer();
        launchTimer = new ElapsedTime();
        follower = Constants.createFollower(hardwareMap);

        buildPaths();
        follower.setPose(startPose);

        waitForStart();
        rrHardware.sorterContents[0] = 1;
        rrHardware.sorterContents[1] = 1; //purple
        rrHardware.sorterContents[2] = 2; //green
        opModeTimer.resetTimer();

        setPathState(pathState);
        while (opModeIsActive()) {
            if (getRuntime() < 30) {
                follower.update();
                statePathUpdate();
                odoTeleop.odoAimTurret(true, true, false);
                telemetry.addData("path state", pathState.toString());
                telemetry.addData("x", follower.getPose().getX());
                telemetry.addData("y", follower.getPose().getY());
                telemetry.addData("heading", follower.getPose().getHeading());
                telemetry.addData("path time", pathTimer.milliseconds());

                telemetry.update();

                if (pathState == pathState.END) {
                    return;
                }

                telemetry.addData("path state", pathState.toString());
                telemetry.addData("x", follower.getPose().getX());
                telemetry.addData("y", follower.getPose().getY());
                telemetry.addData("heading", follower.getPose().getHeading());
                telemetry.addData("path time", pathTimer.milliseconds());
                telemetry.addData("SOrter pso", rrHardware.sorter.getPosition());
                telemetry.addData("not operabledude", i);

                telemetry.update();
            }
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