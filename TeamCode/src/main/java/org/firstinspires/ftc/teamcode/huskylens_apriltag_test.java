package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Arrays;
import java.util.List;


public class huskylens_apriltag_test extends OpMode {
    HuskyLens Camera;

    @Override
    public void init() {
        Camera = hardwareMap.get(HuskyLens.class,"HuskyLens");

        Camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);


    }

    @Override
    public void loop() {
        List<HuskyLens.Block> blocks = Arrays.asList(Camera.blocks());
        for (HuskyLens.Block block : blocks) {
            telemetry.addData("AprilTag ID:",block.id);
            telemetry.addData("AprilTag 1 detected at X:", block.x);
            telemetry.addData("AprilTag 1 detected at Y:", block.y);
        }
        telemetry.update();
    }
}
