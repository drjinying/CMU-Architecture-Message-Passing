/*
 * Author: Ying Jin, Carnegie Mellon University
 * 
 * confirm or cancel sprinkler action
 * Automatically turning on sprinkler after 10s
 * use can turn off when on
 * */
package DevicePackage;

import InstrumentationPackage.Indicator;
import MessagePackage.Message;
import MessagePackage.MessageConstants;
import MessagePackage.MessageQueue;

public class SprinklerController extends AbstractDevice{

	public SprinklerController() {
		this.deviceConfig = new DeviceConfig(
				0.5f,0.6f,
				"Sprinkler Controller","A controller to start or stop the sprinklers",
				"","%",
				MessageConstants.HUMI_CONF,MessageConstants.HUMI_CTRL,
				"Humidifier",MessageConstants.HUMI_ON,MessageConstants.HUMI_OFF,
				"Dehumidifier",MessageConstants.DEHUMI_ON,MessageConstants.DEHUMI_OFF
		);
	}
	
	public static void main(String[] args) {
		SprinklerController controller = new SprinklerController();
		controller.run();
	}
	
	@Override
	protected void run() {
		if (!initRegistrition()) {
			return;
		}
		Indicator indicator = new Indicator ("Sprinkler" +
				" OFF", mw.GetX(), mw.GetY()+mw.Height());
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
				
				if (msg.GetMessageId() == MessageConstants.SPRK_CTRL) {
					if (msg.GetMessage().equals(MessageConstants.SPRK_ON.toString())) {
						indicator.SetLampColorAndMessage("Sprinkler ON", 1);
						mw.WriteMessage("turned on");
					} else {
						indicator.SetLampColorAndMessage("Sprinkler OFF", 0);
						mw.WriteMessage("turned off");
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
