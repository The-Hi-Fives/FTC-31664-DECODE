package org.firstinspires.ftc.teamcode;

import java.lang.Math;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

@TeleOp

public class Practice_2026_starterBot extends OpMode {

    DcMotor LeftMotor;
    DcMotor RightMotor;
    DcMotorEx Launcher;
    Servo LeftFeeder;
    Servo RightFeeder;

    @Override
    public void init() {
        LeftMotor = hardwareMap.get(DcMotor.class,"leftMotor");
        RightMotor = hardwareMap.get(DcMotor.class,"rightMotor");
        Launcher = hardwareMap.get(DcMotorEx.class,"launchMotor");
        LeftFeeder = hardwareMap.get(Servo.class,"left_feeder");
        RightFeeder = hardwareMap.get(Servo.class,"right_feeder");
        Launcher.setZeroPowerBehavior(BRAKE);
    }

    @Override
    public void loop() {
        //Test
        if (gamepad1.right_trigger > 0.6) {
            LeftFeeder.setPosition(300);
            RightFeeder.setPosition(300);
            Launcher.setPower(0.5);
        } else {
            LeftFeeder.setPosition(0);
            RightFeeder.setPosition(0);
            Launcher.setPower(0);
        }
    }
}
