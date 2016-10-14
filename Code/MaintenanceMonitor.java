import InstrumentationPackage.*;
import MessagePackage.*;
import java.util.*;

class MaintenanceMonitor extends Thread
{
	public static HashMap<String, HealthCheckRecord> healthRecord = new HashMap<String, HealthCheckRecord>();
	
	private MessageManagerInterface em = null;	// Interface object to the message manager
	private String MsgMgrIP = null;				// Message Manager IP address
	boolean Registered = true;					// Signifies that this class is registered with an message manager.
	MessageWindow mw = null;					// This is the message window
	Indicator indicator;						// Device Alive indicator
	public static int Delay = 3000;							// The loop delay (1 second)
	int MaxHealthTolerance = 3;					// do not response continuously 3 times, regarded as dead
	int seed = 132452345;						// simulate a input seed to check that the health response are not faked
	
	class HealthCheckRecord {
		String deviceName;
		String deviceDesc;
		int inresponseCnt = 0;
	}
	
	public MaintenanceMonitor()
	{
		try{
			em = new MessageManagerInterface();
		}catch (Exception e){
			System.out.println("MaintenanceMonitor::Error instantiating message manager interface: " + e);
			Registered = false;
		} 
	}

	public MaintenanceMonitor( String MsgIpAddress )
	{
		MsgMgrIP = MsgIpAddress;
		try{
			em = new MessageManagerInterface( MsgMgrIP );
		}catch (Exception e){
			System.out.println("MaintenanceMonitor::Error instantiating message manager interface: " + e);
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
			mw = new MessageWindow("Maintenance Monitor", 200, 20);
			indicator = new Indicator ("Devices", mw.GetX()+ mw.Width(), 1);

			mw.WriteMessage( "Registered with the message manager." );

	    	try	{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );
			}catch (Exception e){
				System.out.println("Error:: " + e);
			}

			while ( !Done )	
			{
				mw.WriteMessage("Sending health check ping request");
				Message ping = new Message(MessageConstants.HEALTH_REQ, String.valueOf(seed));
				try {
					em.SendMessage(ping);
				} catch (Exception e) {
					mw.WriteMessage("Sending health checking ping Failed!");
					continue;
				}
				
				try{
					Thread.sleep( Delay );
				} catch( Exception e ){
					System.out.println( "Sleep error:: " + e );
				}
				
				try{
					eq = em.GetMessageQueue();
				} catch( Exception e ){
					mw.WriteMessage("Error getting message queue::" + e );
				} 

				for (String id: healthRecord.keySet()) {
					HealthCheckRecord record = healthRecord.get(id);
					record.inresponseCnt++;
					healthRecord.put(id, record);
				}
				
				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ ){
					Msg = eq.GetMessage();
					if ( Msg.GetMessageId() == MessageConstants.HEALTH_RES ) {
						try	{
							String body = Msg.GetMessage();
							String idInBody = body.split(";")[0];
							String deviceName = body.split(";")[1];
							String deviceDesc = body.split(";")[2];
							int seedDerived = Integer.parseInt(body.split(";")[3]);
							
							String id = String.valueOf(Msg.GetSenderId());
							if (healthRecord.containsKey(id)) {
								if (seedDerived == seed + 20120608) {
									HealthCheckRecord record = new HealthCheckRecord();
									record.deviceName = deviceName;
									record.deviceDesc = deviceDesc;
									healthRecord.put(id, record);
								} else {
									mw.WriteMessage("Device " + id + " " + deviceName + 
												" responded incorrectly to the seed");
								}
							} else {
								mw.WriteMessage("New Device: " + id + " name: " + deviceName);
								HealthCheckRecord record = new HealthCheckRecord();
								record.deviceName = deviceName;
								record.deviceDesc = deviceDesc;
								healthRecord.put(id, record);
							}
						}catch( Exception e ){
							mw.WriteMessage("Error reading health check response: " + e);
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
				
				boolean allHealthy = true;
				for (String id: healthRecord.keySet()) {
					HealthCheckRecord record = healthRecord.get(id);
					if (record.inresponseCnt > 0){
						if (record.inresponseCnt >= MaxHealthTolerance){
							mw.WriteMessage("Device " + id + " " + record.deviceName + " DEAD!");
							allHealthy = false;
						} else {
							mw.WriteMessage("Device " + id + " " + record.deviceName + " Needs checking again");
						}
					} else {
						mw.WriteMessage("Device " + id + " " + record.deviceName + " healthy");
					}
				}
				if (allHealthy) {
					indicator.SetLampColorAndMessage("System Heathy", 1);
				} else {
					indicator.SetLampColorAndMessage("System Unhealthy", 3);
				}
			}
		} else {
			System.out.println("Unable to register with the message manager.\n\n" );
		}
	}

	public boolean IsRegistered(){
		return( Registered );
	}
	
	public static void setDelay(int mseconds) {
		if (mseconds >= 3000) {
			Delay = mseconds;
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