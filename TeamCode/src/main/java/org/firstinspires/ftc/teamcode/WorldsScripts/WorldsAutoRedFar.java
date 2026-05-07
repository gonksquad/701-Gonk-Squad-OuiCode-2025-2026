package org.firstinspires.ftc.teamcode.WorldsScripts;

//import com.acmerobotics.dashboard.config.Config;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
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
public class WorldsAutoRedFar extends LinearOpMode {
    MecanumDrive drive;
    WorldsAutoHardware hardware;
    Pose2d launchPos = new Pose2d(58, 15, Math.toRadians(180));
    Pose2d launchPos2 = new Pose2d(48, 10, Math.toRadians(135));

    @Override
    public void runOpMode() {
        drive = new MecanumDrive(hardwareMap, launchPos);
        hardware = new WorldsAutoHardware(hardwareMap);
        Action pickup3 = drive.actionBuilder(launchPos)
                .setTangent(Math.toRadians(235))
                .splineToSplineHeading(new Pose2d(40, 43, Math.toRadians(90)), Math.toRadians(90))
                .lineToY(63)
                .setReversed(true)
                .splineToLinearHeading(launchPos2, Math.toRadians(-45))
                .build();


        Action flushPickup = drive.actionBuilder(launchPos2)
                .setReversed(false)
                .setTangent(Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(launchPos.position.x-3,56), Math.toRadians(70))
                .waitSeconds(0.2)
                .lineToY(50)
                .lineToY(54)
                .strafeToLinearHeading(launchPos2.position, launchPos2.heading)
                .build();

        Action flushPickup2 = drive.actionBuilder(launchPos2)
                .setReversed(false)
                .setTangent(Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(launchPos.position.x-3,56), Math.toRadians(115))
                .strafeToLinearHeading(launchPos2.position, launchPos2.heading)
                .build();

        Action flushPickup3 = drive.actionBuilder(launchPos2)
                .setReversed(false)
                .setTangent(Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(launchPos.position.x-3,56), Math.toRadians(115))
                .strafeToLinearHeading(launchPos2.position, launchPos2.heading)
                .build();

        Action flushPickup4 = drive.actionBuilder(launchPos2)
                .setReversed(false)
                .setTangent(Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(launchPos.position.x-3,56), Math.toRadians(115))
                .build();

        Action endPark = drive.actionBuilder(launchPos2)
                .strafeToLinearHeading(new Vector2d(41, 34), Math.toRadians(180))
                .build();


        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        updatePose(),
                        hardware.sendDataToTele(drive.localizer.getPose().position, drive.localizer.getPose().heading, (byte)1),
                        new ParallelAction(
                                hardware.setHoodPos(0.1),
                                hardware.blockOuttake(),
                                hardware.setYawAngle(-22),
                                hardware.setOuttakeVelStart(1400),
                                hardware.intakeStart()
                        ),
                        new SleepAction(1),
                        hardware.unblockOuttake(),
                        hardware.setOuttakeVelStart(1400),
                        //Turn turret, shoot 3, turn on intake, block turret
                        hardware.setHoodPos(0.1),
                        new SleepAction(1.5),
                        hardware.blockOuttake(),
                        new ParallelAction(
                                hardware.setYawAngle(27),
                                pickup3
                        ),
                        hardware.setHoodPos(0.1),
                        hardware.launch(1450, 0.1, 0.1, 1),
                        new SleepAction(1.5),
                        hardware.blockOuttake(),
                        //turn turret, shoot 3, block turret
                        hardware.setYawAngle(27),
                        flushPickup,
                        hardware.setHoodPos(0.1),
                        hardware.launch(1450, 0.1, 0.1, 1),
                        new SleepAction(1.5),

                        //flush pickups

                        hardware.blockOuttake(),
                        //turn turret, shoot 3, block turret
                        hardware.setYawAngle(27),
                        flushPickup2,
                        hardware.setHoodPos(0.1),
                        hardware.launch(1450, 0.1, 0.1, 1),
                        new SleepAction(1.5),
                        //shoot 3, block turret

                        hardware.blockOuttake(),
                        //turn turret, shoot 3, block turret
                        hardware.setYawAngle(27),
                        flushPickup3,
                        hardware.setHoodPos(0.1),
                        hardware.launch(1450, 0.1, 0.1, 1),
                        new SleepAction(1.5),

                        hardware.blockOuttake(),
                        //turn turret, shoot 3, block turret
                        hardware.setYawAngle(27),
                        flushPickup4,
                        hardware.setHoodPos(0.1),
                        hardware.launch(1450, 0.1, 0.1, 1),
                        new SleepAction(1.5),
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
