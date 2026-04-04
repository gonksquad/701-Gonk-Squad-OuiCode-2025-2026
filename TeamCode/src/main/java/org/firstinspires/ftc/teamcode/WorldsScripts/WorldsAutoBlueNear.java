package org.firstinspires.ftc.teamcode.WorldsScripts;

//import com.acmerobotics.dashboard.config.Config;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous
public class WorldsAutoBlueNear extends LinearOpMode {


    MecanumDrive drive;
    WorldsAutoHardware hardware;
    Pose2d initialPose;

    Pose2d launchPos = new Pose2d(-28, 22, Math.toRadians(90));

    @Override
    public void runOpMode() {
        initialPose = new Pose2d(-50, 50, Math.toRadians(135));
        drive = new MecanumDrive(hardwareMap, initialPose);
        hardware = new WorldsAutoHardware(hardwareMap);

        Action launch0 = drive.actionBuilderBlue(initialPose)
                .strafeToLinearHeading(new Vector2d(-19, 19), Math.toRadians(90))
                .build();


        Action pickup1 = drive.actionBuilderBlue(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(-16, 54, Math.toRadians(90)), launchPos.heading)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(launchPos, Math.toRadians(90))
                .build();

        Action pickup2 = drive.actionBuilderBlue(launchPos)
                .setTangent(Math.toRadians(0))
                //.splineToSplineHeading(new Pose2d(12, 64, Math.toRadians(92.5)), Math.toRadians(95))
                .splineToSplineHeading(new Pose2d(12, 64, Math.toRadians(85)), Math.toRadians(95))
                .setTangent(Math.toRadians(315))
                .splineToSplineHeading(launchPos, Math.toRadians(180))
                .build();

        Action flushPickup = drive.actionBuilderBlue(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(6, 64, Math.toRadians(135)), launchPos.heading)
                .strafeToConstantHeading(new Vector2d(14, 66))
                .waitSeconds(1)
                .setTangent(Math.toRadians(325))
                .splineToLinearHeading(launchPos, Math.toRadians(165))
                .build();

        Action endPark = drive.actionBuilderBlue(launchPos)
                .strafeToLinearHeading(new Vector2d(-16, 34), Math.toRadians(135))
                .build();


        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        hardware.setHoodPos(0.1),
                        hardware.setYawAngle(41),
                        hardware.setOuttakeVelStart(700),
                        hardware.blockOuttake(),

                        launch0,
                        //hardware.launch(1100, 0.1),
                        // h(100, 0.8f),
                        new SleepAction(1),
                        hardware.blockOuttake(),
                        hardware.setYawAngle(39),

                        pickup2,
                        new ParallelAction(
                                //hardware.launch(1000, 0.1),
                                hardware.intakeStart(),
                                new SleepAction(1.5)
                        ),
                        hardware.blockOuttake(),

                        flushPickup,
                        hardware.setYawAngle(39),
                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        new ParallelAction(
                                //hardware.launch(1150, 0.1),
                                new SleepAction(1.5)
                        ),
                        hardware.setYawAngle(39),

                        hardware.blockOuttake(),

                        pickup1,
                        new ParallelAction(
                               // hardware.launch(1100, 0.1),
                                hardware.intakeStart(),
                                new SleepAction(1.5)
                        ),
                        hardware.blockOuttake(),
                        endPark
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
