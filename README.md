# iFixDataloss
This repository contains the iFixDataloss tool, the evaluation detail and all data loss issues detected by iFixDataloss, which are available in three separate folders: tool and source code, evaluation, and DataSet.

Specifically, the comparisions between iFixDataloss and the other two data loss detection tools(LiveDroid and DLD) for each app's activities are in the Results.xlsx which is in the evaluation folder. The picture below shows partial results.
<p align="left"><img src="pictures/results.png" /></div>

All data loss issues detected by iFixDataloss are in the DataSet folder, and the output of LiveDroid and DLD are in the results folder of evaluation.

We give two examples to show the data loss issue.

## Example1
<table><tr>
<td>
<div>
<img src="videos/dataloss1.gif" border=0>
<h1 align="center">before fixing</h1>
</div>
</td>
<td>
<div>
<img src="videos/fix1.gif" border=0>
<h1 align="center">after fixing</h1>
</div>
</td>
</tr></table>

## Example2
<table><tr>
<td>
<div>
<img src="videos/dataloss2.gif" border=0>
<h1 align="center">before fixing</h1>
</div>
</td>
<td>
<div>
<img src="videos/fix2.gif" border=0>
<h1 align="center">after fixing</h1>
</div>
</td>
</tr></table>

## How to use the tool
1. Make sure you have an Android device opened and connected via ADB
2. Install the input apk in the Android device and start the installed app:   
   **adb install <apk file path>**  
   **adb shell am start -S -n <app package name>**
3. Compile and run AndroidStaticAnalysis:  
   **cd .../AndroidStaticAnalysis**  
   **mvn compile**  
   **mvn exec:java -Dexec.mainClass="com.fdu.se.sootanalyze.Main"**
4. Compile and run DynamicExplore:  
   **cd .../DynamicExplore**  
   **gradlew clean**  
   **gradlew build**  
   **gradlew assembleDebug**  
   **adb install -t .../app-debug.apk**  
   **adb install -r .../app-debug-androidTest.apk**  
   **adb shell am instrument -w -r -e debug false -e class 'com.fdu.uiautomatortest.DynamicTest#getDynamicGraphWithStatic' com.fdu.uiautomatortest.test/android.support.test.runner.AndroidJUnitRunner**
5. Compile and run AndroidPatchGen:  
   **cd .../AndroidPatchGen**  
   **mvn compile**  
   **mvn exec:java -Dexec.mainClass="com.fdu.se.patchgen.Main"**