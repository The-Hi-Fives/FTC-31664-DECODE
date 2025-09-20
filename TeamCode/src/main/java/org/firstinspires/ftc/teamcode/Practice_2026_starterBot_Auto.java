package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class Practice_2026_starterBot_Auto  extends LinearOpMode {
    DcMotor LeftMotor;
    DcMotor RightMotor;
    DcMotorEx Launcher;
    Servo LeftFeeder;
    Servo RightFeeder;

    @Override
    public void runOpMode() throws InterruptedException {
        LeftMotor = hardwareMap.get(DcMotor.class,"leftMotor");
        RightMotor = hardwareMap.get(DcMotor.class,"rightMotor");
        Launcher = hardwareMap.get(DcMotorEx.class,"launchMotor");
        LeftFeeder = hardwareMap.get(Servo.class,"left_feeder");
        RightFeeder = hardwareMap.get(Servo.class,"right_feeder");

        Launcher.setZeroPowerBehavior(BRAKE);
        LeftFeeder.setDirection(Servo.Direction.REVERSE);
        RightMotor.setDirection(DcMotor.Direction.REVERSE);

        Launcher.setPower(0.5);

        sleep(2000);

        for (int i = 1; i <= 3; i++) {

            LeftFeeder.setPosition(0.2);
            RightFeeder.setPosition(0.2);

            sleep(500);

            LeftFeeder.setPosition(0);
            RightFeeder.setPosition(0);
        }


        LeftMotor.setPower(-1);
        RightMotor.setPower(-1);

        sleep(2000);

    }
}
