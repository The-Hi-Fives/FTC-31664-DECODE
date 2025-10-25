package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

@TeleOp
public class Macanum_Test extends OpMode {
    DcMotorEx FrontLeftMotor;
    DcMotorEx BackLeftMotor;
    DcMotorEx FrontRightMotor;
    DcMotorEx BackRightMotor;
    HuskyLens Camera;

    public enum State {
        DEFAULT,
        AIMING
    }
    State currentState = State.DEFAULT;

    @Override
    public void init() {
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
        List<HuskyLens.Block> blocks = null;//Arrays.asList(Camera.blocks());
        HuskyLens.Block targetBlock = null;
        if (false) {
            for (HuskyLens.Block block : blocks) {
                if (block.id != 0) {
                    if (targetBlock != null) {
                        if (block.x * block.y > targetBlock.x * targetBlock.y) {
                            targetBlock = block;
                        }
                    } else {
                        targetBlock = block;
                    }
                }
            }
        }

        return targetBlock;
    }
    public void Telemetry(HuskyLens.Block block) {
        telemetry.addLine("============");
        telemetry.addLine("Controls:");
        telemetry.addLine("Left Stick: Macanum Drive");
        telemetry.addLine("Right Stick: Rotation");
        telemetry.addLine("============");
        if (block != null) {
            telemetry.addData("id:", block);
        }
        telemetry.addData("State:", currentState);
    }

    public void MacanumDrive(double x, double y, double r, double ad) {
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
        HuskyLens.Block block = getTag();

        double ad = 0;

        //if (block != null && block.id == 1 && gamepad1.y && currentState != State.AIMING) {
        //    currentState = State.AIMING;
        //} else {
        //    if (gamepad1.y || block != null || block.id != 1) {
        //        currentState = State.DEFAULT;
        //    } else {
        //        ad = Math.min((double) block.x,-0.5);
        //        ad = Math.max(ad,0.5);
        //    }
        //}

        MacanumDrive(x,y,r,ad);
        Telemetry(block);
    }
}
