package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class backendAutoRed extends OpMode {
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;

    //poses initialized
    private final Pose startPose = new Pose(48, 9, Math.toRadians(90));
    private final Pose firstSpikeStart = new Pose(48,36, Math.toRadians(180));
    private final Pose firstSpikeEnd = new Pose(24,36, Math.toRadians(180));

    //path initializing
}
