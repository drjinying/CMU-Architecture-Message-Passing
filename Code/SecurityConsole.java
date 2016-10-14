/*
 * Author: Ying Jin, Carnegie Mellon University
 * 
 * list all devices
 * detect not responding devices and notify
 * */
import TermioPackage.*;
import MessagePackage.*;

public class SecurityConsole
{
	public static void main(String args[]){
    	Termio UserInput = new Termio();	// Termio IO Object
		boolean Done = false;				// Main loop flag
		String Option = null;				// Menu choice from user
		Message Msg = null;					// Message object
		SecurityMonitor securityMonitor = null;	
		FireMonitor fireMonitor = null;
		
		
 		if ( args.length != 0 ){
 			securityMonitor = new SecurityMonitor(args[0]);
 			fireMonitor = new FireMonitor(args[0]);
		} else {
			securityMonitor = new SecurityMonitor();
			fireMonitor = new FireMonitor();
		}

		if (securityMonitor.IsRegistered() && fireMonitor.IsRegistered())
		{
			securityMonitor.start();
			fireMonitor.start();
			
			while (!Done)
			{
				System.out.println( "\n---------------------" );
				System.out.println( "Intrusion & Fire Detection and Monitoring \n" );

				if (args.length != 0)
					System.out.println( "Using message manger at: " + args[0] + "\n" );
				else
					System.out.println( "Using local message manger \n" );
				
				System.out.println("\nPossible commands:");
				System.out.println( "IA: Instrusion: Arming" );
				System.out.println( "ID: Instrusion: Disarming" );
				System.out.println( "IC: Instrusion: Clear Alarm (reset to no instrusion & armed)" );
				System.out.println( "FA: Fire: Silent alarm" );
				System.out.println( "FS1: Fire: Confirm sprinkler action" );
				System.out.println( "FS0: Fire: Cancel sprinkler action" );
				System.out.println( "FC: Fire: Clear alarm (reset to no fire)" );
				System.out.println( "X: Stop System\n" );
				System.out.println( "0: Show this menu" );
				System.out.print( "\n>>>> " );
				
				Option = UserInput.KeyboardReadString();

				if ( Option.equals( "IA" ) ){
					securityMonitor.SetArmed(true);
				}
				if ( Option.equals( "ID" ) ){
					securityMonitor.SetArmed(false);
				}
				if ( Option.equals( "IC" ) ){
					securityMonitor.SetArmed(false);
					securityMonitor.SetArmed(true);
					securityMonitor.indicator.SetLampColorAndMessage("System Safe", 1);
				}
				
				if ( Option.equals( "FA" ) ){
					Message msg = new Message(MessageConstants.FALARM_CTRL, MessageConstants.ALARM_OFF);
					fireMonitor.SendMsg(msg);
				}
				if ( Option.equals( "FS1" ) ){
					Message msg = new Message(MessageConstants.SPRK_CTRL, MessageConstants.SPRK_ON);
					fireMonitor.SendMsg(msg);
				}
				if ( Option.equals( "FS0" ) ){
					fireMonitor.sprinklerWaiting = false;
					Message msg = new Message(MessageConstants.SPRK_CTRL, MessageConstants.SPRK_OFF);
					fireMonitor.SendMsg(msg);
				}
				if ( Option.equals( "FC" ) ){
					fireMonitor.sprinklerWaiting = false;
					Message msg = new Message(MessageConstants.SPRK_CTRL, MessageConstants.SPRK_OFF);
					fireMonitor.SendMsg(msg);
					msg = new Message(MessageConstants.FALARM_CTRL, MessageConstants.ALARM_OFF);
					fireMonitor.SendMsg(msg);
					fireMonitor.indicator.SetLampColorAndMessage("Fire Not Detected", 1);
				}
				if (Option.equalsIgnoreCase("X")){ 
					securityMonitor.Halt();
					fireMonitor.Halt();
					Done = true;
					System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
					securityMonitor.Halt();
					fireMonitor.Halt();
				}
			}
		} else {
			System.out.println("\n\nUnable start all the monitors.\n\n" );
		}
  	}
}