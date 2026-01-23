package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;

@Autonomous(name="Blue Near Collect Launch Auto", group="Default")
public class Blue_Near_Collect_Launch_Auto extends LinearOpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake, LeftLaunch, RightLaunch, Conveyor;
    HuskyLens Camera;

    public void Macanum(Double x,Double y,Double r,Integer Speed) {
        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1);

        // turn y into x and vice versa
        double send = -y;
        y = x;
        x = send;

        double FTVelocity = (x + y + r)/d * Speed; // Don't touch or it
        double BTVelocity = (x - y + r)/d * Speed; // may NEVER work again...
        double FRVelocity = (x - y - r)/d * Speed;
        double BRVelocity = (x + y - r)/d * Speed;

        FrontLeftMotor.setVelocity(FTVelocity);
        BackLeftMotor.setVelocity(BTVelocity);
        FrontRightMotor.setVelocity(FRVelocity);
        BackRightMotor.setVelocity(BRVelocity);
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
            double distance = 8.25 / targetBlock.height; // Formula for distance in inches
            telemetry.addData("Distance", distance);
            telemetry.addData("ID", targetBlock.id);
        }
        return targetBlock; // Returns The AprilTag Block
    }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public void aim_bot(int max_rounds){
        HuskyLens.Block block = getTag();
        if (block != null && block.id == 5){
            double screen_middle_x;
            int rounds = 0;
            while (opModeIsActive()){
                sleep(100);
                block = getTag();
                rounds += 1;
                if (block == null){
                    break;
                }
                screen_middle_x = block.x - 160;
                if (rounds >= max_rounds){
                    break;
                }
                Macanum(0.0,0.0,-clamp(screen_middle_x,-1.0,1.0),50); // clamp
            }
        }
        Macanum(0.0,0.0,0.0,0);
    }
    public void yaw_bot(){
        HuskyLens.Block block = getTag();
        if (block != null && block.id == 5){
            double screen_middle_x;
            int max_fails = 30;
            int fails = 0;
            while (opModeIsActive()){
                block = getTag();
                if (block == null){
                    fails += 1;
                    continue;
                } else {
                    fails = 0;
                }
                screen_middle_x = block.x - 160;
                if (fails >= max_fails){
                    break;
                }
                if (!(5.0 < Math.abs(screen_middle_x)) && !(block.width < block.height * 0.7)){
                    break;
                }
                if (block.width < block.height * 0.7) {
                    double clampedValue = clamp(screen_middle_x, -1.0, 1.0);
                    Macanum(-clampedValue, 0.0, -clampedValue, 50);
                } else {
                    // Move normally
                    Macanum(0.0, 0.0, -clamp(screen_middle_x, -1.0, 1.0), 50);
                }
            }
        }
        Macanum(0.0,0.0,0.0,0);
    }
    @Override
    public void runOpMode() throws InterruptedException {

        Camera = hardwareMap.get(HuskyLens.class,"HuskyLens");
        Camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        FrontLeftMotor = hardwareMap.get(DcMotorEx.class,"frontLeft");
        BackLeftMotor = hardwareMap.get(DcMotorEx.class,"backLeft");
        FrontRightMotor = hardwareMap.get(DcMotorEx.class,"frontRight");
        BackRightMotor = hardwareMap.get(DcMotorEx.class,"backRight");

        Intake = hardwareMap.get(DcMotorEx.class,"intake");
        RightLaunch = hardwareMap.get(DcMotorEx.class,"rightLaunch");
        LeftLaunch = hardwareMap.get(DcMotorEx.class,"leftLaunch");

        Conveyor = hardwareMap.get(DcMotorEx.class,"Conveyor");

        FrontRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        FrontLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        LeftLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);

        FrontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        // Sequence
        // head back to have space to launch
        Intake.setVelocity(3000);
        Macanum(0.0,-1.0,0.0,2000);
        sleep(1200);

        // Aim-bot
        aim_bot(20);

        int launcherVelocity = 2500;

        LeftLaunch.setVelocity(launcherVelocity);
        RightLaunch.setVelocity(launcherVelocity);

        // Wait for Velocity
        while (RightLaunch.getVelocity() != launcherVelocity || LeftLaunch.getVelocity() != launcherVelocity && opModeIsActive()){
            sleep(100);
        }
        sleep(1000);

        // Enable Conveyor
        Conveyor.setVelocity(3000);

        sleep(3000);

        Conveyor.setVelocity(0);
        LeftLaunch.setVelocity(0);
        RightLaunch.setVelocity(0);

        sleep(100);

        int turnTime = 1500;

        // Turn Robot 135 degrees
        Macanum(0.0,0.0,-1.0,900);
        sleep(turnTime);

        int intakeTime = 4100;

        // Move backwards and intake
        Macanum(0.0,-1.0,0.0,500);
        sleep(intakeTime);
        Macanum(0.0,1.0,0.0,500);
        sleep(intakeTime);

        // Move forward and Turn 135 degrees
        Macanum(0.0,0.0,1.0,900);
        sleep(turnTime); // however long it takes

        // Aim-bot
        aim_bot(20);

        // start Launch Motors
        LeftLaunch.setVelocity(launcherVelocity);
        RightLaunch.setVelocity(launcherVelocity);

        // Wait for Velocity
        while (RightLaunch.getVelocity() != launcherVelocity || LeftLaunch.getVelocity() != launcherVelocity && opModeIsActive()){
            sleep(100);
        }
        sleep(1000);
        // Enable Conveyor
        Conveyor.setVelocity(3000);

        // Out of zone
        sleep(3000);
        Conveyor.setVelocity(0);
        Macanum(1.0,0.0,0.0,2000);
        sleep(700);
        Macanum(0.0,0.0,0.0,0);
        // no telemetry :P
    }
}
