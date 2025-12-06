package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous
public class Motor_Test extends LinearOpMode {
    CRServo topConveyor;
    @Override
    public void runOpMode() throws InterruptedException {
        topConveyor = hardwareMap.get(CRServo.class,"topC");
        topConveyor.setPower(1);

    }

}
