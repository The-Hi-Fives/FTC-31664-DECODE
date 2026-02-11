package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

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
    public void aim_bot(int max_rounds) {
        // If I wrote a book on how many times I had to change this code, then it's page count would rival the Bible's.
        HuskyLens.Block block = getTag();
        double screen_middle_x;
        int rounds = 0;
        while (opModeIsActive()) {
            sleep(50);
            block = getTag();
            rounds += 1;
            if (block == null) {
                Macanum(0.0,0.0,0.0,0);
                continue;
            }
            if (block.id != 1) {
                Macanum(0.0,0.0,0.0,0);
                continue;
            }
            screen_middle_x = block.x - 160;
            if (rounds >= max_rounds) {
                break;
            }
            Macanum(0.0, 0.0, clamp(screen_middle_x, -1.0, 1.0), 100); // clamp
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
        Conveyor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
        Conveyor.setDirection(DcMotorSimple.Direction.REVERSE);

        FrontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        // Sequence
        // head back to have space to launch
        Intake.setVelocity(3000);
        Macanum(0.0,-1.0,0.0,2000);
        sleep(1400);

        // Aim-bot
        aim_bot(20);

        int launcherVelocity = 2000;

        LeftLaunch.setVelocity(launcherVelocity);
        RightLaunch.setVelocity(launcherVelocity);

        // Wait for Velocity
        while (RightLaunch.getVelocity() != launcherVelocity || LeftLaunch.getVelocity() != launcherVelocity && opModeIsActive()){
            sleep(50);
            telemetry.addData("Launch velocity",RightLaunch.getVelocity());
            telemetry.addData("Launch velocity",LeftLaunch.getVelocity());
            telemetry.update();
        }
        sleep(1000);

        // Enable Conveyor
        Conveyor.setVelocity(3000);

        int rounds = 0;
        int max_rounds = 60;

        while (rounds <= max_rounds){
            sleep(50);
            rounds += 1;
            telemetry.addData("Launch velocity",RightLaunch.getVelocity());
            telemetry.addData("Launch velocity",LeftLaunch.getVelocity());
            telemetry.update();
        }

        Conveyor.setVelocity(0);
        LeftLaunch.setVelocity(0);
        RightLaunch.setVelocity(0);

        sleep(20);

        int turnTime = 1500;

        // move a little forward
        Macanum(0.0,1.0,0.0,2000);
        sleep(150);


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

        // move a little backward
        Macanum(0.0,-1.0,0.0,2000);
        sleep(200);

        // Aim-bot
        aim_bot(20);

        // start Launch Motors
        LeftLaunch.setVelocity(launcherVelocity);
        RightLaunch.setVelocity(launcherVelocity);

        // Wait for Velocity
        while (RightLaunch.getVelocity() != launcherVelocity || LeftLaunch.getVelocity() != launcherVelocity && opModeIsActive()){
            sleep(50);
        }
        sleep(1000);
        // Enable Conveyor
        Conveyor.setVelocity(3000);

        rounds = 0;
        max_rounds = 60;

        while (rounds <= max_rounds){
            sleep(50);
            rounds += 1;
            telemetry.addData("Launch velocity",RightLaunch.getVelocity());
            telemetry.addData("Launch velocity",LeftLaunch.getVelocity());
            telemetry.update();
        }

        // Out of zone
        Conveyor.setVelocity(0);
        Macanum(1.0,0.0,0.0,2000);
        sleep(700);
        Macanum(0.0,0.0,0.0,0);
        // no telemetry :P
    }
}
