# WeatherStation
This weather station software interfaces with a Davis Vantage Pro II weather station.  The software interfaces to the console using the Davis supplied adapter.  I have owned the weather station since 2005.  The Davis weather station is in my opinion the best personnal weather station available, however their supplied software interface is rudimentary and does not meet my needs.  

My goal is to create software with a more intuitive GUI interface, better graphing capabilities, better data analysis, integrated NOAA alerts, sun/moon rise/set times, snow data, all in a single program that can run on a small Raspberry PI driving a small 10" HDMI screen for daily use and can run on a laptop/desktop for data analysis.  

The source code is written in Java and contained within the "src" directory.  Java is the most widely used high level object oriented language in use today.  It is open source freeware software.  There are a number of free IDEs such as Eclipse and IntelliJ Idea.  I used IntelliJ Idea for this project, however Eclipse is just as good.   

The "helptext" directory contains the help text files.  Modifying these files modifies the help text that is displayed.

The "moonicons" directory contain the .png images of the 30 day phases of the moon.

The "reports" directory contains the reports that are generated by the program.  When any report is generated, the report is both displayed and written to this folder.  The rain spreadsheet is also written here.

The "wxicons" directory contains the weather icons that relate to the forecast weather conditions.

The "tmp" directory contains the debug and log files.
