package DevicePackage;

import java.util.Random;

import InstrumentationPackage.Indicator;
import MessagePackage.Message;
import MessagePackage.MessageConstants;
import MessagePackage.MessageManagerInterface;
import MessagePackage.MessageQueue;

public abstract class AbstractECSDevice extends AbstractDevice{
	float driftValue;			// The amount of metric value gained or lost
	
	// valueRaiser: the device which raises the monitored value
	// like a heater
	boolean valueRaiserState = OFF;
	Indicator valueRaiserIndc;
	
	// valueDropper: the device which decreases the monitored value
	// like a chiller
	boolean valueDropperState = OFF;
	Indicator valueDropperIndc;

	public void run() {
		
		if (!initRegistrition()) {
			return;
		}
		
		if (deviceConfig.isController) {
			valueRaiserIndc = new Indicator (deviceConfig.valueDropperName +
					" OFF", mw.GetX(), mw.GetY()+mw.Height());
			valueDropperIndc = new Indicator (deviceConfig.valueRaiserName +
					" OFF", mw.GetX()+(valueRaiserIndc.Width()*2), mw.GetY()+mw.Height());
		}
		
		Message msg = null;							// Message object
		MessageQueue msgQue = null;					// Message Queue

		final int Delay = 2500;						// The loop delay (2.5 seconds)
		boolean Done = false;						// Loop termination flag

		if (!deviceConfig.isController) {
			mw.WriteMessage("\nInitializing Humidity Simulation::" );
			metricValue = GetRandomNumber() * (float) 100.00;
			if (CoinToss()) {
				driftValue = GetRandomNumber() * (float) -1.0;
			} else {
				driftValue = GetRandomNumber();
			}
			mw.WriteMessage("   Initial " + deviceConfig.metricName + " Set:: " + metricValue );
		}
		
		while (!Done) {
			if (!deviceConfig.isController) {
				PostFloat(msgMgrIntf, metricValue);
				mw.WriteMessage("Current " + deviceConfig.metricName + ":: " + 
											metricValue + deviceConfig.metricUnit);
			}

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
				if (msg.GetMessageId() == deviceConfig.readMsgId) {
					int printMsgType = 0;
					if (msg.GetMessage().equals(deviceConfig.valueRaiserOnCmd.toString())) {
						printMsgType = 1;
						valueRaiserState = ON;
					} else if (msg.GetMessage().equals(deviceConfig.valueRaiserOffCmd.toString())) {
						printMsgType = 2;
						valueRaiserState = OFF;
					} else if (msg.GetMessage().equals(deviceConfig.valueDropperOnCmd.toString())) {
						printMsgType = 3;
						valueDropperState = ON;
					} else if (msg.GetMessage().equals(deviceConfig.valueDropperOffCmd.toString())) {
						printMsgType = 4;
						valueDropperState = OFF;
					}
					if (deviceConfig.isController) {
						String printContent = "Unknown";
						switch (printMsgType) {
						case 1:
							printContent = "Received " + deviceConfig.valueRaiserName + " on message"; break;
						case 2:
							printContent = "Received " + deviceConfig.valueRaiserName + " off message"; break;
						case 3:
							printContent = "Received " + deviceConfig.valueDropperName + " on message"; break;
						case 4:
							printContent = "Received " + deviceConfig.valueDropperName + " off message"; break;
						case 0:
							printContent = "INVALID MESSAGE";
						}
						mw.WriteMessage(printContent);
						ConfirmMessage(msgMgrIntf, msg.GetMessage());
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
						valueRaiserIndc.dispose();
						valueDropperIndc.dispose();
					}						
				}
			}

			if (deviceConfig.isController) {
				valueRaiserIndc.SetLampColorAndMessage(deviceConfig.valueRaiserName + " " + (valueRaiserState ? "ON" : "OFF"), (valueRaiserState ? 1 : 0));
				valueDropperIndc.SetLampColorAndMessage(deviceConfig.valueDropperName + " " + (valueDropperState ? "ON" : "OFF"), (valueDropperState ? 1 : 0));
			} else {
				if (valueRaiserState) {
					metricValue += GetRandomNumber();
				}
				if (!valueRaiserState && !valueDropperState) {
					metricValue += driftValue;
				}
				if (valueDropperState){
					metricValue -= GetRandomNumber();
				}
			}

			try {
				Thread.sleep(Delay);
			} catch(Exception e) {
				mw.WriteMessage( "Sleep error:: " + e );
			}
		}	
	}

	private float GetRandomNumber(){
		Random r = new Random();
		float val = r.nextFloat();
		// map [0.0 - 1] to [0.1 - 1]
		return (float) (val * 0.9 + 0.1);
	}

	private boolean CoinToss(){
		return new Random().nextBoolean();
	}

	private void PostFloat(MessageManagerInterface ei, float metricValue ){
		Message msg = new Message( deviceConfig.sendMsgId, String.valueOf(metricValue) );
		try {
			ei.SendMessage(msg);
		} catch (Exception e) {
			System.out.println( "Error Posting " + deviceConfig.metricName + ":: " + e );
		}
	}
}
