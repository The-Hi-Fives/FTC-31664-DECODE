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

@Autonomous(name="Universal Far Launch Auto", group="Default")
public class Universal_far_Launch_Auto extends LinearOpMode {
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
            if (block.id != 5 && block.id != 1) {
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
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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

        FrontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
        Conveyor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        // Sequence
        Macanum(0.0,0.0,0.0,0);

        //aim_bot(20);

        int target_velocity = 2500;

        LeftLaunch.setVelocity(target_velocity);
        RightLaunch.setVelocity(target_velocity);

        Macanum(0.0,1.0,0.0,2000);
        sleep(200);
        Macanum(0.0,0.0,0.0,0);

        while (RightLaunch.getVelocity() < target_velocity || LeftLaunch.getVelocity() < target_velocity){
            sleep(50);
        }

        Conveyor.setVelocity(3000);
        Intake.setVelocity(2000);

        sleep(3000);
        Macanum(-1.0,1.0,0.0,2000);
        sleep(250);
        Macanum(0.0,0.0,0.0,0);
        // no telemetry
    }
}
