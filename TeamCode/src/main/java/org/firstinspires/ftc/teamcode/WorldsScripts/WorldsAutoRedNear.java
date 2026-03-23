package org.firstinspires.ftc.teamcode.WorldsScripts;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous
public class WorldsAutoRedNear extends LinearOpMode {
    MecanumDrive drive;
    WorldsAutoHardware hardware;
    Pose2d initialPose;

    @Override
    public void runOpMode() {
        initialPose = new Pose2d(-50, 50, Math.toRadians(125));
        drive = new MecanumDrive(hardwareMap, initialPose);
        hardware = new WorldsAutoHardware(hardwareMap);

        Action launch0 = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-24, 24), Math.toRadians(135))
                .build();

        Action launchWait = drive.actionBuilder(new Pose2d(-24, 24, Math.toRadians(135)))
                .waitSeconds(1.0)
                .build();

        Action pickup1 = drive.actionBuilder(new Pose2d(-24, 24, Math.toRadians(135)))
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(-12, 24, Math.toRadians(90)), Math.toRadians(90))
                .lineToY(48)
                .setTangent(Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(-24, 24, Math.toRadians(135)), Math.toRadians(180))
                .build();

        Action pickup2 = drive.actionBuilder(new Pose2d(-24, 24, Math.toRadians(135)))
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(12, 24, Math.toRadians(90)), Math.toRadians(90))
                .lineToY(48)
                .setTangent(Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(-24, 24, Math.toRadians(135)), Math.toRadians(180))
                .build();

        Action pickup3 = drive.actionBuilder(new Pose2d(-24, 24, Math.toRadians(135)))
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(36, 24, Math.toRadians(90)), Math.toRadians(90))
                .lineToY(48)
                .setTangent(Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(-24, 24, Math.toRadians(135)), Math.toRadians(180))
                .build();


        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        hardware.outtakeStart(1200),
                        launch0,
                        hardware.launch(1200),
                        launchWait,
                        hardware.intakeStart(),
                        hardware.blockOuttake(),
                        pickup1,
                        hardware.intakeStop(),
                        hardware.launch(1200),
                        launchWait,
                        hardware.intakeStart(),
                        hardware.blockOuttake(),
                        pickup2,
                        hardware.intakeStop(),
                        hardware.launch(1200),
                        launchWait,
                        hardware.intakeStart(),
                        hardware.blockOuttake(),
                        pickup3,
                        hardware.intakeStop(),
                        hardware.launch(1200),
                        launchWait,
                        hardware.outtakeStop(),
                        hardware.intakeStop(),
                        hardware.blockOuttake()
                )
        );
    }
}
