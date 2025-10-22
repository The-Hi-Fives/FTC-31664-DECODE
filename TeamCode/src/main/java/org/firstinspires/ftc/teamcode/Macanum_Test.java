package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class Macanum_Test extends OpMode {

    DcMotor FrontLeftMotor;
    DcMotor BackLeftMotor;
    DcMotor FrontRightMotor;
    DcMotor BackRightMotor;

    @Override
    public void init() {
        FrontLeftMotor = hardwareMap.get(DcMotor.class,"frontLeft");
        BackLeftMotor = hardwareMap.get(DcMotor.class,"backLeft");
        FrontRightMotor = hardwareMap.get(DcMotor.class,"frontRight");
        BackRightMotor = hardwareMap.get(DcMotor.class,"backRight");

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        double x = gamepad1.left_stick_y;
        double y = -gamepad1.left_stick_x;
        double r = gamepad1.right_stick_x;

        double m = 1;

        if (gamepad1.left_bumper) {
            m = 0.5;
        }

        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1)/m;

        FrontLeftMotor.setPower((x+y+r)/d);
        BackLeftMotor.setPower((x-y+r)/d);
        FrontRightMotor.setPower((x-y-r)/d);
        BackRightMotor.setPower((x+y-r)/d);
    }
}
