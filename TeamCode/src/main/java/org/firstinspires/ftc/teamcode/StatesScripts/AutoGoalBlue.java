
package org.firstinspires.ftc.teamcode.StatesScripts;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.QualifierScripts.RRHardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Goal Blue")
@Configurable // Panels
public class AutoGoalBlue extends LinearOpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class
    private ElapsedTime pathTimer;
    private Hardware hardware;
    private byte sorterPos;


    @Override
    public void runOpMode() throws InterruptedException {
        hardware = new Hardware(hardwareMap);

        sorterPos = 2;

        pathTimer = new ElapsedTime();

        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(16, 122, Math.toRadians(144)));

        paths = new Paths(follower); // Build paths

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);


        waitForStart();


        hardware.launcherTurn.setPosition(0.5);


        while (opModeIsActive()) {
            follower.update(); // Update Pedro Pathing
            autonomousPathUpdate(); // Update autonomous state machine

            // Log values to Panels and Driver Station
            panelsTelemetry.debug("Path State", pathState);
            panelsTelemetry.debug("X", follower.getPose().getX());
            panelsTelemetry.debug("Y", follower.getPose().getY());
            panelsTelemetry.debug("Heading", follower.getPose().getHeading());
            panelsTelemetry.update(telemetry);
        }
    }



    public static class Paths {
        public PathChain Shoot0;
        public PathChain Align1;
        public PathChain Intake11;
        public PathChain Intake12;
        public PathChain Intake13;
        public PathChain Shoot1;
        public PathChain Align2;
        public PathChain Intake21;
        public PathChain Intake22;
        public PathChain Intake23;
        public PathChain Shoot2;
        public PathChain Align3;
        public PathChain Intake31;
        public PathChain Intake32;
        public PathChain Intake33;
        public PathChain Shoot3;
        public PathChain Exit;

        public Paths(Follower follower) {
            Shoot0 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(20.000, 122.000),

                                    new Pose(60.000, 84.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(144), Math.toRadians(135))

                    .build();

            Align1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(60.000, 84.000),

                                    new Pose(48.000, 84.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake11 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 84.000),

                                    new Pose(36.000, 84.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake12 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(36.000, 84.000),

                                    new Pose(31.000, 84.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake13 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(31.000, 84.000),

                                    new Pose(20.000, 84.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Shoot1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(20.000, 84.000),

                                    new Pose(60.000, 84.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))

                    .build();

            Align2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(60.000, 84.000),
                                    new Pose(60.000, 60.000),
                                    new Pose(48.000, 60.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))

                    .build();

            Intake21 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 60.000),

                                    new Pose(36.000, 60.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake22 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(36.000, 60.000),

                                    new Pose(31.000, 60.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake23 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(31.000, 60.000),

                                    new Pose(20.000, 60.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Shoot2 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(20.000, 60.000),
                                    new Pose(60.000, 60.000),
                                    new Pose(60.000, 84.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))

                    .build();

            Align3 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(60.000, 84.000),
                                    new Pose(60.000, 36.000),
                                    new Pose(48.000, 36.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))

                    .build();

            Intake31 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 36.000),

                                    new Pose(36.000, 36.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake32 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(36.000, 36.000),

                                    new Pose(31.000, 36.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Intake33 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(31.000, 36.000),

                                    new Pose(20.000, 36.000)
                            )
                    ).setTangentHeadingInterpolation()

                    .build();

            Shoot3 = follower.pathBuilder().addPath(
                            new BezierCurve(
                                    new Pose(20.000, 36.000),
                                    new Pose(60.000, 36.000),
                                    new Pose(60.000, 84.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))

                    .build();

            Exit = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(60.000, 84.000),

                                    new Pose(36.000, 84.000)
                            )
                    ).setConstantHeadingInterpolation(Math.toRadians(135))

                    .build();
        }
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(paths.Shoot0);
                hardware.launcherLeft.setVelocity(1170);
                hardware.launcherRight.setVelocity(1170);
                hardware.sorter.setPosition(hardware.outtakePos[2]);
                setPathState(1);
                break;
            case 1:
                launchAndSetState(2);
                break;
            case 2:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Align1,true);
                    hardware.intake.setPower(1);
                    setPathState(3);
                }
                break;
            case 3:
                if(!follower.isBusy()) {
                    hardware.sorter.setPosition(hardware.intakePos[0]);
                    sleep(1000);
                    follower.followPath(paths.Intake11,true);
                    setPathState(4);
                }
                break;
            case 4:
                if(!follower.isBusy() && pathTimer.milliseconds() > 1000) {
                    hardware.sorter.setPosition(hardware.intakePos[1]);
                    sleep(1000);
                    follower.followPath(paths.Intake12,true);
                    setPathState(5);
                }
                break;
            case 5:
                if(!follower.isBusy() && pathTimer.milliseconds() > 1000) {
                    hardware.sorter.setPosition(hardware.intakePos[2]);
                    sleep(1000);
                    follower.followPath(paths.Intake13,true);
                    setPathState(6);
                }
                break;
            case 6:
                if(!follower.isBusy() && pathTimer.milliseconds() > 1000) {
                    follower.followPath(paths.Shoot1,true);
                    setPathState(7);
                }
                break;
            case 7:
                launchAndSetState(-1);
                break;
            case 8:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Align2,true);
                    setPathState(9);
                }
                break;
            case 9:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Intake21,true);
                    setPathState(10);
                }
                break;
            case 10:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Intake22,true);
                    setPathState(11);
                }
                break;
            case 11:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Intake23,true);
                    setPathState(12);
                }
                break;
            case 12:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Shoot2,true);
                    setPathState(13);
                }
                break;
            case 14:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Align3,true);
                    setPathState(15);
                }
                break;
            case 15:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Intake31,true);
                    setPathState(16);
                }
                break;
            case 16:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Intake32,true);
                    setPathState(17);
                }
                break;
            case 17:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Intake33,true);
                    setPathState(18);
                }
                break;
            case 18:
                if(!follower.isBusy()) {
                    follower.followPath(paths.Shoot3,true);
                    setPathState(19);
                }
                break;
            case 19:
                if (!follower.isBusy()) {
                    follower.followPath(paths.Exit,true);
                    setPathState(-1);
                }
                break;
        }
    }

    public void launchAndSetState(int state) {
        if (follower.isBusy() || hardware.launcherLeft.getVelocity() < 1150) return;
        hardware.intake.setPower(0.5);
        hardware.sorter.setPosition(hardware.outtakePos[sorterPos]);
        sleep(1000);
        hardware.intake.setPower(0);
        if (sorterPos > 0) {
            hardware.outtakeTransferLeft.setPosition(hardware.liftPos[1]);
            hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[1]);
            sleep(1000);
            hardware.outtakeTransferLeft.setPosition(hardware.liftPos[0]);
            hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[0]);
            sleep(400);
            sorterPos--;
        } else {
            hardware.outtakeTransferLeft.setPosition(hardware.liftPos[1]);
            hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[1]);
            sleep(1000);
            hardware.outtakeTransferLeft.setPosition(hardware.liftPos[0]);
            hardware.outtakeTransferRight.setPosition(1 - hardware.liftPos[0]);
            sorterPos = 2;
            sleep(400);
            setPathState(state);
        }
    }

    /** These change the states of the paths and actions. It will also reset the timers of the individual switches **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.reset();
    }
}