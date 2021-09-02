# cordova-plugin-app-security

This plugin is developed for the anti-debug mechanism in Ionic and Cordova applications by detecting debugging and closing the application.

** Many functions have not been developed.
** Not updated or fixed if any problems are found.

##### Support
Android v5.1+
iOS v11+
cordova-android v9+
cordova-ios v6+
ionic 3+

##### Using
# 
- Install
```
ionic cordova plugin add https://github.com/secretmoo35/cordova-plugin-app-security.git
or
cordova plugin add https://github.com/secretmoo35/cordova-plugin-app-security.git
```
- function
```
declare var cordova: any;

cordova.plugins.AppSecurityPlugin.detectDebuggingAndDenyDebugger();
cordova.plugins.AppSecurityPlugin.denyReverseEngineered();
```

##### Credit
Android : https://github.com/OWASP/owasp-mstg/blob/master/Document/0x05j-Testing-Resiliency-Against-Reverse-Engineering.md
iOS : https://github.com/securing/IOSSecuritySuite

##### Runtime Mobile Exploration
https://github.com/sensepost/objection
