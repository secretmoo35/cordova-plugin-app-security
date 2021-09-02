@objc(AppSecurityPlugin) class AppSecurityPlugin : CDVPlugin {
  @objc(isDebugging:)
  func isDebugging(_ command: CDVInvokedUrlCommand) {
    var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR,messageAs: "Failed.")
    print("================ Start ================");

    // https://github.com/securing/IOSSecuritySuite
    let amIDebugged: Bool = IOSSecuritySuite.amIDebugged();
    if (amIDebugged){
        // show warning and exit the application
      print("================ amIDebugged:true ================");
    }

    pluginResult = CDVPluginResult(
        status: CDVCommandStatus_OK,
        messageAs: "isDebigging : " + String(amIDebugged)
    )

  self.commandDelegate!.send(pluginResult, callbackId: command.callbackId) 
  }

  @objc(denyDebugger:)
  func denyDebugger(_ command: CDVInvokedUrlCommand) {
    var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR,messageAs: "Failed.")

    IOSSecuritySuite.denyDebugger();

    pluginResult = CDVPluginResult(
        status: CDVCommandStatus_OK,
        messageAs: false)

  self.commandDelegate!.send(pluginResult, callbackId: command.callbackId) 
  }

  @objc(detectDebuggingAndDenyDebugger:)
  func detectDebuggingAndDenyDebugger(_ command: CDVInvokedUrlCommand) {
    var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR,messageAs: "Failed.")

    IOSSecuritySuite.amIDetectDebuggedAndDeny();

    pluginResult = CDVPluginResult(
        status: CDVCommandStatus_OK,
        messageAs: false)

  self.commandDelegate!.send(pluginResult, callbackId: command.callbackId) 
  }

  @objc(reverseEngineered:)
  func reverseEngineered(_ command: CDVInvokedUrlCommand) {
    var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR,messageAs: "Failed.")

    let isReverseEngineered: Bool = IOSSecuritySuite.amIReverseEngineered();

    pluginResult = CDVPluginResult(
        status: CDVCommandStatus_OK,
        messageAs: "reverseEngineered : " + String(isReverseEngineered)
    )

  self.commandDelegate!.send(pluginResult, callbackId: command.callbackId) 
  }

  @objc(denyReverseEngineered:)
  func denyReverseEngineered(_ command: CDVInvokedUrlCommand) {
    var pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR,messageAs: "Failed.")

    IOSSecuritySuite.amIDetectReverseEngineeredAndDeny();

    pluginResult = CDVPluginResult(
        status: CDVCommandStatus_OK,
        messageAs: "denyReverseEngineered"
    )

  self.commandDelegate!.send(pluginResult, callbackId: command.callbackId) 
  }
}


