/*
 * Author: Ying Jin, Carnegie Mellon University
 * 
 * ??? Indicates a fire has been detected, then show alarm in the security console
 * */
package DevicePackage;

import InstrumentationPackage.Indicator;
import MessagePackage.Message;
import MessagePackage.MessageConstants;
import MessagePackage.MessageQueue;

public class FireAlarmController extends AbstractDevice{
	
	public FireAlarmController() {
		this.deviceConfig = new DeviceConfig(
				0.5f,0.0f,
				"Fire Alarm Controller","A controller to start or silent fire alarms",
				"","",
				MessageConstants.HUMI_CONF,MessageConstants.HUMI_CTRL,
				"Humidifier",MessageConstants.HUMI_ON,MessageConstants.HUMI_OFF,
				"Dehumidifier",MessageConstants.DEHUMI_ON,MessageConstants.DEHUMI_OFF
		);
	}
	
	public static void main(String[] args) {
		FireAlarmController controller = new FireAlarmController();
		controller.run();
	}
	
	@Override
	protected void run() {
		if (!initRegistrition()) {
			return;
		}
		Indicator indicator = new Indicator ("Fire Alarm" +
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
				
				if (msg.GetMessageId() == MessageConstants.FALARM_CTRL) {
					if (msg.GetMessage().equals(MessageConstants.ALARM_ON.toString())) {
						indicator.SetLampColorAndMessage("Fire Alarm ON: Alarming", 3);
						mw.WriteMessage("Received alarm on");
					} else {
						indicator.SetLampColorAndMessage("Fire Alarm OFF: Silent", 0);
						mw.WriteMessage("Received alarm off");
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