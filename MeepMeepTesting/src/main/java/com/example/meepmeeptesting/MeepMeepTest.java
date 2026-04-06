package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTest {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 10)
                .build();
        Pose2d launchPos = new Pose2d(-28, -22, Math.toRadians(-90));
        Pose2d initialPose = new Pose2d(-50, -50, Math.toRadians(-135));;
        myBot.runAction(myBot.getDrive().actionBuilder(launchPos/*new Pose2d(-50, 50, Math.toRadians(125))*/)
                .setTangent(Math.toRadians(0))
                //.splineToSplineHeading(new Pose2d(12, 64, Math.toRadians(92.5)), Math.toRadians(95))
                .splineToSplineHeading(new Pose2d(4, -33, Math.toRadians(-85)), Math.toRadians(-90))
                .lineToY(-66)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(launchPos, Math.toRadians(250))
                .build());

                /*.strafeToLinearHeading(new Vector2d(-19, -19), Math.toRadians(-90))

                .setTangent(Math.toRadians(0))
                //.splineToSplineHeading(new Pose2d(12, 64, Math.toRadians(92.5)), Math.toRadians(95))
                .splineToSplineHeading(new Pose2d(10, -60, Math.toRadians(-85)), Math.toRadians(-95))
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(launchPos, Math.toRadians(250))

                .setTangent(0)
                .splineToLinearHeading(new Pose2d(6, -63, Math.toRadians(-135)), launchPos.heading)
                .strafeToConstantHeading(new Vector2d(14, -66))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(launchPos, Math.toRadians(-165))

                .setTangent(0)
                .splineToLinearHeading(new Pose2d(6, -63, Math.toRadians(-135)), launchPos.heading)
                .strafeToConstantHeading(new Vector2d(14, -66))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(90))
                .splineToLinearHeading(launchPos, Math.toRadians(-165))


                .setTangent(0)
                .splineToLinearHeading(new Pose2d(-16, -54, Math.toRadians(-90)), launchPos.heading)
                .setTangent(Math.toRadians(180))
                .splineToLinearHeading(launchPos, Math.toRadians(-90))


                .strafeToLinearHeading(new Vector2d(-16, -34), Math.toRadians(-135))*/

                //.build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(1.0f)
                .addEntity(myBot)
                .start();
    }
}