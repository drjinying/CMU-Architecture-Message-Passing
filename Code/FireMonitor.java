import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class FireMonitor extends Thread
{
	public static int status = 0;				// 0: no fire, 1: on fire, 2: disabled
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	Indicator indicator;						// Device Alive indicator
	int Delay = 2500;				// The loop delay
	public boolean sprinklerWaiting = false;
	
	public FireMonitor()
	{
		try{
			em = new MessageManagerInterface();
		}catch (Exception e){
			System.out.println("FireMonitor::Error instantiating message manager interface: " + e);
			Registered = false;
		} 
	}

	public FireMonitor( String MsgIpAddress )
	{
		MsgMgrIP = MsgIpAddress;
		try{
			em = new MessageManagerInterface( MsgMgrIP );
		}catch (Exception e){
			System.out.println("FireMonitor::Error instantiating message manager interface: " + e);
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
			mw = new MessageWindow("Fire Monitor Console", 200, 200);
			indicator = new Indicator ("Fire", mw.GetX()+ mw.Width(), 1);

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
					
					if ( Msg.GetMessageId() == MessageConstants.FIRE_DATA ){
						indicator.SetLampColorAndMessage("Fire Detected", 3);
						prepareSprinkler();
						Message msg = new Message(MessageConstants.FALARM_CTRL, MessageConstants.ALARM_ON);
						try {
							em.SendMessage(msg);
						} catch (Exception e) {
							e.printStackTrace();
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
	
	public void SendMsg(Message msg) {
		try{
			em.SendMessage( msg );
		}catch (Exception e){
		}
	}
	
	public void prepareSprinkler() {
		sprinklerWaiting = true;
		mw.WriteMessage("Sprinkler will be turned on in 10 seconds");
		final Message msg2 = new Message(MessageConstants.SPRK_CTRL, MessageConstants.SPRK_ON);
		new Thread() {
		    public void run() {
		    	try {
					Thread.sleep(10000);
					if (sprinklerWaiting) {
						em.SendMessage(msg2);
						mw.WriteMessage("Sprinkler is turning on");
					}
					sprinklerWaiting = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }  
		}.start();
	}
}