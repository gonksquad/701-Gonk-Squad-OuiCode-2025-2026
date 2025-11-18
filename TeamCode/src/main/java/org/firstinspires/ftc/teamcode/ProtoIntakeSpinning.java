package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
//@TeleOp
public class ProtoIntakeSpinning extends LinearOpMode {

    private DcMotor intake;

    @Override
    public void runOpMode() {

        intake = hardwareMap.get(DcMotor.class, "intake");

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.a) {
                intake.setPower(1);
            } else if (gamepad1.y) {
                intake.setPower(-1);
            } else if (gamepad1.b) {
                intake.setPower(0);
            }
        }
    }
}
