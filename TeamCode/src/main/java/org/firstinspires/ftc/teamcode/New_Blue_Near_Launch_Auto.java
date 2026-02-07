package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

@Autonomous(name="New Super Blue Near Launch Auto U", group="Default")
public class New_Blue_Near_Launch_Auto extends LinearOpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake, LeftLaunch, RightLaunch, Conveyor;
    HuskyLens Camera;
    @Override
    public void runOpMode() throws InterruptedException {
        setUp(); // set Up
        waitForStart(); // wait For Start

        // Sequence
        Intake.setVelocity(3000);

        // head back to have space to launch, being close to but not touching/crossing the alliance line
        Macanum(0.0, -1.0, 0.0, 2000);
        sleep(1100); // How long you can drive back until you almost cross the alliance line

        // Move the Robot so it is just about out of zone.
        Macanum(1.0, -1.0, 0.0, 1000);
        sleep(200);

        // Aim-bot for 0.5 seconds
        aim_bot(10);

        // Launch Artifact
        launchArtifact(2500);

        // Get out of zone
        Macanum(1.0, 0.0, 0.0, 2000);
        sleep(700);
        Macanum(0.0, 0.0, 0.0, 0);
        // no telemetry :o
    }
    public void launchArtifact(int targetVelocity) {
        LeftLaunch.setVelocity(targetVelocity);
        RightLaunch.setVelocity(targetVelocity);

        // Wait for Velocity
        while (RightLaunch.getVelocity() != targetVelocity || LeftLaunch.getVelocity() != targetVelocity && opModeIsActive()) {
            sleep(50);
        }
        sleep(1000);

        // Enable Conveyor
        Conveyor.setVelocity(500);

        sleep(3000);
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
    public void aim_bot(int max_rounds) { // 1 round is 50 milliseconds
        // If I wrote a book on how many times I had to change this code, then it's page count would rival the Bible's.
        HuskyLens.Block block;
        block = getTag();
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
    public void setUp(){
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

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
    }
}
