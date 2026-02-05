package org.firstinspires.ftc.teamcode.StatesScripts;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class odoteleop {

    Hardware hardware;
    private Follower follower;
    double lastTheta;

    public enum odoDataTypes {
        X,
        Y,
        HEADING
    }

    // ===== 100 DEGREE SERVO TUNING =====

    private static final double TURRET_CENTER = 0.47; // adjust until forward is perfect
    private static final double SERVO_TOTAL_DEGREES = 100.0;
    private static final double TURRET_MAX_DEGREES = 45.0; // safe turret swing each side

    // ==================================

    public odoteleop(HardwareMap hardwareMap) {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(36, 84, Math.toRadians(135))); // remove later
        hardware = new Hardware(hardwareMap);
    }

    public String odoAimTurret(boolean autoAim, boolean isBlue, boolean aimLimelight) {
        if(autoAim){
            follower.update();

            int goalX = (isBlue) ? 0 : 144;
            int goalY = 144;

            Pose pose = follower.getPose();

            double angleOffset = pose.getHeading();
            if(!isBlue) {
                angleOffset -= 90;
            }


            // -------- HEADING MATH --------

            double targetAngle = Math.atan2(goalY - pose.getY(), goalX - pose.getX());

            double theta = targetAngle - angleOffset;

            theta = Math.toDegrees(theta);

            while (theta > 180) theta -= 360;
            while (theta < -180) theta += 360;

            // Limit to what turret is allowed to rotate
            theta = Math.max(-TURRET_MAX_DEGREES, Math.min(TURRET_MAX_DEGREES, theta));

            // ------------------------------

            // Convert turret angle to servo position
            double servoScale = TURRET_MAX_DEGREES / SERVO_TOTAL_DEGREES;

            double servoPos = TURRET_CENTER - (theta * servoScale / TURRET_MAX_DEGREES);

            servoPos = Math.max(0, Math.min(1, servoPos));

            hardware.launcherTurn.setPosition(servoPos);
            if(aimLimelight) {
                hardware.limelightTurn.setPosition(1-(hardware.launcherTurn.getPosition()/2));
            }

            return "heading: " + follower.getHeading()
                    + ", pose: " + follower.getPose();
        } else {
            hardware.launcherTurn.setPosition(0.5f);
            return "im lazy";
        }
    }

    public double getOdoData(odoDataTypes odt) {

        follower.update();

        switch (odt) {
            case X:
                return follower.getPose().getX();

            case Y:
                return follower.getPose().getY();

            case HEADING:
                return follower.getHeading();
        }

        return 0;
    }

    public void resetOdoPos(boolean isBlue){
        if(isBlue) {
            follower.setPose(new Pose(16, 122, Math.toRadians(144)));
        } else {
            follower.setPose(new Pose(144-16, 122, Math.toRadians(54)));
        }
    }
}
