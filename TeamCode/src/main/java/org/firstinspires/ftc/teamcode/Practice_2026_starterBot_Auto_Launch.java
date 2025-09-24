package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous
public class Practice_2026_starterBot_Auto_Launch extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor LeftMotor;
        DcMotor RightMotor;
        DcMotorEx Launcher;
        Servo LeftFeeder;
        Servo RightFeeder;

        LeftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        RightMotor = hardwareMap.get(DcMotor.class, "rightMotor");
        Launcher = hardwareMap.get(DcMotorEx.class, "launchMotor");
        LeftFeeder = hardwareMap.get(Servo.class, "left_feeder");
        RightFeeder = hardwareMap.get(Servo.class, "right_feeder");

        Launcher.setZeroPowerBehavior(BRAKE);
        LeftFeeder.setDirection(Servo.Direction.REVERSE);
        RightMotor.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        Launcher.setPower(0.45);

        sleep(2000);

        for (int i = 1; i <= 3; i++) {

            LeftFeeder.setPosition(0.4);
            RightFeeder.setPosition(0.4);

            sleep(800);


            LeftFeeder.setPosition(0);
            RightFeeder.setPosition(0);

            sleep(500);
        }

        Launcher.setPower(0);

        RightMotor.setPower(-1);
        LeftMotor.setPower(-1);

        sleep(1000);

        RightMotor.setPower(0);
        LeftMotor.setPower(0);
    }
}