/*
 * Author: Ying Jin, Carnegie Mellon University
 * 
 * Works with Instrusion Sensor and Controller
 * Allows alarm and disalarm
 * */

import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class SecurityMonitor extends Thread
{
	private static boolean armed = true;
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	public Indicator indicator;						// Device Alive indicator
	public static int Delay = 2500;				

	public SecurityMonitor()
	{
		try{
			em = new MessageManagerInterface();
		}catch (Exception e){
			System.out.println("SecurityMonitor::Error instantiating message manager interface: " + e);
			Registered = false;
		} 
	}

	public SecurityMonitor( String MsgIpAddress )
	{
		MsgMgrIP = MsgIpAddress;
		try{
			em = new MessageManagerInterface( MsgMgrIP );
		}catch (Exception e){
			System.out.println("SecurityMonitor::Error instantiating message manager interface: " + e);
			Registered = false;
		}
	}

	public void run()
	{
		Message Msg = null;				// Message object
		MessageQueue eq = null;			// Message Queue
		int MsgId = 0;					// User specified message ID
		boolean Done = false;			// Loop termination flag
		
		if (em != null)
		{
			mw = new MessageWindow("Security Monitor", 400, 400);
			indicator = new Indicator ("Intrusion", mw.GetX()+ mw.Width(), 1);
			indicator.SetLampColorAndMessage("Instrusion: Safe", 1);
			mw.WriteMessage( "Registered with the message manager." );

	    	try	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );
			}catch (Exception e){
				System.out.println("Error:: " + e);
			}

			while ( !Done )	
			{
				try{
					eq = em.GetMessageQueue();
				} catch( Exception e ){
					mw.WriteMessage("Error getting message queue::" + e );
				} 
				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )
				{
					Msg = eq.GetMessage();
					if (!armed) {
						indicator.SetLampColorAndMessage("Security Disarmed", 2);
					}else if ( Msg.GetMessageId() == MessageConstants.ALARM_DATA ){
						Message msg = new Message(MessageConstants.SALARM_CTRL, MessageConstants.ALARM_ON);
						try{
							em.SendMessage( msg );
						}catch (Exception e){
							System.out.println("Error sending message:: " + e);
						}
						if (Msg.GetMessage().equals(MessageConstants.ALARM_DOOR.toString())) {
							indicator.SetLampColorAndMessage("Intrusion Detected", 3);
							mw.WriteMessage("ALARM: Door break!");
						} else if (Msg.GetMessage().equals(MessageConstants.ALARM_WINDOW.toString())) {
							indicator.SetLampColorAndMessage("Intrusion Detected", 3);
							mw.WriteMessage("ALARM: Window break!");
						} else if (Msg.GetMessage().equals(MessageConstants.ALARM_MOTION.toString())) {
							indicator.SetLampColorAndMessage("Intrusion Detected", 3);
							mw.WriteMessage("ALARM: Motion detected!");
						}
					}
					
					if ( Msg.GetMessageId() == MessageConstants.DEVICE_STOP ){
						Done = true;
						try{
							em.UnRegister();
				    	}catch (Exception e){
							mw.WriteMessage("Error unregistering: " + e);
				    	}
				    	mw.WriteMessage( "\n\nSimulation Stopped. \n");

						indicator.dispose();
					}
				}
			}
			try{
				Thread.sleep( Delay );
			} catch( Exception e ){
				System.out.println( "Sleep error:: " + e );
			}
		} else {
			System.out.println("Unable to register with the message manager.\n\n" );
		}
	}

	public boolean IsRegistered(){
		return( Registered );
	}
	
	public void SetArmed(boolean arm) {
		if (!armed && arm) {
			indicator.SetLampColorAndMessage("Instrusion Safe", 1);
		}
		armed = arm;
		mw.WriteMessage("Instrusion " + (arm ? "Safe" : "Disarmed"));
		if (!armed) {
			Message msg = new Message(MessageConstants.SALARM_CTRL, MessageConstants.ALARM_OFF);
			try{
				em.SendMessage( msg );
			}catch (Exception e){
				System.out.println("Error sending halt message:: " + e);
			}
		}
	}
	public void Halt(){
		mw.WriteMessage( "***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***" );
		Message msg;
		msg = new Message( MessageConstants.DEVICE_STOP, "XXX" );
		try{
			em.SendMessage( msg );
		}catch (Exception e){
			System.out.println("Error sending halt message:: " + e);
		}
	}
}