package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Arrays;
import java.util.List;

@TeleOp(name="Macanum Main TeleOp", group="TeleOp")
public class Macanum_Test extends OpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake;
    CRServo leftPulley, rightPulley;
    HuskyLens Camera;
    SparkFunOTOS Odometry;
    public enum State {
        DEFAULT,
        AIMING
    }
    State currentState = State.DEFAULT;
    double Rotation_Offset = 0;
    @Override
    public void init() {
        Odometry = hardwareMap.get(SparkFunOTOS.class, "Odometry");
        Odometry.setLinearUnit(DistanceUnit.INCH);
        Odometry.setPosition(new SparkFunOTOS.Pose2D(0,0,0));

        //Camera = hardwareMap.get(HuskyLens.class,"Huskylens");
        //Camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        FrontLeftMotor = hardwareMap.get(DcMotorEx.class,"frontLeft");
        BackLeftMotor = hardwareMap.get(DcMotorEx.class,"backLeft");
        FrontRightMotor = hardwareMap.get(DcMotorEx.class,"frontRight");
        BackRightMotor = hardwareMap.get(DcMotorEx.class,"backRight");
        Intake = hardwareMap.get(DcMotorEx.class,"intake");
        leftPulley = hardwareMap.get(CRServo.class,"leftpulley");
        rightPulley = hardwareMap.get(CRServo.class,"rightpulley");

        FrontRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        FrontLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        Odometry.setSignalProcessConfig(new SparkFunOTOS.SignalProcessConfig((byte)0x0B));
        Odometry.initialize();
        Odometry.resetTracking();
    }

    public HuskyLens.Block getTag() {
        List<HuskyLens.Block> blocks = Arrays.asList(Camera.blocks());
        HuskyLens.Block targetBlock = null;
        for (HuskyLens.Block block : blocks) {
            if (block.id != 0) {
                if (targetBlock != null) {
                    if (block.width * block.height > targetBlock.width * targetBlock.height) { // Checks which AprilTag is the largest on the screen.
                        targetBlock = block; // Sets block as currentBlock
                    }
                } else { // If not currentBlock then set block as currentBlock.
                    targetBlock = block;
                }
            }
        }
        return targetBlock; // Returns The AprilTag Block
    }
    public void Telemetry() {
        telemetry.addLine("==== Controls ====");
        telemetry.addLine("Game Pad 1: (Driver)");
        telemetry.addLine("Left Stick: Macanum Drive");
        telemetry.addLine("Right Stick: Rotation");
        telemetry.addLine("Y (Triangle) Button: Toggle Aiming");
        telemetry.addLine("X (Square) Button: Recenter");
        telemetry.addLine("================");
        telemetry.addLine("Game Pad 2: (Attachments)");
        telemetry.addLine("Up/Down D-Pad: Manual Up/Down Aiming");
        telemetry.addLine("A (Cross) Button: Spit out");
        telemetry.addLine("B (Circle) Button: Launch");
        telemetry.addLine("==== Telemetry ====");
        //if (block != null) {
        //    telemetry.addData("id:", block);
        //}
        telemetry.addData("State:", currentState);
        telemetry.addData("Position",Odometry.getPosition());
    }
    public void MacanumDrive(double x, double y, double r, double ad) {
        double Rot = (Rotation_Offset + Odometry.getPosition().h + 360) % 360;

        double Radians = Math.toRadians(Rot);

        double newX = y * Math.sin(Radians) - x * Math.cos(Radians); // idk if it works
        double newY = x * Math.sin(Radians) + y * Math.cos(Radians); // ditto
        r += ad;

        double m = 1;

        if (gamepad1.left_bumper) {
            m = 0.5;
        }

        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1)/m;

        double FTVelocity = (newX + newY + r)/d * 2000;
        double BTVelocity = (newX - newY + r)/d * 2000;
        double FRVelocity = (newX - newY - r)/d * 2000;
        double BRVelocity = (newX + newY - r)/d * 2000;

        FrontLeftMotor.setVelocity(FTVelocity);
        BackLeftMotor.setVelocity(BTVelocity);
        FrontRightMotor.setVelocity(FRVelocity);
        BackRightMotor.setVelocity(BRVelocity);
    }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    @Override
    public void loop() {
        double x = gamepad1.left_stick_y;
        double y = -gamepad1.left_stick_x;
        double r = -gamepad1.right_stick_x;

        SparkFunOTOS.Pose2D currentVector = Odometry.getPosition();

        Odometry.setPosition(new SparkFunOTOS.Pose2D(currentVector.x,currentVector.y,currentVector.h+15)); // Corrects the Heading (Rotation) of the Odometry

        //HuskyLens.Block block = getTag(); // returns AprilTag Block

        double ad = 0; // what is added to the rotation for aim

        // The Logic for Aiming
        //if (block != null && block.id == 1 && gamepad1.y && currentState != State.AIMING) {
        //    currentState = State.AIMING; // Changes state to AIMING
        //} else {
        //    if (gamepad1.y || block == null || block.id != 1.0) {
        //        currentState = State.DEFAULT; // Changes state to DEFAULT
        //    } else {
        //        ad = clamp((double) block.x,-0.5,0.5); // Makes sure the ad is between -0.5 and 0.5
        //    }
        //}
        // Sets the Rotation Offset to the current when X is pressed on gamepad1
        if (gamepad1.x) {
            Rotation_Offset = Odometry.getPosition().h;
        }

        MacanumDrive(x,y,r,ad);
        Telemetry();
    }
}
