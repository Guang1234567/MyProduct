adb wait-for-device

adb pull /data/anr/traces.txt anr_traces_%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%.txt

pause