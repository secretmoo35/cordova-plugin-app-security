package cordova.plugin.app.security;

import org.apache.cordova.BuildConfig;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult.Status;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import android.os.Debug;
import android.content.pm.ApplicationInfo;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class echoes a string called from JavaScript.
 */
public class AppSecurityPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("detectDebuggingAndDenyDebugger")) {
            this.detectDebuggingAndDenyDebugger(callbackContext);
            return true;
        }else if(action.equals("isFlagDebuggable")){
            this.isFlagDebuggable(callbackContext);
            return true;
        }
        return false;
    }

    private void detectDebuggingAndDenyDebugger(CallbackContext callbackContext) {
        LOG.d("AppSecurityPlugin", "Checking");
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                Boolean isDebuggerAttached = Debug.isDebuggerConnected() || Debug.waitingForDebugger() || (!BuildConfig.DEBUG && ((ApplicationInfo.FLAG_DEBUGGABLE) != 0));
                LOG.d("AppSecurityPlugin", "Checked Result : " + isDebuggerAttached);
                if(isDebuggerAttached){
                    System.exit(0);
                }
            }
        },1000,2000);
    }

    private void isFlagDebuggable(CallbackContext callbackContext) {
        try {
            callbackContext.sendPluginResult(new PluginResult(Status.OK, (ApplicationInfo.FLAG_DEBUGGABLE) != 0));
        } catch (Exception e) {
            callbackContext.error("Exception isFlagDebuggable");
        }
    }
}