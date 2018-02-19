/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6851.robot;

import java.util.ArrayList;

import org.usfirst.frc.team6851.robot.commands.claw.SetGrabberDown;
import org.usfirst.frc.team6851.robot.commands.claw.SetGrabberUp;
import org.usfirst.frc.team6851.robot.commands.claw.ThrowPowerCube;
import org.usfirst.frc.team6851.robot.commands.driving.ToggleDriveDirectionCommand;
import org.usfirst.frc.team6851.robot.commands.driving.ToggleSlowerMoveCommand;
import org.usfirst.frc.team6851.robot.utils.Extreme3DPro.Extreme3DProButton;
import org.usfirst.frc.team6851.robot.utils.Gamepad.GamepadAxis;
import org.usfirst.frc.team6851.robot.utils.Gamepad.GamepadButton;
import org.usfirst.frc.team6851.robot.utils.input.AxisInputBase;
import org.usfirst.frc.team6851.robot.utils.input.DualInputInput;
import org.usfirst.frc.team6851.robot.utils.input.JoystickInput;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	
	public Joystick joystick1;
	
	public AxisInputBase moveInput;
	public AxisInputBase rotateInput;
	public AxisInputBase screwHeightInput;
	
	public JoystickButton grabberLeftMotor;
	public JoystickButton grabberRightMotor;
	public JoystickButton grabberThrow;
	
	public boolean reverseDriveDirection = false;
	public double driveSpeedFactor = 0.6;
	
	private double t;
	private long lastUpdate;
	
	private ArrayList<JoystickButton> buttons = new ArrayList<>();
	
	public OI() {
		//initTestJoystick();
		joystick1 = new Joystick(0);

		initGamePad();
	}

	private void initGamePad() {
		rotateInput = new JoystickInput(joystick1, GamepadAxis.LeftX, 0.03);
		moveInput   = new DualInputInput( joystick1, GamepadAxis.LeftTrigger, GamepadAxis.RightTrigger, 0.08 );
		screwHeightInput = new JoystickInput(joystick1, GamepadAxis.RightY, 0.1, -0.75);
		
		getButton(GamepadButton.A).toggleWhenActive(new ToggleSlowerMoveCommand());
		getButton(GamepadButton.B).toggleWhenActive(new ToggleDriveDirectionCommand());
		//getButton(GamepadButton.Start).toggleWhenActive(new ToggleNavxNavigationCommand());
		getButton(GamepadButton.X).whenPressed(new SetGrabberDown(0.3));
		getButton(GamepadButton.Y).whenPressed(new SetGrabberUp(0.3));
		getButton(GamepadButton.Start).whenPressed(new ThrowPowerCube());
		
		grabberLeftMotor = getButton(GamepadButton.LB);
		grabberRightMotor = getButton(GamepadButton.RB);
		//grabberThrow = getButton(GamepadButton.Start);
		
		System.out.println("Switching to Gamepad");
	}
	
	JoystickButton getButton(Joystick joystick, int buttonValue) {
		JoystickButton b = new JoystickButton(joystick, buttonValue);
		buttons.add(b);
		return b;
	}

	JoystickButton getButton(GamepadButton button) {
		return getButton(joystick1, button.value());
	}
	JoystickButton getButton(Joystick joystick, GamepadButton button) {
		return getButton(joystick, button.value());
	}

	JoystickButton getButton(Extreme3DProButton button) {
		return getButton(joystick1, button.value());
	}
	JoystickButton getButton(Joystick joystick, Extreme3DProButton button) {
		return getButton(joystick, button.value());
	}
	
	private void UpdateTime() {
		t += System.nanoTime()-lastUpdate;
		if(t>1) t=1;
		lastUpdate = System.nanoTime();
	}
	
	public double getMoveSpeed() {
		double speed = moveInput.getInput();
		if(reverseDriveDirection)
			speed *=-1;
		return speed*driveSpeedFactor;
		
	}
	
	public double getTurnSpeed() {
		
		double speed = rotateInput.getInput();
		speed = Math.signum(speed) * speed * speed; // Control of the speed from a linear to a exponential curve.
		
		return speed*driveSpeedFactor;
	}

	public void update() {
		SmartDashboard.putNumber("joyX", joystick1.getX());
		SmartDashboard.putNumber("joyY", joystick1.getY());
		SmartDashboard.putNumber("moveX", moveInput.getInput());
		SmartDashboard.putNumber("moveY", rotateInput.getInput());
		
		/*if( ! currentDriveType.equals(Dashboard.DrivingStyle.getSelected()) ) {
			changeIoTo(Dashboard.DrivingStyle.getSelected());
		}*/
	}
}
