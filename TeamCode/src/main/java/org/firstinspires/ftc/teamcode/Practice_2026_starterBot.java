package org.firstinspires.ftc.teamcode;

import java.lang.Math;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

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
    }

    @Override
    public void loop() {

    }
}
