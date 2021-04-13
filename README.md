# BLE-5-Performace-Test
This application services as a test application for BLE 5 Beacon throughput. The testing is based off of 
the dynamic data component of [EV-Retina](https://github.com/IanQuinn22/BLE_Photo_Identifier).
## Testing
To test the throughput, you can enter as much data in the input boxes for Name, Shirt Color, and Pants 
Color as you wish. If the data is less than 245 bytes, it will be transmitted in a single beacon message. 
Otherwise, it will be transmitted over multiple beacon messages. To receive bradcasts, press the "Collect 
Broadcasts" button. To broadcast, press the "Broadcast" button.
## Environment/Requirements
This application is designed to run on Android devices. To use this application, your device must be running Android 8 or later. Your device must also be compatible 
with Bluetooth 5. If either of these requirements is not met, a log message will be printed.
## Installation/Running
You must have Android Studio to run this application. To run the application, clone this repository and open it in Android Studio. Then install the application from Android Studio on a device with Android 8 or later.
## Parameters
Some of the parameters in this application can be modified to fit the implementers needs. They are described below.
* Advertising Interval: The interval between packet advertisiments. This is currently set to high, but can be modified.
* Tx Power Level: Measures the strength of the advertisement signal. This is currently set to medium, but can be modified.
* Minimum advertising time: Minimum amount of time that an entire broadcast will be advertised for. Currently this is set to 20 seconds.
* Broadcast Repeat: Minimum numnber of times that a broadcast will be re-advertised. Currently this is set to 5.
* Maximum Packet Size: Largest possible size for a single advertisement. For BLE 5, this was increased to 245. Since this implementation is meant to be for BLE 5, it is currently set to 245. If the implementer would like to decrease this value, he/she may do so. However, this value cannot be increased.
* Packet Advertisement Duration: Time that each packet is advertised for. Currently set to 420 ms. This value can be modified, but it may affect performance depending on how many devices are advertising and how large the broadcasts are.
* Scan Mode: Latency of the scan. Currently set to low latency.
## Important Functions/Methods
* Fragmenter.advertise: This function is where the actual beacon broadcasting is performed. In this function, the entire broadcast is broken into advertisements with a size equal to the Maximum Packet size. This function takes parameters that include Advertise Settings, Advertising callbacks, and Maximum Packet Size.
* Assembler.gather: his function is called everytime a packet is received. In this function, packet data is stored for each device and updated as new packets arrive. The function takes a byte array of Max Packet Size as a parameter.
## Bottlenecks/Bugs
* If the test is being used to calculate throughput for very specific scenarios, the Packet Advertisement Duration should be tuned to reflect that situation. There is an existing bug in this implementation in which data from multiple devices is sometimes combined into on broadcast on the application-side. To fix this issue, future implementers can add bytes to each packet to uniquely identify an advertiser.
## Notes
For my testing purposes, I placed timing notes on the log files. You can use this method or alter the log 
messages for your specific testing purposes.

