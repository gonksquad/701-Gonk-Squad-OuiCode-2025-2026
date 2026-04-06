package org.firstinspires.ftc.teamcode.WorldsScripts;

//import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous
@Config
public class WorldsAutoBlueNear extends LinearOpMode {

    public static class Params {
        public float[] yawangle = {-47, -54, -50, -55, -50, -50};
    }
public static WorldsAutoBlueNear.Params PARAMS = new WorldsAutoBlueNear.Params();

    MecanumDrive drive;
    WorldsAutoHardware hardware;
    Pose2d initialPose;

    Pose2d launchPos = new Pose2d(-28, -22, Math.toRadians(-90));

    @Override
    public void runOpMode() {
        initialPose = new Pose2d(-50, -50, Math.toRadians(-135));
        drive = new MecanumDrive(hardwareMap, initialPose);
        hardware = new WorldsAutoHardware(hardwareMap);

        Action launch0 = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-19, -19), Math.toRadians(-90))
                .build();

        Action pickup1 = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(-23, -58, Math.toRadians(-90)), launchPos.heading)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(launchPos, Math.toRadians(-90))
                .build();

        Action pickup2 = drive.actionBuilder(launchPos)
                .setTangent(Math.toRadians(0))
                //.splineToSplineHeading(new Pose2d(12, 64, Math.toRadians(92.5)), Math.toRadians(95))
                .splineToSplineHeading(new Pose2d(7, -33, Math.toRadians(-85)), Math.toRadians(-90))
                .lineToY(-66)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(launchPos, Math.toRadians(250))
                .build();

        Action flushPickup = drive.actionBuilder(launchPos)
                .setTangent(20)
                .splineToLinearHeading(new Pose2d(5, -61, Math.toRadians(-135)), launchPos.heading)
                .strafeToConstantHeading(new Vector2d(14, -67))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(launchPos, Math.toRadians(-165))
                .build();

        Action flushPickup2 = drive.actionBuilder(launchPos)
                .setTangent(20)
                .splineToLinearHeading(new Pose2d(5, -61, Math.toRadians(-135)), launchPos.heading)
                .strafeToConstantHeading(new Vector2d(14, -67))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(launchPos, Math.toRadians(-165))
                .build();

        Action endPark = drive.actionBuilder(launchPos)
                .strafeToLinearHeading(new Vector2d(-16, -34), Math.toRadians(-135))
                .build();

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(

                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        hardware.setHoodPos(0.2),
                        hardware.setYawAngle(PARAMS.yawangle[0]),
                        hardware.setOuttakeVelStart(1000),
                        hardware.blockOuttake(),

                        launch0,
                        new SleepAction(0.3),
                        hardware.unblockOuttake(),

                        hardware.setOuttakeVelStart(1150),

                        //hardware.launch(100, 0.6, 0.67, 300),
                        // h(100, 0.8f),
                        new SleepAction(0.2),
                        hardware.setHoodPos(0.5),
                        new SleepAction(0.2),
                        hardware.blockOuttake(),
                        //hardware.setOuttakeVelStart(1000),
                        //new SleepAction(0.2),
                        hardware.setYawAngle(PARAMS.yawangle[1]),

                        pickup2,
                        hardware.setOuttakeVelStart(950),
                        new ParallelAction(
                                hardware.launch(950, 0.15, 0.2, 300),
                                hardware.intakeStart(),
                                new SleepAction(0.3)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.2),

                        flushPickup,
                        hardware.setOuttakeVelStart(650),
                        hardware.setYawAngle(PARAMS.yawangle[2]),
                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        new ParallelAction(
                                hardware.launch(650, 0.1, 0.15, 300),
                                new SleepAction(0.25)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.1),
                        hardware.setYawAngle(PARAMS.yawangle[3]),

                        flushPickup2,
                        hardware.setOuttakeVelStart(650),
                        hardware.setYawAngle(PARAMS.yawangle[4]),
                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        new ParallelAction(
                                hardware.launch(650, 0.1, 0.15, 300),
                                new SleepAction(0.2)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.1),
                        hardware.setYawAngle(PARAMS.yawangle[5]),

                        pickup1,
                        hardware.setOuttakeVelStart(1250),
                        new ParallelAction(
                                hardware.launch(1250, 0.15, 0.2, 300),
                                hardware.intakeStart(),
                                new SleepAction(0.3)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.2),
                        endPark,
                        updatePose(),
                        hardware.sendDataToTele(drive.localizer.getPose().position, drive.localizer.getPose().heading, (byte)1)
                )
        );
    }
    public Action updatePose(){
        drive.updatePoseEstimate();
        telemetry.addData("pose", drive.localizer.getPose());
        telemetry.update();
        return new InstantAction(() -> drive.updatePoseEstimate());
    }
}
