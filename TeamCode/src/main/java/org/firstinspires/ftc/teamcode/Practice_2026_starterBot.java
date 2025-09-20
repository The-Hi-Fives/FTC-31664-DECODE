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
        LeftFeeder.setDirection(Servo.Direction.REVERSE);
        RightMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        //Test
        if (gamepad1.a) {
            Launcher.setPower(0.45);
        } else {
            Launcher.setPower(0);
        }
        if (gamepad1.right_bumper) {
            LeftFeeder.setPosition(1);
            RightFeeder.setPosition(1);
        } else {
            LeftFeeder.setPosition(0);
            RightFeeder.setPosition(0);
        }

        // Get game pad input
        double drive = -gamepad1.left_stick_y; // Throttle (forward/backward)
        double turn = gamepad1.right_stick_x; // Steering (left/right)

        // Calculate motor powers for arcade drive
        double leftPower = drive + turn;
        double rightPower = drive - turn;

        // Scale motor powers to prevent exceeding 1.0 (if necessary)
        double maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (maxPower > 1.0) {
            leftPower /= maxPower;
            rightPower /= maxPower;
        }

        // Set motor powers
        LeftMotor.setPower(leftPower);
        RightMotor.setPower(rightPower);
            // Optional: Add telemetry for debugging
            //            telemetry.addData("Left Motor Power", leftPower);
            //            telemetry.addData("Right Motor Power", rightPower);
            //            ]telemetry.update();
            //        }


    }
}
