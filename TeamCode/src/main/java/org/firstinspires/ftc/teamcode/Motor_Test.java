package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class Motor_Test extends LinearOpMode {

    DcMotor FrontLeftMotor;
    DcMotor BackLeftMotor;
    DcMotor FrontRightMotor;
    DcMotor BackRightMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        FrontLeftMotor = hardwareMap.get(DcMotor.class,"frontleft");
        BackLeftMotor = hardwareMap.get(DcMotor.class,"backleft");
        FrontRightMotor = hardwareMap.get(DcMotor.class,"frontright");
        BackRightMotor = hardwareMap.get(DcMotor.class,"backright");

        FrontLeftMotor.setPower(1);

    }

}
