package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.List;

@Autonomous(name="Red Near Collect Launch Auto", group="Default")
public class Red_Near_Collect_Launch_Auto extends LinearOpMode {
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
            telemetry.addData("ID", targetBlock.id);
        }
        return targetBlock; // Returns The AprilTag Block
    }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public void aim_bot(){
        HuskyLens.Block block = getTag();
        if (block != null && block.id == 5){
            telemetry.addData("data",Math.abs(getTag().x - 160));
            telemetry.update();
            while (Math.abs(getTag().x - 160) > 5 && opModeIsActive()){
                telemetry.addData("data",Math.abs(getTag().x - 160));
                telemetry.update();
                Macanum(0.0,0.0,clamp(getTag().x - 160,-0.5,0.5),100);
            }
        }
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
        Intake.setVelocity(1872);
        Macanum(0.0,-1.0,0.0,2000);
        sleep(1500);
        Macanum(0.0,0.0,0.0,0);

        // Aim-bot
        aim_bot();

        LeftLaunch.setVelocity(2300);
        RightLaunch.setVelocity(2300);

        // Wait for Velocity
        while (RightLaunch.getVelocity() != 2300 || LeftLaunch.getVelocity() != 2300 && opModeIsActive()){
            sleep(100);
        }

        // Enable Conveyor
        Conveyor.setVelocity(500);

        sleep(3000);

        // Turn Robot 135 degrees
        Macanum(0.0,0.0,1.0,900);
        sleep(1000);

        // Move backwards and intake
        Macanum(0.0,-1.0,0.0,500);
        sleep(2000);

        // Move forward and Turn 135 degrees
        Macanum(0.0,1.0,0.0,500);
        sleep(2000);
        Macanum(0.0,0.0,-1.0,900);
        sleep(1000); // however long it takes

        // Aim-bot
        aim_bot();

        // start Launch Motors
        LeftLaunch.setVelocity(2300);
        RightLaunch.setVelocity(2300);

        // Wait for Velocity
        while (RightLaunch.getVelocity() != 2300 || LeftLaunch.getVelocity() != 2300 && opModeIsActive()){
            sleep(100);
        }

        // Enable Conveyor
        Conveyor.setVelocity(500);

        // Out of zone
        sleep(5000);
        Macanum(1.0,0.0,0.0,2000);
        sleep(1000);
        Macanum(0.0,0.0,0.0,0);
        // no telemetry :P
    }
}
