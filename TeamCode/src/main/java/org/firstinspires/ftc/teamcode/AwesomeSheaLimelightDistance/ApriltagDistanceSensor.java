//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.Limelight3A;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
//import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
//
//@TeleOp
//public class ApriltagDistanceSensor extends OpMode {
//    private Limelight3A limeLight3A;
//    private double distance;
//
//    @Override
//    public void init() {
//        limeLight3A = hardwareMap.get(Limelight3A.class, "limelight");
//        limeLight3A.pipelineSwitch(1); // AprilTag 0 pipeline
//    }
//
//    @Override
//    public void start() {
//        limeLight3A.start();
//    }
//
//    @Override
//    public void loop() {
//        // Get yaw from Control Hub IMU
//        YawPitchRollAngles orientation = bench.getOrientation();
//        limeLight3A.updateRobotOrientation(orientation.getYaw(AngleUnit.DEGREES));
//
//        // Get latest Limelight result, pipeline 8 for AprilTag 0
//        LLResult llResult = limeLight3A.getLatestResult();
//        if (llResult != null && llResult.isValid()) {
//            Pose3D botpose = llResult.getBotpose_MT2();
//            telemetry.addData("Calculated Distance", distance);
//            telemetry.addData("Target X", llResult.getTx());
//            telemetry.addData("Target Area", llResult.getTa());
//            telemetry.addData("Botpose", botpose.toString());
//        } else {
//            telemetry.addData("AprilTag", "Not Found");
//        }
//        telemetry.update();
//    }
//
//}
