# mobileprog-group-3
Mobile Programming Group 3 - Benjamin, Daniel, Mats and Sang

## WhenWeekly

#### Description:
An app that lets people plan social events together, by selecting the date and times that work
for them. The organizer will then see what days/times are available for most/all (the time slot
will be highlighted green).
#### Scope:
The app is a calendar extension that helps organize social events.
#### Target Audience:
People that need help with organizing social events in a clean and orderly(?) fashion.

## Usage
#### Prerequisites:
01. You have Android Studio installed with a working emulator.
02. You have IntelliJ IDEA installed.
03. You have XAMPP installed.

1. Make a new folder and clone the project with the command "https://git.gvk.idi.ntnu.no/Daniehhu/mobile-programming-group-3.git"
2. Open XAMPP and start Apache and MySQL
3. Open the directory called "backend" in IntelliJ IDEA,
add the VM Options to the ApplicationKt: "-DDB_URL=jdbc:mysql://127.0.0.1:3306/whenweekly -DDB_USER=root -DDB_PWD= -DPORT=8080 -DBUILD_CONFIG=dev", then run the application
4. Open the directory called "frontend" in Android Studio and run the application
5. You can now use the application
