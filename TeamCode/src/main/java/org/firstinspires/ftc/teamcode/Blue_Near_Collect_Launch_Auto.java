package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import javax.crypto.Mac;

@Autonomous(name="Blue Near Collect Launch Auto", group="Default")
public class Blue_Near_Collect_Launch_Auto extends LinearOpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake, LeftLaunch, RightLaunch;
    CRServo backLeftConveyor, backRightConveyor, frontLeftConveyor, frontRightConveyor, topConveyor;

    public void Macanum(Double x,Double y,Double r,Integer Speed) {
        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1);

        double FTVelocity = (x + y + r)/d * Speed; // Don't touch or it
        double BTVelocity = (x - y + r)/d * Speed; // may NEVER work again...
        double FRVelocity = (x - y - r)/d * Speed;
        double BRVelocity = (x + y - r)/d * Speed;

        FrontLeftMotor.setVelocity(FTVelocity);
        BackLeftMotor.setVelocity(BTVelocity);
        FrontRightMotor.setVelocity(FRVelocity);
        BackRightMotor.setVelocity(BRVelocity);
    }
    @Override
    public void runOpMode() throws InterruptedException {
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

        FrontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        // Sequence
        Intake.setVelocity(-1872);
        Macanum(1.0,0.0,0.0,2000);
        sleep(1000);
        Macanum(0.0,0.0,0.0,0);

        LeftLaunch.setVelocity(2300);
        RightLaunch.setVelocity(2300);

        // Wait for Velocity
        while (RightLaunch.getVelocity() < 2250 || LeftLaunch.getVelocity() < 2250){
            sleep(100);
        }

        // Enable Conveyors
        topConveyor.setPower(1);
        backLeftConveyor.setPower(1);
        frontLeftConveyor.setPower(1);
        backRightConveyor.setPower(1);
        frontRightConveyor.setPower(1);


        Macanum(0.0,0.0,1.0,900);
        sleep(1000);
        Macanum(1.0,0.0,0.0,2000);
        sleep(1000);
        Macanum(-1.0,0.0,0.0,2000);
        sleep(1000);
        Macanum(0.0,0.0,-1.0,900);

        // Out of zone
        sleep(5000);
        Macanum(0.0,1.0,0.0,2000);
        sleep(500);
        Macanum(1.0,0.0,0.0,2000);
        sleep(750);
        Macanum(0.0,0.0,1.0,900);
        sleep(2000);
        Macanum(-1.0,0.0,0.0,2000);
        sleep(500);
        Macanum(0.0,0.0,0.0,0);

        // no telemetry

    }
}
