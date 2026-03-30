package org.firstinspires.ftc.teamcode.WorldsScripts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.jetbrains.annotations.NotNull;

public class WorldsAutoHardware {
    public DcMotorEx outL, outR;
    public DcMotor inL, inR;
    public Servo blocker, outYaw, hood;

    public double launchSpeed = 1100;
    boolean isBlue;
    double currentYawAngle, hoodPos;

    public WorldsAutoHardware(HardwareMap hardwareMap) {
        outL = hardwareMap.get(DcMotorEx.class, "outl");
        outR = hardwareMap.get(DcMotorEx.class, "outr");
        inL = hardwareMap.get(DcMotor.class, "intakel");
        inR = hardwareMap.get(DcMotor.class, "intaker");

        outL.setDirection(DcMotorSimple.Direction.FORWARD);
        outR.setDirection(DcMotorSimple.Direction.REVERSE);
        inL.setDirection(DcMotorSimple.Direction.REVERSE);
        inR.setDirection(DcMotorSimple.Direction.FORWARD);


        blocker = hardwareMap.get(Servo.class, "blocker");
        outYaw = hardwareMap.get(Servo.class, "turn");
        hood = hardwareMap.get(Servo.class, "hood");

    }

    public class IntakeStart implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            inL.setPower(1.0);
            inR.setPower(1.0);
            return false;
        }
    }

    public class IntakeStop implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            inL.setPower(0.0);
            inR.setPower(0.0);
            return false;
        }
    }

    public Action intakeStart() {
        return new IntakeStart();
    }

    public Action intakeStop() {
        return new IntakeStop();
    }


    public class OuttakeStart implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            outL.setVelocity(launchSpeed);
            outR.setVelocity(launchSpeed);
            return false;
        }
    }

    public class Launch implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            outL.setVelocity(launchSpeed);
            outR.setVelocity(launchSpeed);
            hood.setPosition(hoodPos);
            if (Math.abs(outL.getVelocity() - launchSpeed) < 20) {
                return true;
            } else {
                blocker.setPosition(0);
                return false;
            }
        }
    }

    public class OuttakeStop implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            outL.setPower(0);
            outR.setPower(0);

            return false;
        }
    }

    public class BlockOuttake implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            blocker.setPosition(1);
            return false;
        }
    }
    public class UnblockOuttake implements  Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            blocker.setPosition(0);
            return false;
        }
    }
    public class SetYawAngle implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            //0.43 = -45deg, 0.55 = 0deg 0.67 = 45deg
            outYaw.setPosition(0.55 + 0.12*currentYawAngle/45);
            return false;
        }
    }

    /*public class odoAimTurret implements Action {
        @Override
        public boolean run(@NotNull TelemetryPacket packet) {
            //get pose
                drive.localizer.update();
                double currentX = -drive.localizer.getPose().position.y;
                double currentY = drive.localizer.getPose().position.x;
                double currentHeading =  drive.localizer.getPose().heading.toDouble();
                final int goalY = 65;
                final int goalX = -65;

            //set turret rot
                double theta = Math.toDegrees(Math.atan2(goalY-currentY, goalX-currentX) - currentHeading);
                //0.31 = 0deg, 0.43 = 45 deg, 0.55 = 90deg
                double servoPos = (0.12f*theta/45) + 0.31f;
                //set launcher pos
                outYaw.setPosition(servoPos);
                return true;
        }
    }*/

    public Action outtakeStart(double speed) {
        launchSpeed = speed;
        return new OuttakeStart();
    }

    public Action launch(double speed, double newHoodPos) {
        launchSpeed = speed;
        hoodPos = newHoodPos;
        return new Launch();
    }

    public Action outtakeStop() {
        return new OuttakeStop();
    }

    public Action blockOuttake() {
        return new BlockOuttake();
    }
    public Action unblockOuttake() {
        return new UnblockOuttake();
    }

    public Action setYawAngle(double angle) {
        currentYawAngle = angle;
        return new SetYawAngle();
    }

    //made cause the start wasn't settin to the correct velocity and if it aint broke and stuff
    public Action setOuttakeVelStart(double speed) {
        return new InstantAction(() -> SetVel(speed));
        //return new VArmDump();
    }

    public Action setHoodPos(double position) {
        return new InstantAction(() -> hood.setPosition(position));
    }

    void SetVel(double speed) {
        outL.setVelocity(speed);
        outR.setVelocity(speed);
    }

    /*public Action odoAimTurret(boolean onBlue) {
         isBlue = onBlue;
        return new odoAimTurret();
    }*/

}
