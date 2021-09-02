var exec = require('cordova/exec');
var AppSecurityPlugin = function () {}; // This just makes it easier for us to export all of the functions at once.
// All of your plugin functions go below this. 
// Note: We are not passing any options in the [] block for this, so make sure you include the empty [] block.
AppSecurityPlugin.isDebugging = function (onSuccess, onError) {
    exec(onSuccess, onError, "AppSecurityPlugin", "isDebugging", []);
};

AppSecurityPlugin.denyDebugger = function (onSuccess, onError) {
    exec(onSuccess, onError, "AppSecurityPlugin", "denyDebugger", []);
};

AppSecurityPlugin.detectDebuggingAndDenyDebugger = function (onSuccess, onError) {
    exec(onSuccess, onError, "AppSecurityPlugin", "detectDebuggingAndDenyDebugger", []);
};

AppSecurityPlugin.reverseEngineered = function (onSuccess, onError) {
    exec(onSuccess, onError, "AppSecurityPlugin", "reverseEngineered", []);
};

AppSecurityPlugin.denyReverseEngineered = function (onSuccess, onError) {
    exec(onSuccess, onError, "AppSecurityPlugin", "denyReverseEngineered", []);
};

module.exports = AppSecurityPlugin;

