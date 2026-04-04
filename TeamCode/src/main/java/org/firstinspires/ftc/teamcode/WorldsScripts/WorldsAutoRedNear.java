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
public class WorldsAutoRedNear extends LinearOpMode {
    public static class Params {



    }
    public static WorldsAutoRedNear.Params PARAMS = new WorldsAutoRedNear.Params();

    MecanumDrive drive;
    WorldsAutoHardware hardware;
    Pose2d initialPose;

    Pose2d launchPos = new Pose2d(-28, 22, Math.toRadians(90));

    @Override
    public void runOpMode() {
        initialPose = new Pose2d(-50, 50, Math.toRadians(135));
        drive = new MecanumDrive(hardwareMap, initialPose);
        hardware = new WorldsAutoHardware(hardwareMap);

        Action launch0 = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-19, 19), Math.toRadians(90))
                .build();

        Action pickup1 = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(-16, 54, Math.toRadians(90)), launchPos.heading)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(launchPos, Math.toRadians(90))
                .build();

        Action pickup2 = drive.actionBuilder(launchPos)
                .setTangent(Math.toRadians(0))
                //.splineToSplineHeading(new Pose2d(12, 64, Math.toRadians(92.5)), Math.toRadians(95))
                .splineToSplineHeading(new Pose2d(10, 60, Math.toRadians(85)), Math.toRadians(95))
                .setTangent(Math.toRadians(315))
                .splineToSplineHeading(launchPos, Math.toRadians(180))
                .build();

        Action flushPickup = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(6, 63, Math.toRadians(135)), launchPos.heading)
                .strafeToConstantHeading(new Vector2d(14, 66))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(315))
                .splineToLinearHeading(launchPos, Math.toRadians(165))
                .build();

        Action flushPickup2 = drive.actionBuilder(launchPos)
                .setTangent(0)
                .splineToLinearHeading(new Pose2d(6, 63, Math.toRadians(135)), launchPos.heading)
                .strafeToConstantHeading(new Vector2d(14, 66))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(315))
                .splineToLinearHeading(launchPos, Math.toRadians(165))
                .build();

        Action endPark = drive.actionBuilder(launchPos)
                .strafeToLinearHeading(new Vector2d(-16, 34), Math.toRadians(135))
                .build();

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        hardware.setHoodPos(0.1),
                        hardware.setYawAngle(41),
                        hardware.setOuttakeVelStart(950),
                        hardware.blockOuttake(),

                        launch0,
                        new SleepAction(0.3),
                        hardware.unblockOuttake(),
                        hardware.setOuttakeVelStart(1350),


                        //hardware.launch(100, 0.6, 0.67, 300),
                        // h(100, 0.8f),
                        new SleepAction(0.2),
                        hardware.setHoodPos(0.2),
                        new SleepAction(0.2),
                        hardware.blockOuttake(),
                        new SleepAction(0.2),
                        hardware.setYawAngle(39),

                        pickup2,
                        new ParallelAction(
                                hardware.launch(1100, 0.1, 0.15, 300),
                                hardware.intakeStart(),
                                new SleepAction(0.3)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.2),

                        flushPickup,
                        hardware.setYawAngle(35),
                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        new ParallelAction(
                                hardware.launch(1050, 0.1, 0.15, 300),
                                new SleepAction(0.25)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.1),
                        hardware.setYawAngle(40),

                        flushPickup2,
                        hardware.setYawAngle(35),
                        hardware.blockOuttake(),
                        hardware.intakeStart(),
                        new ParallelAction(
                                hardware.launch(1050, 0.1, 0.15, 300),
                                new SleepAction(0.2)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.1),
                        hardware.setYawAngle(38),

                        pickup1,
                        new ParallelAction(
                                hardware.launch(1250, 0.1, 0.15, 300),
                                hardware.intakeStart(),
                                new SleepAction(0.3)
                        ),
                        hardware.blockOuttake(),
                        new SleepAction(0.2),
                        endPark,
                        updatePose(),
                        hardware.sendDataToTele(drive.localizer.getPose().position, drive.localizer.getPose().heading, (byte)1)
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
    public Action updatePose(){
        drive.updatePoseEstimate();
        telemetry.addData("pose", drive.localizer.getPose());
        telemetry.update();
        return new InstantAction(() -> drive.updatePoseEstimate());
    }
}
