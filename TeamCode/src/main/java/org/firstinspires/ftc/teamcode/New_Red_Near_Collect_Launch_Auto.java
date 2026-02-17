package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;

@Autonomous(name="New Red Collect Near Launch Auto", group="Default")
public class New_Red_Near_Collect_Launch_Auto extends LinearOpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake, LeftLaunch, RightLaunch, Conveyor;
    HuskyLens Camera;
    public void setUp(){
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
        LeftLaunch.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        RightLaunch.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        Conveyor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
        Conveyor.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    @Override
    public void runOpMode() throws InterruptedException {
        setUp(); // set Up
        waitForStart(); // wait For Start

        // head back to have space to launch, being close to but not touching/crossing the alliance line
        Macanum(0.0, -1.0, 0.0, 2000);
        sleep(1050); // How long you can drive back until you almost cross the alliance line

        Macanum(0.0,0.0,0.0,0);
        sleep(100);

        // Move back more
        Macanum(-1.0,-1.0,0.0,1000);

        sleep(1000);

        // Aim-bot for 2 seconds
        aim_bot(40);

        // Launch Artifact
        launchArtifact(2300);

        /// SECOND

        Macanum(0.0,0.0,1.0,1800);
        sleep(750);

        Macanum(-1.0,0.0,0.0,1000);
        sleep(200);

        Intake.setVelocity(2000);
        Macanum(0.0,-1.0,0.0,1500);
        sleep(1300);

        Macanum(0.0,0.0,0.0,0);
        sleep(500);

        Macanum(0.0,1.0,0.0,1500); 
        sleep(1100);

        Intake.setVelocity(0);
        Macanum(1.0,0.0,0.0,1000);
        sleep(200);

        Macanum(0.0,0.0,-1.0,1800);
        sleep(750);

        // Aim-bot for 2 seconds
        aim_bot(40);

        // Launch Artifact
        launchArtifact(2300);

        // Get out of zone
        Macanum(-1.0, 0.0, 0.0, 2000);
        sleep(500);
        Macanum(0.0, 0.0, 0.0, 0);
        // no telemetry :o
    }
    public void waitForArtifactUnload() {
        sleep(3000);
    }
    public void waitForLaunchReady(int targetVelocity){
        while (RightLaunch.getVelocity() != targetVelocity || LeftLaunch.getVelocity() != targetVelocity && opModeIsActive()) {
            Conveyor.setVelocity(0);
            Intake.setVelocity(0);
            sleep(50);
        }
    }
    public void launchArtifact(int targetVelocity) {
        // Start Launch Motors
        LeftLaunch.setVelocity(targetVelocity);
        RightLaunch.setVelocity(targetVelocity);

        // shoot if
        int maxRounds = 10; // Checks to be executed.
        int round = 0;
        while (round < maxRounds) {
            round += 1;
            waitForLaunchReady(targetVelocity);
            Conveyor.setVelocity(1250);
            Intake.setVelocity(2000);

            sleep(50);
        }

        // wait long enough for the balls to be shot
        waitForArtifactUnload();
        Intake.setVelocity(0);
        Conveyor.setVelocity(0);
        LeftLaunch.setVelocity(0);
        RightLaunch.setVelocity(0);
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
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public void aim_bot(int max_rounds) { // 1 round is 50 milliseconds
        HuskyLens.Block block;
        double screen_middle_x;
        int rounds = 0;
        while (opModeIsActive()) {
            block = getTag();
            rounds += 1;
            if (rounds >= max_rounds) {
                break;
            }
            if (block == null) {
                Macanum(0.0,0.0,0.0,0);
                continue;
            }
            if (block.id != 5) {
                Macanum(0.0,0.0,0.0,0);
                continue;
            }
            screen_middle_x = block.x - 160;
            Macanum(0.0, 0.0, clamp(screen_middle_x, -1.0, 1.0), 100); // clamp
            sleep(50);
        }
        Macanum(0.0,0.0,0.0,0);
    }
    public void Macanum(Double x,Double y,Double r,Integer Speed) {
        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1);

        // turn y into x and vice versa because no work-y
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
}
