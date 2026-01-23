package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;
import java.util.List;

@Disabled
@TeleOp(name="Macanum Odometry TeleOp", group="TeleOp")
public class Macanum_Odometry_TeleOp_depreciated extends OpMode {
    DcMotorEx FrontRightMotor, BackRightMotor, FrontLeftMotor, BackLeftMotor, Intake, LeftLaunch, RightLaunch;
    CRServo leftPulley, rightPulley, backLeftConveyor, backRightConveyor, frontLeftConveyor, frontRightConveyor;
    HuskyLens Camera;
    Servo LED;
    SparkFunOTOS Odometry;
    public enum State {
        DEFAULT,
        AIMING
    }
    State currentState = State.DEFAULT;
    double Rotation_Offset = 0;
    static double PulleyMax = 180, PulleyMin = 0;
    @Override
    public void init() {

        //Odometry = hardwareMap.get(SparkFunOTOS.class, "Odometry");
        //Odometry.initialize();
        //Odometry.calibrateImu();

        //Odometry.setAngularScalar(1);//.3660377);

        //Odometry.setLinearUnit(DistanceUnit.INCH);

        //Odometry.setPosition(new SparkFunOTOS.Pose2D(0,0,0));
        //Odometry.setOffset(new SparkFunOTOS.Pose2D(0.125,1.25,0));
        // 175.0?

        //LED.setPosition(0.277); // Sets the LED color to Red

        //Camera = hardwareMap.get(HuskyLens.class,"Huskylens");
        //Camera.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        FrontLeftMotor = hardwareMap.get(DcMotorEx.class,"frontLeft");
        BackLeftMotor = hardwareMap.get(DcMotorEx.class,"backLeft");
        FrontRightMotor = hardwareMap.get(DcMotorEx.class,"frontRight");
        BackRightMotor = hardwareMap.get(DcMotorEx.class,"backRight");
        //Intake = hardwareMap.get(DcMotorEx.class,"intake");
        //RightLaunch = hardwareMap.get(DcMotorEx.class, "InsertMotorNameHere");
        //LeftLaunch = hardwareMap.get(DcMotorEx.class, "InsertMotorNameHere");
        //leftPulley = hardwareMap.get(CRServo.class,"leftpulley");
        //rightPulley = hardwareMap.get(CRServo.class,"rightpulley");

        //backLeftConveyor = hardwareMap.get(CRServo.class,"InsertServoNameHere");
        //backRightConveyor = hardwareMap.get(CRServo.class,"InsertServoNameHere");
        //frontLeftConveyor = hardwareMap.get(CRServo.class,"InsertServoNameHere");
        //frontRightConveyor = hardwareMap.get(CRServo.class,"InsertServoNameHere");

        FrontRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackRightMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        FrontLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        BackLeftMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        //LeftLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //RightLaunch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        FrontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        BackRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        //RightLaunch.setDirection(DcMotorSimple.Direction.REVERSE);
        //backRightConveyor.setDirection(DcMotorSimple.Direction.REVERSE);
        //frontRightConveyor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public HuskyLens.Block getTag() {
        List<HuskyLens.Block> blocks = Arrays.asList(Camera.blocks());
        HuskyLens.Block targetBlock = null;
        for (HuskyLens.Block block : blocks) {
            if (block.id != 0) {
                if (targetBlock != null) {
                    if (block.width * block.height > targetBlock.width * targetBlock.height) { // Checks which AprilTag is the largest on the screen.
                        targetBlock = block; // Sets block as currentBlock
                    }
                } else { // If not currentBlock then set block as currentBlock.
                    targetBlock = block;
                }
            }
        }
        return targetBlock; // Returns The AprilTag Block
    }
    public void Telemetry() {
        telemetry.addLine("==== Controls ====");
        telemetry.addLine("Game Pad 1: (Driver) ==");
        telemetry.addLine("Left Stick: Macanum Drive");
        telemetry.addLine("Right Stick: Rotation");
        telemetry.addLine("Y (Triangle) Button: Toggle Aiming");
        telemetry.addLine("X (Square) Button: Recenter");
        telemetry.addLine("================");
        telemetry.addLine("Game Pad 2: (Attachments) ==");
        telemetry.addLine("Up/Down D-Pad: Manual Up/Down Aiming");
        telemetry.addLine("A (Cross) Button: Spit out");
        telemetry.addLine("B (Circle) Button: Launch");
        telemetry.addLine("==== Telemetry ====");
        //if (block != null) {
        //    telemetry.addLine("AprilTag 0"+block.id+":");
        //    telemetry.addLine("Size:"+block.height+","+block.width);
        //    telemetry.addLine("Position:"+block.x+","+block.y);
        //}
        telemetry.addData("State:", currentState);
        telemetry.addData("Position",Odometry.getPosition());
    }
    public void IntakeOuttake() {
        // Pulleys
        double addToPos = 0;
        if (gamepad2.dpad_down) {
            addToPos -= 1;
        }
        if (gamepad2.dpad_up) {
            addToPos += 1;
        }
        addToPos = clamp(addToPos,PulleyMin,PulleyMax);
        leftPulley.setPower(addToPos);
        rightPulley.setPower(addToPos);

        // Intake/Outtake
        if (gamepad2.a) {
            // Outtake
            backLeftConveyor.setPower(1);
            frontLeftConveyor.setPower(1);
            backRightConveyor.setPower(1);
            frontRightConveyor.setPower(1);
            Intake.setVelocity(1000);
        } else {
            // Intake
            Intake.setVelocity(-1000);
        }

        // Launching
        if (gamepad2.b) {
            backLeftConveyor.setPower(-1);
            frontLeftConveyor.setPower(-1);
            backRightConveyor.setPower(-1);
            frontRightConveyor.setPower(-1);
            RightLaunch.setVelocity(2000);
            LeftLaunch.setVelocity(2000);
        } else {
            backLeftConveyor.setPower(0);
            frontLeftConveyor.setPower(0);
            backRightConveyor.setPower(0);
            frontRightConveyor.setPower(0);
            RightLaunch.setVelocity(0);
            LeftLaunch.setVelocity(0);
        }
    }
    public void MacanumDrive(double x, double y, double r, double ad) {
        double Rot = (Odometry.getPosition().h + Rotation_Offset % 360 + 360) % 360;

        // Turns degrees to Radians
        double Radians = Rot * Math.PI/180;

        // Takes the x, y, and heading to get a vector so the robot can offset accordingly
        double newX = y * Math.sin(Radians) + x * Math.cos(Radians);
        double newY = x * Math.sin(Radians) - y * Math.cos(Radians);
        telemetry.addData("OffsetX",newX);
        telemetry.addData("OffsetY",newY);
        r += ad;

        double m = 1;

        if (gamepad1.left_bumper) {
            m = 0.5;
        }

        double d = Math.max(Math.abs(x)+Math.abs(y)+Math.abs(r),1)/m;

        double FTVelocity = (newX + -newY + r)/d * 2000; // Don't touch or it
        double BTVelocity = (newX - -newY + r)/d * 2000; // may NEVER work again...
        double FRVelocity = (newX - -newY - r)/d * 2000;
        double BRVelocity = (newX + -newY - r)/d * 2000;

        FrontLeftMotor.setVelocity(FTVelocity);
        BackLeftMotor.setVelocity(BTVelocity);
        FrontRightMotor.setVelocity(FRVelocity);
        BackRightMotor.setVelocity(BRVelocity);
    }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    @Override
    public void loop() {
        double x = gamepad1.left_stick_y;
        double y = -gamepad1.left_stick_x;
        double r = -gamepad1.right_stick_x;

        SparkFunOTOS.Pose2D currentVector = Odometry.getPosition();

        Odometry.setPosition(new SparkFunOTOS.Pose2D(currentVector.x,currentVector.y,currentVector.h)); // Corrects the Heading (Rotation) of the Odometry

        //HuskyLens.Block block = getTag(); // returns AprilTag Block

        double ad = 0; // what is added to the rotation for aim

        // The Logic for Aiming
        //if (block != null && block.id == 1 && gamepad1.y && currentState != State.AIMING) {
        //    currentState = State.AIMING; // Changes state to AIMING
        //} else {
        //    if (gamepad1.y || block == null || block.id != 1.0) {
        //        currentState = State.DEFAULT; // Changes state to DEFAULT
        //    } else {
        //        ad = clamp((double) block.x,-0.5,0.5); // Makes sure the ad is between -0.5 and 0.5
        //    }
        //}
        // Sets the Rotation Offset to the current when X is pressed on gamepad1
        if (gamepad1.x) {
            Rotation_Offset = Odometry.getPosition().h;
        }

        MacanumDrive(x,y,r,ad);
        //IntakeOuttake();
        Telemetry();
    }
}
