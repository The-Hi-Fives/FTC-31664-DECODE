package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Arrays;
import java.util.List;

@TeleOp(name="Macanum Default TeleOp", group="TeleOp")
public class Macanum_Default_TeleOp extends OpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake, LeftLaunch, RightLaunch;
    CRServo backLeftConveyor, backRightConveyor, frontLeftConveyor, frontRightConveyor, topConveyor;
    HuskyLens Camera;
    Servo LED;
    double PulleyPos = 0;
    public enum State {
        DEFAULT,
        AIMING,
    }

    State currentState = State.DEFAULT;
    double AlternateVelocity = 0;
    static double PulleyMax = 180, PulleyMin = 0;
    @Override
    public void init() {
        // 175.0?

        LED = hardwareMap.get(Servo.class,"LED");
        //
        LED.setPosition(0.277); // Sets the LED color to Red (Hopefully)

        Camera = hardwareMap.get(HuskyLens.class,"Huskylens");
        Camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        FrontLeftMotor = hardwareMap.get(DcMotorEx.class,"frontLeft");
        BackLeftMotor = hardwareMap.get(DcMotorEx.class,"backLeft");
        FrontRightMotor = hardwareMap.get(DcMotorEx.class,"frontRight");
        BackRightMotor = hardwareMap.get(DcMotorEx.class,"backRight");

        Intake = hardwareMap.get(DcMotorEx.class,"intake");
        RightLaunch = hardwareMap.get(DcMotorEx.class,"rightLaunch");
        LeftLaunch = hardwareMap.get(DcMotorEx.class,"leftLaunch");

        topConveyor = hardwareMap.get(CRServo.class,"topC");                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
        backLeftConveyor = hardwareMap.get(CRServo.class,"backLeftC");
        backRightConveyor = hardwareMap.get(CRServo.class,"backRightC");
        frontLeftConveyor = hardwareMap.get(CRServo.class,"frontLeftC");
        frontRightConveyor = hardwareMap.get(CRServo.class,"frontRightC");

        FrontRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        FrontLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        LeftLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightConveyor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightConveyor.setDirection(DcMotorSimple.Direction.REVERSE);
        topConveyor.setDirection(DcMotorSimple.Direction.REVERSE);
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
        telemetry.addLine("Game Pad 1: (Driver) ==");
        telemetry.addLine("Left Stick: Macanum Drive");
        telemetry.addLine("Right Stick: Rotation");
        telemetry.addLine("================");
        telemetry.addLine("Game Pad 2: (Attachments) ==");
        telemetry.addLine("A (Cross) Button: Spit out");
        telemetry.addLine("B (Circle) Button: Launch (Far)");
        telemetry.addLine("Y (Triangle) Button: Launch (Near)");
        telemetry.addLine("== Both Game Pads ==");
        telemetry.addLine("Left Bumper: Intake");
        telemetry.addLine("Right Bumper: Outtake");
        telemetry.addLine("==== Telemetry ====");

    }
    public void IntakeOuttake(double AlternateVelocity) {
        // No Pulley? :\
        // Intake/Outtake
        if (gamepad2.a) {
            // Outtake
            backLeftConveyor.setPower(-1);
            frontLeftConveyor.setPower(-1);
            backRightConveyor.setPower(-1);
            frontRightConveyor.setPower(-1);
        }
        if (gamepad2.left_bumper || gamepad1.left_bumper) {
            Intake.setVelocity(-1872);
        } else if (gamepad2.right_bumper || gamepad1.right_bumper) {
            Intake.setVelocity(1872);
        } else {
            Intake.setVelocity(0);
        }
        double velo = 0;
        // Launching
        if (gamepad2.b || gamepad2.y) {
            backLeftConveyor.setPower(1);
            frontLeftConveyor.setPower(1);
            backRightConveyor.setPower(1);
            frontRightConveyor.setPower(1);
            if (gamepad2.b) {
                velo = 2500;
            } else {
                velo = 2300;
            }
        } else {
            backLeftConveyor.setPower(0);
            frontLeftConveyor.setPower(0);
            backRightConveyor.setPower(0);
            frontRightConveyor.setPower(0);
            velo = 0;
        }
        // Backup Intake
        if (gamepad2.x) {
            velo = -500;
        }
        if (velo != 0 && RightLaunch.getVelocity() > velo-70 && LeftLaunch.getVelocity() > velo-70) {
            topConveyor.setPower(1);
        } else {
            topConveyor.setPower(0);
        }
        RightLaunch.setVelocity(velo);
        LeftLaunch.setVelocity(velo);
    }
    public void MacanumDrive(double x, double y, double r, double ad) {
        r += ad;

        double m = 1;

        if (gamepad1.left_bumper) {
            m = 0.5;
        }

        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1)/m;

        double FLVelocity = (x + y + r)/d * 2000;
        double BLVelocity = (x - y + r)/d * 2000;
        double FRVelocity = (x - y - r)/d * 2000;
        double BRVelocity = (x + y - r)/d * 2000;

        FrontLeftMotor.setVelocity(FLVelocity);
        BackLeftMotor.setVelocity(BLVelocity);
        FrontRightMotor.setVelocity(FRVelocity);
        BackRightMotor.setVelocity(BRVelocity);
    }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public static double normalize(double value, double min, double max) {
        return (value - min)/(max - min); // returns a number between 0 and 1
    }
    @Override
    public void loop() {
        double x = gamepad1.left_stick_y;
        double y = -gamepad1.left_stick_x;
        double r = -gamepad1.right_stick_x;

        HuskyLens.Block block = getTag(); // returns AprilTag Block

        // The Logic for Aiming
        double ad = 0; // what is added to the rotation for aim
        AlternateVelocity = 0;
        if (block != null && block.id == 1 && gamepad1.y && currentState != State.AIMING) {
            currentState = State.AIMING; // Changes state to AIMING
        } else {
            if (gamepad1.y || block == null || block.id != 1.0 && block.id != 2.0) {
                currentState = State.DEFAULT; // Changes state to DEFAULT
            } else {
                ad = clamp((double) block.x,-0.5,0.5); // Makes sure the ad is between -0.5 and 0.5
                //AlternatePos = normalize((block.width * block.height)/1000,PulleyMin,PulleyMax) * 120;
                //AlternateVelocity = normalize((block.width * block.height)/1000,0,2000) * 2000;
            }
        }
        // Sets the Rotation Offset to the current when X is pressed on gamepad1

        MacanumDrive(x,y,r,ad);
        IntakeOuttake(AlternateVelocity);
        Telemetry(); // telemetry
    }
}
