package MessagePackage;
/*
 * XXX Sensor--> (XXX_DATA+float value)
 * 				 -->Monitor-->(XXX_CTRL+XXX_ON/XXX_OFF)
 * 					-->XXX Controller-->(XXX_CONF+XXX_ON/XXX_OFF)
 * XXX Sensor<--
 */
public enum MessageConstants {	
	HEALTH_REQ,			// Message ID of a health check ping, followed by a float value
						// Sent by the maintenance console, Read by all devices
	HEALTH_RES,			// Message ID of a health check response, follow by a float value
						// Sent by devices, Read by the Maintenance console
	TEMP_DATA,			// Message ID of a temperature float value in degrees Fahrenheit
						// Sent by Temperature Sensor, Read by ECSMonitor
	
	HUMI_DATA,			// Message ID of a Humidity float value in percentage
						// Sent by Humidity Sensor, Read by ECSMonitor
	
	ALARM_DATA,			// Message ID of an intrusion sensor's alarm type
						// Sent by Intrusion Sensor, Read by Security Monitor
	FIRE_DATA,			// Message ID of an fire report
						// Sent by Fire Sensor, Read by Security Monitor
	
	HUMI_CTRL,			// Message ID of a command to turn ON or OFF the Humidifier/Dehumidifier
						// Sent by ECSMonitor, Read by Humidity Controller
	
	TEMP_CTRL,			// Message ID of a command to turn ON or OFF the Heater/Chiller
						// Sent by ECSMonitor, Read by Humidity Controller
	
	SALARM_CTRL,			// Message ID of a command to turn ON or OFF the Security Alarm
						// Sent by Security Console, Read by Intrusion Controller
	FALARM_CTRL,		// Message ID of fire alarm control
	
	SPRK_CTRL,			// Message ID of a command to turn ON or OFF the Security Alarm
						// Sent by Security Console, Read by Intrusion Controller
	
	HUMI_CONF,			// Message ID of a confirmation of turning ON or OFF the Humidifier/Dehumidifier
						// Sent by Humidity Controller, Read by Humidity Sensor
	
	TEMP_CONF,			// Message ID of a confirmation of turning ON or OFF the Heater/Chiller
						// Sent by Temperature Controller, Read by Temperature Sensor
	
	HUMI_ON,			// Message body: turn on the humidifier
	HUMI_OFF,			// Message body: turn off the humidifier
	DEHUMI_ON,			// Message body: turn on the dehumidifier
	DEHUMI_OFF,			// Message body: turn off the dehumidifier
	
	HEATER_ON,			// Message body: turn on the heater
	HEATER_OFF,			// Message body: turn off the heater
	CHILLER_ON,			// Message body: turn on the chiller
	CHILLER_OFF,		// Message body: turn off the chiller

	ALARM_WINDOW,		// Message body: Type of alarm is window break
	ALARM_DOOR,			// Message body: Type of alarm is door break
	ALARM_MOTION,		// Message body: Type of alarm is motion detection
	
	ALARM_ON,			// Message body: Intrusion Armed
	ALARM_OFF,			// Message body: Intrusion Disarmed
	
	SPRK_ON,			// Message body: Sprinkler ON
	SPRK_OFF,			// Message body: Sprinkler OFF
	
	DEVICE_STOP,		// Tells a device to stop
}
