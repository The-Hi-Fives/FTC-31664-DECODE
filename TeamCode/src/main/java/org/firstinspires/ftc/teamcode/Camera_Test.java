package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.external.samples.UtilityCameraFrameCapture;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class Camera_Test extends OpMode {
    AprilTagProcessor LogiCam;
    VisionPortal VPortal;

    @Override
    public void init() {
        LogiCam = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();
        VPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class,"Logi 720p Webcam"),
                LogiCam
        );
    }

    @Override
    public void loop() {
        List<AprilTagDetection> myAprilTagDetections = LogiCam.getDetections();


        telemetry.addData("",0);

        telemetry.update();
    }
}
