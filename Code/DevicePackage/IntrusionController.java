/*
 * Author: Ying Jin, Carnegie Mellon University
 * 
 * This controller works with Instrusion Sensor
 * */
package DevicePackage;

import InstrumentationPackage.Indicator;
import MessagePackage.Message;
import MessagePackage.MessageConstants;
import MessagePackage.MessageQueue;

public class IntrusionController extends AbstractDevice{
	
	public IntrusionController() {
		this.deviceConfig = new DeviceConfig(
				0.5f,0.3f,
				"Instrusion Alarm Controller","A controller to start or silent intrusion alarms",
				"","%",
				MessageConstants.HUMI_CONF,MessageConstants.HUMI_CTRL,
				"Humidifier",MessageConstants.HUMI_ON,MessageConstants.HUMI_OFF,
				"Dehumidifier",MessageConstants.DEHUMI_ON,MessageConstants.DEHUMI_OFF
		);
	}
	
	public static void main(String[] args) {
		IntrusionController controller = new IntrusionController();
		controller.run();
	}
	
	@Override
	protected void run() {
		if (!initRegistrition()) {
			return;
		}
		Indicator indicator = new Indicator ("Instrusion Alarm" +
				" OFF", mw.GetX(), mw.GetY()+mw.Height());
		Message msg = null;							// Message object
		MessageQueue msgQue = null;					// Message Queue

		final int Delay = 100;						// The loop delay (2.5 seconds)
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
				
				if (msg.GetMessageId() == MessageConstants.SALARM_CTRL) {
					if (msg.GetMessage().equals(MessageConstants.ALARM_ON.toString())) {
						indicator.SetLampColorAndMessage("Instrusion Alarm ON: Alarming", 3);
					} else {
						indicator.SetLampColorAndMessage("Instrusion Alarm OFF: Silent", 0);
					}
				}
				
				if (msg.GetMessageId() == MessageConstants.DEVICE_STOP){
					Done = true;
					try {
						msgMgrIntf.UnRegister();
					} catch (Exception e) {
						mw.WriteMessage("Error unregistering: " + e);
					}
					mw.WriteMessage( "\n\nSimulation Stopped. \n");		
					if (deviceConfig.isController) {
						indicator.dispose();
					}						
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
