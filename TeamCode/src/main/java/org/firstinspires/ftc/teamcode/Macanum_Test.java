package org.firstinspires.ftc.teamcode;

import android.media.audiofx.AcousticEchoCanceler;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Arrays;
import java.util.List;

@TeleOp
public class Macanum_Test extends OpMode {
    DcMotorEx FrontLeftMotor;
    DcMotorEx BackLeftMotor;
    DcMotorEx FrontRightMotor;
    DcMotorEx BackRightMotor;
    //DcMotorEx LeftLaunchMotor;
    HuskyLens Camera;
    SparkFunOTOS Odometry;
    public enum State {
        DEFAULT,
        AIMING
    }
    State currentState = State.DEFAULT;

    @Override
    public void init() {
        Odometry = hardwareMap.get(SparkFunOTOS.class, "Odometry");
        Odometry.setLinearUnit(DistanceUnit.INCH);
        Odometry.setPosition(new SparkFunOTOS.Pose2D(0,0,0));

        Camera = hardwareMap.get(HuskyLens.class,"Huskylens");
        Camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        FrontLeftMotor = hardwareMap.get(DcMotorEx.class,"frontLeft");
        BackLeftMotor = hardwareMap.get(DcMotorEx.class,"backLeft");
        FrontRightMotor = hardwareMap.get(DcMotorEx.class,"frontRight");
        BackRightMotor = hardwareMap.get(DcMotorEx.class,"backRight");

        FrontRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        FrontLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public HuskyLens.Block getTag() {
        List<HuskyLens.Block> blocks = Arrays.asList(Camera.blocks());
        HuskyLens.Block targetBlock = null;
        for (HuskyLens.Block block : blocks) {
            if (block.id != 0) {
                if (targetBlock != null) {
                    if (block.x * block.y > targetBlock.x * targetBlock.y) { // Checks which AprilTag is the largest on the screen.
                        targetBlock = block; // Sets block as currentBlock
                    }
                } else { // If not currentBlock then set block as currentBlock.
                    targetBlock = block;
                }
            }
        }

        return targetBlock; // Returns The AprilTag Block
    }
    public void Telemetry(HuskyLens.Block block) {
        telemetry.addLine("==== Controls ====");
        telemetry.addLine("Game Pad 1:");
        telemetry.addLine("Left Stick: Macanum Drive");
        telemetry.addLine("Right Stick: Rotation");
        telemetry.addLine("Y Button: Toggle Aiming");
        telemetry.addLine("X Button: Recenter");
        telemetry.addLine("================");
        if (block != null) {
            telemetry.addData("id:", block);
        }
        telemetry.addData("State:", currentState);
        telemetry.addData("Position",Odometry.getPosition());
        telemetry.addData("Rot",DegreesToPose2D(Odometry.getPosition().h));
    }

    public SparkFunOTOS.Pose2D DegreesToPose2D(double degrees){
        Double Radians = degrees * (Math.PI / 180);
        SparkFunOTOS.Pose2D vecter = new SparkFunOTOS.Pose2D(Math.cos(Radians),Math.sin(Radians),0);

        return vecter;
    }

    public void MacanumDrive(double x, double y, double r, double ad) {
        SparkFunOTOS.Pose2D vector = DegreesToPose2D(Odometry.getPosition().h);
        x += vector.x;
        y += vector.y;
        r += ad;
        double m = 1;

        if (gamepad1.left_bumper) {
            m = 0.5;
        }

        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1)/m;

        double FTVelocity = (x + y + r)/d * 2000;
        double BTVelocity = (x - y + r)/d * 2000;
        double FRVelocity = (x - y - r)/d * 2000;
        double BRVelocity = (x + y - r)/d * 2000;

        FrontLeftMotor.setVelocity(FTVelocity);
        BackLeftMotor.setVelocity(BTVelocity);
        FrontRightMotor.setVelocity(FRVelocity);
        BackRightMotor.setVelocity(BRVelocity);
    }

    @Override
    public void loop() {
        double x = gamepad1.left_stick_y;
        double y = -gamepad1.left_stick_x;
        double r = gamepad1.right_stick_x;

        SparkFunOTOS.Pose2D currentVector = Odometry.getPosition();

        Odometry.setPosition(new SparkFunOTOS.Pose2D(currentVector.x,currentVector.y,currentVector.h-1.74567));

        HuskyLens.Block block = getTag(); // returns Apriltag Block

        double ad = 0; // what is added to the rotation for aim

        if (block != null && block.id == 1 && gamepad1.y && currentState != State.AIMING) {
            currentState = State.AIMING;
        } else {
            if (gamepad1.y || block == null || block.id != 1.0) {
                currentState = State.DEFAULT;
            } else {
                ad = Math.min((double) block.x,-0.5);
                ad = Math.max(ad,0.5);
            }
        }

        MacanumDrive(x,y,r,ad);
        Telemetry(block);
    }
}
