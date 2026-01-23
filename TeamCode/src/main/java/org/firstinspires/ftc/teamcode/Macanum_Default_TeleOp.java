package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

@TeleOp(name="Macanum Default TeleOp", group="TeleOp")
public class Macanum_Default_TeleOp extends OpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake, LeftLaunch, RightLaunch;
    DcMotorEx Conveyor;
    HuskyLens Camera;
    boolean aiming = false;
    boolean debounce = false;
    @Override
    public void init() {
        // 175.0?

        Camera = hardwareMap.get(HuskyLens.class,"HuskyLens");
        Camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        FrontLeftMotor = hardwareMap.get(DcMotorEx.class,"frontLeft");
        BackLeftMotor = hardwareMap.get(DcMotorEx.class,"backLeft");
        FrontRightMotor = hardwareMap.get(DcMotorEx.class,"frontRight");
        BackRightMotor = hardwareMap.get(DcMotorEx.class,"backRight");
        Conveyor = hardwareMap.get(DcMotorEx.class,"Conveyor");

        Intake = hardwareMap.get(DcMotorEx.class,"intake");
        RightLaunch = hardwareMap.get(DcMotorEx.class,"rightLaunch");
        LeftLaunch = hardwareMap.get(DcMotorEx.class,"leftLaunch");

        FrontRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        FrontLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        LeftLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
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
        if (targetBlock != null) {
            telemetry.addData("ID ::",targetBlock.id);
        }
        return targetBlock; // Returns The AprilTag Block
    }

    public void Telemetry() {
        telemetry.addData("Aiming ::", aiming);
        telemetry.addData("Conveyor Velocity ::", Conveyor.getVelocity());
        telemetry.addData("Intake Velocity ::", Intake.getVelocity());
        telemetry.addLine("==== Controls ====");
        telemetry.addLine("Game Pad 1: (Driver) ==");
        telemetry.addLine("Left Stick: Macanum Drive");
        telemetry.addLine("Right Stick: Rotation");
        telemetry.addLine("Left Trigger: Toggle Aiming");
        telemetry.addLine("================");
        telemetry.addLine("Game Pad 2: (Attachments) ==");
        telemetry.addLine("A (Cross) Button: Spit out");
        telemetry.addLine("B (Circle) Button: Launch (Far)");
        telemetry.addLine("Y (Triangle) Button: Launch (Near)");
        telemetry.addLine("X (Square) Button: Backup Intake");
        telemetry.addLine("D-Pad Up/Down: Manual Conveyor");
        telemetry.addLine("== Both Game Pads ==");
        telemetry.addLine("Left Bumper: Intake");
        telemetry.addLine("Right Bumper: Outtake");
    }
    public void IntakeOuttake() {
        // No Pulley? :\
        // Intake/Outtake
        // Manual Conveyor Input
        if (gamepad2.dpad_down) {
            Conveyor.setVelocity(-250);
        } else if (gamepad2.dpad_up) {
            Conveyor.setVelocity(250);
        } else {
            Conveyor.setVelocity(0);
        }
        // Intake
        if (gamepad2.right_bumper || gamepad1.right_bumper) {
            Intake.setVelocity(4000);
        } else {
            Intake.setVelocity(0);
        }
        double velocity;
        // Launching
        if (gamepad2.b) {
            velocity = 2500;
        } else if (gamepad2.y) {
            velocity = 2500;
        } else {
            velocity = 0;
        }
        // Backup Intake
        if (gamepad2.x) {
            velocity = 500;
        }
        if (gamepad2.right_trigger >= 0.5) {
            Conveyor.setVelocity(400);
        } else if (velocity != 0 && RightLaunch.getVelocity() > velocity-50 && LeftLaunch.getVelocity() > velocity-50) {
            Conveyor.setVelocity(3000);
        } else {
            Conveyor.setPower(0);
        }
        RightLaunch.setVelocity(velocity);
        LeftLaunch.setVelocity(velocity);
        if (gamepad2.a) {
            // Outtake
            Conveyor.setVelocity(-500);
            Intake.setVelocity(-4000);
        }
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
        return Math.max(min, Math.min(max, value)); // Makes sure a value is in between two numbers.
    }
    public static double normalize(double value, double min, double max) {
        return (value - min)/(max - min); // Returns a number between 0 and 1
    }
    @Override
    public void loop() {
        telemetry.addLine("==== Telemetry ===="); // For formatting :D
        double x = gamepad1.left_stick_y;
        double y = -gamepad1.left_stick_x;
        double r = -gamepad1.right_stick_x;

        HuskyLens.Block block = getTag(); // returns AprilTag Block

        // The Logic for Aiming
        // The debounce variable is used for "just pressed" input
        double ad = 0; // What is added to the rotation for aim
        boolean is_left_trigger_down = (gamepad1.left_trigger > 0.5);
        if (is_left_trigger_down && !debounce) {
            aiming = !aiming; // Toggle boolean
            debounce = true;
        } else if (!is_left_trigger_down) {
            debounce = false;
        }
        if (aiming && block != null) { // Aim if aiming is enabled and block exists
            double screen_middle_x = (double) block.x - 160; // 160 being half of 320, the x screen resolution
            if (20 < Math.abs(screen_middle_x)) { // Check if it's in a certain threshold (i.e. more than 10)
                ad = clamp(screen_middle_x,-1.0,1.0); // Makes sure the ad is between -1.0 and 1.0
                //AlternatePos = normalize((block.width * block.height)/1000,PulleyMin,PulleyMax) * 120;
                //AlternateVelocity = normalize((block.width * block.height)/1000,0,2000) * 2000;
            }
        }

        MacanumDrive(x,y,r,ad/5);
        IntakeOuttake();
        Telemetry(); // telemetry
    }
}
