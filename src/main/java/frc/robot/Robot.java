/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private final String kDefaultAuto = "Default";
  private final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //CONSTANTS
  private final int leftMotorFrontID = 3;
  private final int leftMotorRearID = 4;
  private final int rightMotorFrontID = 1;
  private final int rightMotorRearID = 2;
  private final int intakeMotorID = 5;
  private final int ballStopMotorID = 6;
  private final int xboxControllerPort = 0;
  private final int launcherMotorLeftID = 0;
  private final int launcherMotorRightID = 1;

  //VARIABLES
  private int direction = -1;

  //CONTROLLER OBJECTS
  private final XboxController xboxController = new XboxController(xboxControllerPort);
  private final Hand intakeHand = Hand.kLeft;
  private final Hand intakeReverseHand = Hand.kRight;

  //MOTOR CONTROLLER OBJECTS
  private final WPI_VictorSPX leftMotorFront = new WPI_VictorSPX(leftMotorFrontID);
  private final WPI_VictorSPX leftMotorRear = new WPI_VictorSPX(leftMotorRearID);
  private final WPI_VictorSPX rightMotorFront = new WPI_VictorSPX(rightMotorFrontID);
  private final WPI_VictorSPX rightMotorRear = new WPI_VictorSPX(rightMotorRearID);
  private final SpeedController intakeMotor = new CANSparkMax(intakeMotorID, MotorType.kBrushless);
  private final SpeedController ballStopMotor = new CANSparkMax(ballStopMotorID, MotorType.kBrushed);
  private final Spark launcherMotorLeft = new Spark(launcherMotorLeftID);
  private final Spark launcherMotorRight = new Spark(launcherMotorRightID);

  //MOTOR CONTROLLER GROUPS
  private final SpeedControllerGroup leftSideMotors = new SpeedControllerGroup(leftMotorFront, leftMotorRear);
  private final SpeedControllerGroup rightSideMotors = new SpeedControllerGroup(rightMotorFront, rightMotorRear);

  //MISCELLANEOUS
  private final DifferentialDrive robotDrive = new DifferentialDrive(leftSideMotors, rightSideMotors);
  private final Timer timer = new Timer();
  private int counterStopper = 0;

  //GLOBAL VARIABLE startTime? so that all methods can use
  //private double startTime;
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //CAMERA
    CameraServer.getInstance().startAutomaticCapture(0);
    CameraServer.getInstance().startAutomaticCapture(1);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    //AUTONOMOUS TIMER USE
    timer.reset();
    timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

    //MOVE FORWARD
    if (timer.get() < 2.0) {
      robotDrive.arcadeDrive(0.5, 0);
    }
    else {
      robotDrive.stopMotor();
    }

/*  switch (m_autoSelected) {
    case kCustomAuto:
      //Put custom auto code here
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      break; */

    //controlling motor controllers
    //spin in circles (startTime = when autonomous mode begins))

  /**
   * This function is called periodically during operator control.
   */
  }

  @Override
  public void teleopPeriodic() {

    //DRIVE
    if(xboxController.getYButtonPressed()){
      direction *= -1;
    }

    double driveYAxis;
    double driveXAxis;
    driveYAxis = xboxController.getY(Hand.kLeft);
    driveXAxis = xboxController.getX(Hand.kRight);
    robotDrive.arcadeDrive(Math.abs(driveYAxis)*driveYAxis*direction, Math.abs(driveXAxis)*driveXAxis);
  
    //INTAKE
    double intakeRun;
    if (xboxController.getBumper(intakeHand)) {
      intakeRun = 0.3;
    }
    else if (xboxController.getBumper(intakeReverseHand)){
      intakeRun = -0.2; 
    }
    else {
      intakeRun = 0;
    }
    intakeMotor.set(intakeRun);

    //ROTATION
    if (xboxController.getBButton()){
      ballStopMotor.set(0.3);
    }

    //LAUNCHER
    if (xboxController.getXButton()){
      launcherMotorLeft.set(1.0);
      launcherMotorRight.set(-1.0);
    //  ballStopMotor.set(0.2);
      counterStopper++;

      if (counterStopper > 50) {
        ballStopMotor.set(-1.0);
      }
    }
    else {
      launcherMotorLeft.set(0);
      launcherMotorRight.set(0);
      counterStopper = 0;
    }
    if (xboxController.getXButton()==false && xboxController.getBButton()==false){
      ballStopMotor.set(0.0);
    }
  }

  @Override
  public void testPeriodic() {
  }
}
//git test