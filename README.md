#### ADSBSQLite ADS-B Receiver Database Engine

* Very Experimental, Initial Version, tested on Ubuntu 18.04.3 LTS, and Java SE 1.8.0_111

This is a Java 8 Application that listens to ADS-B data on TCP port 30003 (or configured alternate port), which is known as the Basestation Compatible port from the original ADS-B receiver from Kinetics UK.

This TCP port does not have any fancy data being output, it is just the most common data that people are interested in - heading, altitude, speed, etc.

The application reads the data in and creates database SQL statements in which to store the targets.

It can optionally display a simple GUI to show the data counts being processed.

![My image](https://raw.githubusercontent.com/srsampson/ADSBSQLite/master/adsbsqlite.png)

The database is updated by a configurable (1 to 13 seconds) time. Aircraft transmit their position every second, but in most cases it isn't necessary to have that resolution. By default I set 3 seconds, which is adequate for me. In the Track Display program, it reads the database every second, which if you set in 13 seconds here, not a lot of its queries will have new data, but reading is not as database intensive as writing. Every write will update several tables.

This update time can be considered a simulated Revolutions Per Minute (RPM) of a rotating radar antenna. A long range radar usually updates every 10 to 12 seconds (6 or 5 RPM), while an airport radar will update every 2.5 seconds (24 RPM). A surface to air missile system will update every second (for comparisons).

Included is a config file that the program reads on startup. This contains the database table name. There is also an export of the database SQL so you can initialize the database.

Basically you create a directory to put the ```ADSBSQLite.jar``` file in. Then create a ```lib``` directory and put the database JAR file in there. The program was compiled using this particular file, so don't upgrade it without recompiling (I use Netbeans IDE).

Put the config file in the created directory and you are ready to go after you create the SQLite database. Just import the SQL file. I use the ```sqlitebrowser``` tool.

A GUI will be optionally displayed and it basically shows the data counts being received by the Basestation TCP port. Otherwise this program does nothing but work in the background storing the data.

The database is designed so that as new targets come in, their Mode-S ICAO number is added, and the TCP port data is recorded. Sooner or later this aircraft will land or fade-out, and the database will move it to the history file. If it pops up again, then it is issued a new flight number. The data in the ```target``` table then, is the current data. When those targets fade or land, they are deleted from this table, and moved to the ```targethistory``` table.

Also, for ADS-B data with latitude and longitude, a table is available that shows their target echoes, so you could do a database query and plot all the position history data from this aircraft on a map.

There is also a ```metrics``` table that shows how much data has been processed every 30 seconds.

Commandline: ```java -jar ADSBSQLite.jar 2>&1 >>errorlog.txt &``` on Linux and just double click the jar file on Windows.

##### Time and Date Stamps
All data is recorded in UTC time. This is so multiple receivers in different time zones all record to the same time reference. Although, the times may differ if system are not synchronized to GPS.

##### Aircraft Registration
Currently it only updates the registration (N-Number) for USA aircraft, as they are assigned 1:1, and I don't have any info on other countries. I don't do an Internet lookup, as most sites don't allow it anyway.

##### Port 30003 Compatibility
The program to get is ```dump1090``` which along with a $20 receiver will suck data out of the atmosphere and drop it into your database.

##### Mode-S Beast
I have a Raspberry PI in the rafters with Power Over Ethernet (POE), and it has a Mode-S Beast receiver plugged in. I'm using the ```beast-splitter``` program to read the USB serial port data. Then I use ```modesmixer2``` to convert that to Basestation Port 30003.
```
nohup sudo beast-splitter --serial /dev/beast --listen 30005:R &
nohup ./modesmixer2 --location 34.382901:-98.423287 --outServer msg:30003 --inConnect 127.0.0.1:30005&
```
If you don't want to use beast-splitter you can just use the serial option to modesmixer2:
```
nohup ./modesmixer2 --location 34.382:-98.423 --outServer msg:30003 --inSerial /dev/ttyUSB0:3000000 &
```
If I use an RTL SDR receiver (which doesn't work half as well as the Beast), you can use something like this:
```
nohup sudo ./modesdeco2 --location 34.382901:-98.423287 --msg 30003&
```
In this case, you don't need the ```beast-splitter``` or ```modesmixer2```.
