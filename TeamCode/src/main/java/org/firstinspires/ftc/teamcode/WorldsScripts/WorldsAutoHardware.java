package org.firstinspires.ftc.teamcode.WorldsScripts;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.jetbrains.annotations.NotNull;

public class WorldsAutoHardware {
    public DcMotorEx outL, outR;
    public DcMotor inL, inR;
    public Servo blocker, outYaw;

    public double launchSpeed;

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
            outR.setPower(launchSpeed);

            if (outL.getVelocity() < launchSpeed - 10.0 || outL.getVelocity() > launchSpeed + 10.0) {
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

    public Action outtakeStart(double speed) {
        launchSpeed = speed;
        return new OuttakeStart();
    }

    public Action launch(double speed) {
        launchSpeed = speed;
        return new Launch();
    }

    public Action outtakeStop() {
        return new OuttakeStop();
    }

    public Action blockOuttake() {
        return new BlockOuttake();
    }
}
