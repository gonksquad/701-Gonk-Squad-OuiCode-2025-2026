package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Hardware {
    // declare hardware
    public DcMotor frontLeft, frontRight, backLeft, backRight, intake;
    public DcMotorEx launcherLeft, launcherRight;
    public CRServo launcherTurn, limelightTurn;
    public Servo sorter, outtakeTransferLeft, outtakeTransferRight;
    public Limelight3A limelight;
    public ColorSensor colorSensor;
    int red, green, blue;
    float[] hsvValues = new float[3];
    boolean nextPos = true;
    public final double[] intakePos = {1.0, 0.6, 0.2};//*/{0.4, 0.0, 0.8};// sorter servo positions for outtaking
    public final double[] outtakePos = {0.4, 0.0, 0.8};//*/{1.0, 0.6, 0.2}; // sorter servo positions for intaking
    public double sorterOffset = 0d;
    public byte[] sorterContents = {0, 0, 0}; // what is stored in each sorter slot 0 = empty, 1 = purple, 2 = green
    public double[] liftPos = {0.6, 0.2};

    public int currentPos = 0; // 0-2
    int targetTps = 0; // protect launcher tps from override
    ElapsedTime intakeTimer = new ElapsedTime();
    public ElapsedTime launchTimer = new ElapsedTime();

    // initialize flags
    public boolean intaking = false;
    private boolean launchingPurple = false;
    private boolean launchingGreen = false;

    // CONSTRUCTOR
    // assign hardware
    public Hardware(HardwareMap hardwareMap) {
        // e0 = expansion hub 0, c2 = control hub 2
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        launcherLeft = hardwareMap.get(DcMotorEx.class, "launcherL");
        launcherRight = hardwareMap.get(DcMotorEx.class, "launcherR");

        launcherTurn = hardwareMap.get(CRServo.class, "launcherYaw");
        limelightTurn = hardwareMap.get(CRServo.class, "limeservo");

        intake = hardwareMap.get(DcMotor.class, "intake"); // e2
        sorter = hardwareMap.get(Servo.class, "sorter"); //c2
        outtakeTransferLeft = hardwareMap.get(Servo.class, "liftLeft"); // c5
        outtakeTransferRight = hardwareMap.get(Servo.class, "liftRight"); // c5
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSens");


        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherRight.setDirection(DcMotorSimple.Direction.REVERSE);

        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void tryIntake(boolean button) {
        // check if intake button is being pressed and not currently intaking
        if (button && !intaking) {
            // set sorter position
            for (int i = 0; i < 3; i++) {
                //if spot is empty, set to that pos
                if (sorterContents[i] == 0) {
                    stopLaunch();

                    intaking = true;
                    intake.setPower(1);

                    sorter.setPosition(intakePos[i]);
                    currentPos = i;
                    intakeTimer.reset();

                    break;
                }
            }
        }
        if (intaking && intakeTimer.milliseconds() > 1000) {
            byte guess = detectFilled();
            if (guess == 0) return;
            //set current sorter pos to color-sensor-detected color
            sorterContents[currentPos] = guess;
            currentPos = (currentPos + 2) % 3;
            //change to outtake
            sorter.setPosition(outtakePos[currentPos]);
            stopIntake();
            // treat this as a loop
            // try to go to the artifact (maybe split int tryIntakePurple and tryIntakeGreen)
            // check if intaking was completed -> store color at position
        }
    }

    public void stopIntake() {
        intake.setPower(0);
        intaking = false;
    }

    public void stopLaunch() {
        outtakeTransferLeft.setPosition(liftPos[0]);
        outtakeTransferRight.setPosition(1-liftPos[0]);
        launcherLeft.setVelocity(0);
        launcherRight.setVelocity(0);
        launchingPurple = false;
        launchingGreen = false;
    }

    // NOTE: if the color is not in the
    public void tryLaunch(boolean button, int color, int tps) { // 1=purple, 2=green, other=any color
        if (button && !(launchingPurple || launchingGreen)) { // on first button press
            // check if sorter has purple
            for (int i = currentPos; i < currentPos + 3; i++) { // for every sorter position starting at the current one
                //if position has purple
                if (sorterContents[i % 3] != 0 && (sorterContents[i % 3] == color || color == 0)) {
                    stopLaunch();
                    stopIntake();

                    launchingPurple = sorterContents[i % 3] == 1;
                    launchingGreen = sorterContents[i % 3] == 2;

                    // TODO: set velocity based on apriltag distance
                    launcherLeft.setVelocity(tps + 20);
                    launcherRight.setVelocity(tps + 20);

                    targetTps = tps;

                    currentPos = i % 3;
                    sorter.setPosition(outtakePos[currentPos]);
                    launchTimer.reset();

                    break;
                }
            }
        }
        if ((launchingPurple || launchingGreen) && launcherLeft.getVelocity() > targetTps && outtakeTransferLeft.getPosition() != liftPos[1] && launchTimer.milliseconds() > 600) {
            outtakeTransferLeft.setPosition(liftPos[1]);
            outtakeTransferRight.setPosition(1-liftPos[1]);
            sorterContents[currentPos] = 0;
            launchTimer.reset();
        }
        if (outtakeTransferLeft.getPosition() == liftPos[1] && launchTimer.milliseconds() > 1000) {
            stopLaunch();
        }
    }

    public void doDrive(double ctrlX, double ctrlY, double ctrlYaw) {
        if (Math.abs(ctrlY) < 0.1) {
            ctrlY = 0;
        }
        if (Math.abs(ctrlX) < 0.1) {
            ctrlX = 0;
        }
        if (Math.abs(ctrlYaw) < 0.1) {
            ctrlYaw = 0;
        } else if (Math.abs(ctrlYaw) < 0.5) {
            ctrlYaw = Math.signum(ctrlYaw) * 0.5;
        }

        double flPwr = ctrlY - ctrlYaw - ctrlX;
        double frPwr = ctrlY + ctrlYaw + ctrlX;
        double blPwr = ctrlY - ctrlYaw + ctrlX;
        double brPwr = ctrlY + ctrlYaw - ctrlX;

        double denominator = Math.max(Math.max(Math.max(flPwr, frPwr), Math.max(blPwr, brPwr)), 1);

        frontLeft.setPower(flPwr / denominator);
        frontRight.setPower(frPwr / denominator);
        backLeft.setPower(blPwr / denominator);
        backRight.setPower(brPwr / denominator);
    }

    public void doDrive(double ctrlX, double ctrlY, double ctrlYaw, double speedX, double speedY, double speedYaw) {
        double pwrY = ctrlY * speedY;
        double pwrX = ctrlX * speedX;
        double pwrYaw = ctrlYaw * speedYaw;

        if (Math.abs(pwrY) < 0.1) {
            pwrY = 0;
        }
        if (Math.abs(pwrX) < 0.1) {
            pwrX = 0;
        }
        if (Math.abs(pwrYaw) < 0.1) {
            pwrYaw = 0;
        } else if (Math.abs(pwrYaw) < 0.5) {
            pwrYaw = Math.signum(pwrYaw) * 0.5;
        }

        double flPwr = pwrY - pwrYaw - pwrX;
        double frPwr = pwrY + pwrYaw + pwrX;
        double blPwr = pwrY - pwrYaw + pwrX;
        double brPwr = pwrY + pwrYaw - pwrX;

        double denominator = Math.max(Math.max(Math.max(flPwr, frPwr), Math.max(blPwr, brPwr)), 1);

        frontLeft.setPower(flPwr / denominator);
        frontRight.setPower(frPwr / denominator);
        backLeft.setPower(blPwr / denominator);
        backRight.setPower(brPwr / denominator);
    }

    public int getCurrentPos() {
        for (int i = 0; i < sorterContents.length; i++) {
            if (sorter.getPosition() == intakePos[i] || sorter.getPosition() == outtakePos[i]) {
                return i;
            }
        }
        return -1;
    }

    public byte detectFilled() {
        float red = colorSensor.red();
        float green = colorSensor.green();
        float blue = colorSensor.blue();

        float multiplier = 255f / Math.max(Math.max(red, green), Math.max(blue, 255f));
        green *= multiplier;
        blue *= multiplier;

        byte guess = 0;
        if (blue > 200 && blue > green) {
            //likely purple artifact
            guess = 1;
        } else if (green > 200 && green > blue) {
            //likely green artifact
            guess = 2;
        }

        return guess;
    }

            /*public void setSide (String side){
                switch (side) {
                    case "blue":
                        limelight.pipelineSwitch(6); // pretend this detects april tag id 20
                        break;
                    case "red":
                        limelight.pipelineSwitch(5); // pretend this detects april tag id 24
                        break;
                }
            }*/
            /*public String autoAimTurret ( boolean isBlueTeam, float llsP){
                LLResult result = limelight.getLatestResult();
                double id = -1;
                //doesn't work for detecting if its true and it doesnt cause...
                //errors not having it so if it aint broke
                //if(true){//result != null && result.isValid()) {

                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    id = fiducial.getFiducialId();
                }
                //if((id == 20 && isBlueTeam) || (id == 24 && !isBlueTeam)) {
                if (result != null && result.isValid()) {
                    launcherTurn.setPower(0.6);// * llsP * result.getTx());
                    limelightTurn.setPower(llsP * result.getTx() * Math.abs(result.getTx()));
                } else {
                    launcherTurn.setPower(0);
                    limelightTurn.setPower(0);
                }
                //}

                return "April tag found at degree, " + result.getTx() + "ID = " + id;
            }*/
}