package org.firstinspires.ftc.teamcode;

import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp
public class Cam_Shall_Be_Tested extends LinearOpMode {
    AprilTagProcessor ATP;
    VisionPortal LogiWebcam;
    @Override
    public void runOpMode() {

        ATP = new AprilTagProcessor.Builder()
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder()
                .setCameraResolution(new Size(640, 480));


        builder.setCamera(hardwareMap.get(WebcamName.class, "Logi 720p Webcam"));


        builder.addProcessor(ATP);

        LogiWebcam = builder.build();

        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                telemetry();
                sleep(50);
            }
        }

        LogiWebcam.close();
    }

    private void telemetry() {

        List<AprilTagDetection> currentDetections = ATP.getDetections();

        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addData("Id", detection.id);
            } else {
                telemetry.addData("Id", "Nope");
            }
        }
        telemetry.update();
    }
}

