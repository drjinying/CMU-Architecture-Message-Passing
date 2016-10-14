# CMU Software Architecture: Message Passing
This is the A3 project for the "Architecture for Software System" course at Carnegie Mellon University. The highlight of this code is the architectural design of a centeral message router. Core functionalities:

- Enableing sensors, alarms and a controlling console to work together
- Security features like sensor's health check and malicious device detaction
- Auto adjustment of temperature and humidity
- Configurations from the controlling console to all the security network
- Device registration and update
- Message redelivery upon network failures

Architecture is designed in a way that security is guarenteed. In the static perspective, developing effort is reduced by good coding structure: Those command messages are taken care by the code, instead of the programmer (this is similar to Android's R class, the resourse ID tags).
