package org.firstinspires.ftc.teamcode;

import java.lang.Math;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

@TeleOp(name = "Main TeleOp", group = "TeleOp")
public class Practice_2026_starterBot extends OpMode {

    DcMotor LeftMotor, RightMotor;
    DcMotorEx Launcher;
    CRServo LeftFeeder, RightFeeder;

    @Override
    public void init() {
        LeftMotor = hardwareMap.get(DcMotor.class,"leftMotor");
        RightMotor = hardwareMap.get(DcMotor.class,"rightMotor");
        Launcher = hardwareMap.get(DcMotorEx.class,"launchMotor");
        LeftFeeder = hardwareMap.get(CRServo.class,"left_feeder");
        RightFeeder = hardwareMap.get(CRServo.class,"right_feeder");

        Launcher.setZeroPowerBehavior(BRAKE);
        Launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RightFeeder.setDirection(CRServo.Direction.REVERSE);
        LeftMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        //Test
        if (gamepad2.a) {
            Launcher.setVelocity(1000);
        } else {
            Launcher.setVelocity(0);
        }
        if (gamepad2.right_bumper) {
            LeftFeeder.setPower(1);
            RightFeeder.setPower(1);
        } else if (gamepad2.left_bumper) {
            LeftFeeder.setPower(-1);
            RightFeeder.setPower(-1);
            Launcher.setVelocity(-360);
        } else {
            LeftFeeder.setPower(0);
            RightFeeder.setPower(0);
        }

        double Speed = 1; // 0-1

        if (gamepad1.left_trigger > 0) {
            Speed -= gamepad1.left_trigger/2;
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
        LeftMotor.setPower(leftPower*Speed);
        RightMotor.setPower(rightPower*Speed);
            // Optional: Add telemetry for debugging
            //            telemetry.addData("Left Motor Power", leftPower);
            //            telemetry.addData("Right Motor Power", rightPower);
            //            ]telemetry.update();
            //        }


    }
}
