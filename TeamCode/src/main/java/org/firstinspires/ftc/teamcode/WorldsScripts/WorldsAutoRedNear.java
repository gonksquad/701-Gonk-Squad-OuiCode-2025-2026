package org.firstinspires.ftc.teamcode.WorldsScripts;

//import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
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
public class WorldsAutoRedNear extends LinearOpMode {
    public static class Params {
        public double hoodpos = 0.25;
        public double xpos = 5;
        public double ypos =  60;
        public double heading = 135;
        public double xpos2;
        public double ypos2;
        public double heading2;

    }
    public static WorldsAutoRedNear.Params PARAMS = new WorldsAutoRedNear.Params();

    MecanumDrive drive;
    WorldsAutoHardware hardware;
    Pose2d initialPose;

    Pose2d launchPos = new Pose2d(-28, 22, Math.toRadians(135));

    @Override
    public void runOpMode() {
        initialPose = new Pose2d(-50, 50, Math.toRadians(135));
        drive = new MecanumDrive(hardwareMap, initialPose);
        hardware = new WorldsAutoHardware(hardwareMap);

        Action launch0 = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(135))
                .build();


        Action pickup1 = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(-15, 27, Math.toRadians(90)), launchPos.heading)
                .setTangent(Math.toRadians(270))
                .lineToY(48)
                .splineToLinearHeading(launchPos, Math.toRadians(90))
                .build();

        Action pickup2 = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(10, 30, Math.toRadians(90)), launchPos.heading)
                .setTangent(Math.toRadians(270))
                .lineToY(54)
                .lineToY(30)
                .splineToLinearHeading(launchPos, Math.toRadians(90))
                .build();

        Action pickup3 = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(12, 30, Math.toRadians(135)), launchPos.heading)
                .setTangent(Math.toRadians(270))
                .lineToY(54)
                .lineToY(30)
                .splineToLinearHeading(launchPos, Math.toRadians(90))
                .build();

        Action flushPickup = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(PARAMS.xpos, PARAMS.ypos, Math.toRadians(135)), launchPos.heading)
                .waitSeconds(2)
                .setTangent(PARAMS.heading2)
                .splineToSplineHeading(new Pose2d(PARAMS.xpos2, PARAMS.ypos2, Math.toRadians(90)), Math.toRadians(135))
                .setTangent(0)
                //.lineToY(48)
                //.setTangent(Math.toRadians(270))
                .splineToLinearHeading(launchPos, Math.toRadians(135))
                .build();


        waitForStart();

        Actions.runBlocking(
                    new SequentialAction(
                            hardware.blockOuttake(),
                                hardware.intakeStart(),
                                hardware.setYawAngle(0),
                                hardware.setOuttakeVelStart(),
                                hardware.blockOuttake(),

                            launch0,
                            hardware.launch(1100, PARAMS.hoodpos),
                            // h(100, 0.8f),
                            new SleepAction(1.5),
                            hardware.blockOuttake(),

                            pickup2,
                            new ParallelAction(
                                    hardware.launch(1100, PARAMS.hoodpos),
                                    hardware.intakeStart(),
                                    new SleepAction(1.5)
                            ),
                            hardware.blockOuttake(),

                            new ParallelAction(
                                flushPickup,
                                hardware.intakeStart(),
                                new SequentialAction(
                                    new SleepAction(1.5),
                                    new ParallelAction(
                                        hardware.launch(1100, PARAMS.hoodpos),
                                        new SleepAction(1.5)
                                    )
                                )
                            ),
                            hardware.blockOuttake(),

                            pickup1,
                            new ParallelAction(
                                    hardware.launch(1100, PARAMS.hoodpos),
                                    hardware.intakeStart(),
                                    new SleepAction(1.5)
                            ),
                            hardware.blockOuttake()
                            /*pickup3,
                            hardware.intakeStop(),
                            hardware.launch(750, 0.55f),
                            launchWait,
                            hardware.outtakeStop(),
                            hardware.intakeStop(),
                            hardware.blockOuttake()*/
                    )
            );
    }
}
