# BLE-5-Performace-Test
This application services as a test application for BLE 5 Beacon throughput. The testing is based off of 
the dynamic data component of [EV-Retina](https://github.com/IanQuinn22/BLE_Photo_Identifier).
## Testing
To test the throughput, you can enter as much data in the input boxes for Name, Shirt Color, and Pants 
Color as you wish. If the data is less than 245 bytes, it will be transmitted in a single beacon message. 
Otherwise, it will be transmitted over multiple beacon messages. To receive bradcasts, press the "Collect 
Broadcasts" button. To broadcast, press the "Broadcast" button.
## Requirements
To use this application, your device must be running Android 8 or later. Your device must also be compatible 
with Bluetooth 5. If either of these requirements is not met, a log message will be printed.
## Notes
For my testing purposes, I placed timing notes on the log files. You can use this method or alter the log 
messages for your specific testing purposes.

