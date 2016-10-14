package DevicePackage;

import MessagePackage.Message;
import MessagePackage.MessageConstants;
import MessagePackage.MessageQueue;

public class FireSensor extends AbstractDevice {
	
	public FireSensor() {
		this.deviceConfig = new DeviceConfig(
				0.8f,0.6f,
				"Fire Sensor","A sensor to detect fire",
				"", "%",
				MessageConstants.HUMI_DATA,MessageConstants.HUMI_CONF,
				MessageConstants.HUMI_ON, MessageConstants.HUMI_OFF,
				MessageConstants.DEHUMI_ON, MessageConstants.DEHUMI_OFF
		);
	}
	
	public static void main(String[] args) {
		FireSensor sensor = new FireSensor();
		sensor.run();
	}
	
	@Override
	protected void run() {
		if (!initRegistrition()) {
			return;
		}
		
		Message msg = null;							// Message object
		MessageQueue msgQue = null;					// Message Queue

		final int Delay = 2500;						// The loop delay (2.5 seconds)
		boolean Done = false;						// Loop termination flag
		while (!Done) {
			try {
				msgQue = msgMgrIntf.GetMessageQueue();
			} catch(Exception e) {
				mw.WriteMessage("Error getting message queue::" + e);
			}

			int qlen = msgQue.GetSize();
			for (int i = 0; i < qlen; i++) {
				msg = msgQue.GetMessage();
				
				if (msg.GetMessageId() == MessageConstants.HEALTH_REQ) {
					mw.WriteMessage(" >>>> Received Health Checking message");
					healthCheckResponse(msgMgrIntf, msg.GetMessage());
				}
				
				if (msg.GetMessageId() == MessageConstants.DEVICE_STOP){
					Done = true;
					try {
						msgMgrIntf.UnRegister();
					} catch (Exception e) {
						mw.WriteMessage("Error unregistering: " + e);
					}
					mw.WriteMessage( "\n\nSimulation Stopped. \n");				
				}
			}
			try {
				Thread.sleep(Delay);
			} catch(Exception e) {
				mw.WriteMessage( "Sleep error:: " + e );
			}
		}	
	}
}
