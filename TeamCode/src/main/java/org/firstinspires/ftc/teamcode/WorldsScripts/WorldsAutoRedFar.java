package org.firstinspires.ftc.teamcode.WorldsScripts;

//import com.acmerobotics.dashboard.config.Config;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous
public class WorldsAutoRedFar extends LinearOpMode {
    MecanumDrive drive;
    WorldsAutoHardware hardware;
    Pose2d launchPos = new Pose2d(61, 15, Math.toRadians(180));
    Pose2d launchPos2 = new Pose2d(55, 10, Math.toRadians(135));

    @Override
    public void runOpMode() {
        drive = new MecanumDrive(hardwareMap, launchPos);
        hardware = new WorldsAutoHardware(hardwareMap);
        Action pickup3 = drive.actionBuilder(launchPos)
                .setTangent(Math.toRadians(210))
                .splineToSplineHeading(new Pose2d(36, 43, Math.toRadians(90)), Math.toRadians(90))
                .lineToY(63)
                .setReversed(true)
                .splineToLinearHeading(launchPos2, Math.toRadians(45))
                .build();


        Action flushPickup = drive.actionBuilder(launchPos2)
                .setReversed(false)
                .setTangent(Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(launchPos.position.x-2,56), Math.toRadians(75))
                .waitSeconds(0.2)
                .lineToY(52)
                .lineToY(58)
                .strafeToLinearHeading(launchPos2.position, launchPos2.heading)
                .build();

        Action endPark = drive.actionBuilder(launchPos2)
                .strafeToLinearHeading(new Vector2d(41, 34), Math.toRadians(180))
                .build();


        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                            hardware.blockOuttake(),
                            hardware.setYawAngle(-20),
                            hardware.setOuttakeVelStart(400),
                            hardware.intakeStart()
                        ),
                        hardware.blockOuttake(),
                        //Turn turret, shoot 3, turn on intake, block turret
                        hardware.setHoodPos(0.3),
                        hardware.launch(100, 0.65, 0.45, 800),
                        new SleepAction(1.5),
                        hardware.blockOuttake(),
                        new ParallelAction(
                            hardware.setYawAngle(15),
                            pickup3
                        ),
                        hardware.setHoodPos(0.4),
                        hardware.launch(1750, 0.3, 0.1, 400),
                        new SleepAction(1.5),
                        hardware.blockOuttake(),
                        //turn turret, shoot 3, block turret
                        hardware.setYawAngle(21),
                        flushPickup,
                        hardware.setHoodPos(0.4),
                        hardware.launch(1650, 0.4, 0.25, 500),
                        new SleepAction(1.5),
                        //shoot 3, block turret
                        endPark

                )
        );
    }
}
