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
        Pose2d launchPos = new Pose2d(61, 30, Math.toRadians(180));
        myBot.runAction(myBot.getDrive().actionBuilder(launchPos/*new Pose2d(-50, 50, Math.toRadians(125))*/)

                .setTangent(Math.toRadians(210))
                .splineToSplineHeading(new Pose2d(35, 63, Math.toRadians(90)), Math.toRadians(90))
                .waitSeconds(0.5)
                .setTangent(Math.toRadians(225))
                .splineToConstantHeading(launchPos.position, Math.toRadians(45))

                .setTangent(Math.toRadians(90))
                .lineToY(61)
                .lineToY(launchPos.position.y)
  //.splineToSplineHeading(new Pose2d(61, 61, Math.toRadians(90)), Math.toRadians(45))


                .build());


        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(1.0f)
                .addEntity(myBot)
                .start();
    }
}