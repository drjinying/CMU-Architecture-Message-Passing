%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java DevicePackage/TemperatureController %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java DevicePackage/HumidityController %1
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java DevicePackage/TemperatureSensor %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java DevicePackage/HumiditySensor %1
%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java ECSConsole %1

START "INTRUSION ALARM CONTROLLER" /MIN /NORMAL java DevicePackage/IntrusionController %1
START "FIRE ALARM CONTROLLER" /MIN /NORMAL java DevicePackage/FireAlarmController %1
START "SPRINKLER CONTROLLER" /MIN /NORMAL java DevicePackage/SprinklerController %1
START "INTRUSION SENSOR" /MIN /NORMAL java DevicePackage/IntrusionSensor %1
START "FIRE SENSOR" /MIN /NORMAL java DevicePackage/FireSensor %1

START "MUSEUM MAINTENANCE CONSOLE" /NORMAL java MaintenanceConsole %1
START "MUSEUM SECURITY CONSOLE" /NORMAL java SecurityConsole %1
START "SECURITY SENSORS SIMILATOR" /NORMAL java SecuritySimulator %1
