package DevicePackage;
import InstrumentationPackage.*;
import MessagePackage.*;

public abstract class AbstractDevice {
	protected DeviceConfig deviceConfig;
	protected float metricValue;							// Current simulated metric value(like humidity)
	private String msgMgrIP = null;
	protected MessageManagerInterface msgMgrIntf = null;	// Interface object to the message manager
	protected MessageWindow mw = null;
	
	protected static final boolean ON  = true;
	protected static final boolean OFF  = false;

	// for initializing a sensor or a controller
	public AbstractDevice() { }
	public AbstractDevice(String msgMgrIP) {
		this.msgMgrIP = msgMgrIP;
	}
	
	// registration is the same for all devices
	protected boolean initRegistrition() {
		// message manager is on the local system
 		if (msgMgrIP == null) {
			System.out.println("\n\nAttempting to register on the local machine..." );
			try {
				msgMgrIntf = new MessageManagerInterface();
			} catch (Exception e) {
				System.out.println("Error instantiating message manager interface: " + e);
			}
		} else {
			System.out.println("\n\nAttempting to register on the machine:: " + msgMgrIP);
			try {
				msgMgrIntf = new MessageManagerInterface(msgMgrIP);
			} catch (Exception e) {
				System.out.println("Error instantiating message manager interface: " + e);
			}
		}
		// check registration
		if (msgMgrIntf != null) {
			System.out.println("Registered with the message manager." );
			mw = new MessageWindow(deviceConfig.deviceName + " Status Console", deviceConfig.winPosX, deviceConfig.winPosY);
	
			mw.WriteMessage("Registered with the message manager." );
	    	try {
				deviceConfig.deviceID = String.valueOf(msgMgrIntf.GetMyId()); 
				mw.WriteMessage("   Participant id: " + msgMgrIntf.GetMyId() );
				mw.WriteMessage("   Registration Time: " + msgMgrIntf.GetRegistrationTime() );
			} catch (Exception e) {
				System.out.println("Error:: " + e);
			}
	    	return true;
		} else {
			System.out.println("Unable to register with the message manager.\n\n" );
			return false;
		}
	}

	// main loop
	protected abstract void run();
	// inside main loop, used for send message back
	protected void ConfirmMessage(MessageManagerInterface ei, String m){
		Message msg = new Message(deviceConfig.sendMsgId, m);
		try {
			ei.SendMessage(msg);
		} catch (Exception e) {
			System.out.println("Error Confirming Message:: " + e);
		}
	}
	// response to health check
	protected void healthCheckResponse(MessageManagerInterface ei, String input) {
		// message format: HEALTH_RES | device_id;device_name;device_description;inputCalulated
		String inputDerived = String.valueOf(Integer.valueOf(input) + 20120608);
		String msgBody = deviceConfig.deviceID + ";" + deviceConfig.deviceName + ";"
							+ deviceConfig.deviceDesc + ";" + inputDerived;
		
		Message msg = new Message(MessageConstants.HEALTH_RES, msgBody);
		try {
			ei.SendMessage(msg);
		} catch (Exception e) {
			System.out.println("Error Confirming Message:: " + e);
		}
	}
}
