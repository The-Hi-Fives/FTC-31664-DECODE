package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "Launch Auto", group = "Autos")
public class Practice_2026_starterBot_Auto_Launch extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor LeftMotor, RightMotor;
        DcMotorEx Launcher;
        CRServo LeftFeeder, RightFeeder;

        LeftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        RightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        Launcher = hardwareMap.get(DcMotorEx.class, "launchMotor");
        LeftFeeder = hardwareMap.get(CRServo.class, "left_feeder");
        RightFeeder = hardwareMap.get(CRServo.class, "right_feeder");

        Launcher.setZeroPowerBehavior(BRAKE);
        Launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RightFeeder.setDirection(CRServo.Direction.REVERSE);
        RightMotor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        Launcher.setVelocity(1000);

        sleep(1000);

        for (int i = 1; i <= 3; i++) {

            LeftFeeder.setPower(-1);
            RightFeeder.setPower(-1);

            sleep(500);


            LeftFeeder.setPower(0.5);
            RightFeeder.setPower(0.5);

            sleep(1000);
        }

        Launcher.setVelocity(0);

        RightMotor.setPower(1);
        LeftMotor.setPower(1);

        sleep(1000);

        RightMotor.setPower(0);
        LeftMotor.setPower(0);
    }
}