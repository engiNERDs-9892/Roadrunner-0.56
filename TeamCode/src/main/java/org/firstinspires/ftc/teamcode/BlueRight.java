package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;
/* TODO
   - turn off redFinder when no longer needed.
 */
@Config
@Autonomous(name = "BlueRight", group = "robot", preselectTeleOp = "FluffyTeleOp")
public class BlueRight extends LinearOpMode {
    //BRING GRABBER UP FIRST THING//
    AutoFluffy fluffy;
    String PATH;
    static double DELTA = 1;
    List<Recognition> currentRecognitions;
    public void runOpMode(){
        initialize();
        while(!isStarted() && !isStopRequested()){
            //currentRecognitions=fluffy.getRecognitions();
            /*if(gamepad1.x){
                PATH = "Left";
                telemetry.addData("TargetPosition: ", PATH);
            }
            if(gamepad1.y){
                PATH = "Center";
                telemetry.addData("TargetPosition: ", PATH);
            }
            if(gamepad1.b){
                PATH = "Right";
                telemetry.addData("TargetPosition: ", PATH);
            }*/
            //telemetry.addData("TargetPosition: ", PATH);
            // telemetry.update();
            PATH= fluffy.getPropLocation();
            telemetry.addData("Prop Location", PATH );
            telemetry.addData("Left Sat. Value", fluffy.getLeftMean());
            telemetry.addData("Center Sat. Value", fluffy.getCenterMean());
            telemetry.addData("Right Sat. Value", fluffy.getRightMean());

            telemetry.update();


        }

        // JRC: Turn off redFinder at this point.

        if(PATH .equals("Left")){
            deliverPurpleLeft();
            //yellowLeft();
        } else if(PATH .equals("Right")){
            deliverPurpleRight();
            //yellowRight();
        }else{
            deliverPurpleCenter();
            //yellowCenter();
        }

        //deliverYellow();
        //park();
        //RobotLog.i(String.format("Final position %f,%f",fluffy.drive.pose.position.x, fluffy.drive.pose.position.y));
    }


    public void initialize(){
        fluffy = new AutoFluffy(this, "Blue");
        fluffy.raiseGrabber();
    }

    public void deliverPurpleLeft(){
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        //.lineToX(20.5)
                        .strafeToLinearHeading( new Vector2d(25.4,0), Math.toRadians(0))
                        .strafeToLinearHeading( new Vector2d(28.4,19.5), Math.toRadians(89.9))
                        .build());
        fluffy.deliverPurple();

    }
    public void deliverPurpleCenter(){
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(new Vector2d(28.3, -0.4 ), Math.toRadians(89.9))
                        .strafeToLinearHeading(new Vector2d(37.3, -0.4 ), Math.toRadians(89.9))
                        .build());
        fluffy.deliverPurple();
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(new Vector2d(25.3, -0.4 ), Math.toRadians(89.9))
                        .build());
    }
    public void deliverPurpleRight(){
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(new Vector2d(30.8,1.5), Math.toRadians(0))
                        .build());
        fluffy.deliverPurple();
    }

    public void yellowLeft(){
        /* JRC: To read position, use
         * Don't forget to handle the null case gracefully!
         */
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(new Vector2d(34.4, -26), Math.toRadians(-89.9))
                        .build());
        fluffy.retractPurple();
    }
    public void yellowCenter(){
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeTo(new Vector2d(30, -.5))
                        .strafeToLinearHeading(new Vector2d(28.4, -26), Math.toRadians(-89.9))
                        .build());
        fluffy.retractPurple();
    }
    public void yellowRight(){
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(new Vector2d(18, -5), Math.toRadians(-89.9))
                        .strafeToLinearHeading(new Vector2d(22.4, -26), Math.toRadians(-89.9))
                        .build());
        fluffy.retractPurple();
    }


    public void deliverYellow(){
        fluffy.raiseLift();
        Pose2d destination = fluffy.correctYellowPositionBlue(PATH);
        RobotLog.i(String.format("Destination position: (%3.1f, %3.1f) at %3.1f deg",
                destination.position.x,
                destination.position.y,
                Math.toDegrees(destination.heading.toDouble())));
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(destination.position , destination.heading)
                        .build());
        fluffy.raiseFinger();
        sleep(500);
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(new Vector2d(fluffy.drive.pose.position.x, (fluffy.drive.pose.position.y - DELTA)),
                                Math.toRadians(-89.9))
                        .build());
        fluffy.lowerLift();

        /* HA plan 12/13:
         * raiseLift()
         * get detection  // JRC - see below
         * do math      // JRC - math method is fluffy.correctYellowPosition, gets detection within.
         * create trajectory and drive to it
         * raiseFinger()
         * back up
         * lowerLift()
         * NOTE: must park after delivering yellow.
         */

        /* JRC testing recommendation
         * write deliverYellow with raiseLift, correctYellowPosition, raiseFinger, grabberDown.  (ie, no driving).
         *    Test just that, and use telemetry to determine whether correctYellowPosition is working properly.
         * When satisfied, add driving.  Test final version.
         * (then park)
         */

    }
    public void park(){
        Actions.runBlocking(
                fluffy.drive.actionBuilder(fluffy.drive.pose)
                        .strafeToLinearHeading(new Vector2d(0, -32), Math.toRadians(-89.9))
                        .build());
    }


}
