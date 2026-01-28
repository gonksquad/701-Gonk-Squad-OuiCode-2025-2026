package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    // TODO: Set the proper mass
    public static FollowerConstants followerConstants = new FollowerConstants()
            .forwardZeroPowerAcceleration(-39)
            .lateralZeroPowerAcceleration(-72)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.06, 0, 0.0005, 0.025))
            .headingPIDFCoefficients(new PIDFCoefficients(0.7, 0, 0.0005, 0.025))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.03,0,0.001,0.6,0.025))
            .centripetalScaling(0.0005)
            .mass(13.6); //should be right for battlebot

    public static PathConstraints pathConstraints = new PathConstraints(0.99,
            100,
            1.5,
            1);

    // TODO: Set the proper directions and update motor names if needed
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(.5)
            .xVelocity(42.284)
            .yVelocity(28.7)
            .leftFrontMotorName("fl")
            .rightFrontMotorName("fr")
            .leftRearMotorName("bl")
            .rightRearMotorName("br")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-5.125) //these are for wooden robot 2
            .strafePodX(-6.4375)
            .distanceUnit(DistanceUnit.MM)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD) //why does reversing this do nothing?
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);
}
