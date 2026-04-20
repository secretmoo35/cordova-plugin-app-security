package cordova.plugin.app.security;

// import org.apache.cordova.BuildConfig;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult.Status;
import org.apache.cordova.PluginResult;
// import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import android.os.Debug;
import android.os.Build;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
        } else if (action.equals("isFlagDebuggable")) {
            this.isFlagDebuggable(callbackContext);
            return true;
        } else if (action.equals("isDeviceCompromised")) {
            this.isDeviceCompromised(callbackContext);
            return true;
        }
        return false;
    }

    private void detectDebuggingAndDenyDebugger(CallbackContext callbackContext) {
        // LOG.d("AppSecurityPlugin", "Checking");
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Boolean isDebuggerAttached = Debug.isDebuggerConnected() || Debug.waitingForDebugger();
                // LOG.d("AppSecurityPlugin", "Checked Result : " + isDebuggerAttached);
                if (isDebuggerAttached) {
                    System.exit(0);
                }
            }
        }, 1000, 2000);
    }

    private void isFlagDebuggable(CallbackContext callbackContext) {
        try {
            callbackContext.sendPluginResult(new PluginResult(Status.OK, (ApplicationInfo.FLAG_DEBUGGABLE) != 0));
        } catch (Exception e) {
            callbackContext.error("Exception isFlagDebuggable");
        }
    }

    private void isDeviceCompromised(CallbackContext callbackContext) {
        try {
            boolean isRooted = checkSuBinary() || checkRootManagementApps() || checkBuildTags() || checkSuCommand() || checkSystemProperties();
            callbackContext.sendPluginResult(new PluginResult(Status.OK, isRooted));
        } catch (Exception e) {
            callbackContext.sendPluginResult(new PluginResult(Status.OK, false));
        }
    }

    /**
     * Check if su binary exists in common paths
     */
    private boolean checkSuBinary() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su",
                "/su/bin",
                "/system/xbin/daemonsu",
                "/system/etc/init.d/99teleknox"
        };

        for (String path : paths) {
            try {
                if (new File(path).exists()) {
                    return true;
                }
            } catch (Exception e) {
                // SELinux may block access, continue
            }
        }

        // Fallback: use shell to check paths (bypasses some SELinux restrictions)
        for (String path : paths) {
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", "ls " + path});
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                process.destroy();
                if (line != null && line.contains(path)) {
                    return true;
                }
            } catch (Exception e) {
                // continue
            }
        }
        return false;
    }

    /**
     * Check if known root management apps are installed
     */
    private boolean checkRootManagementApps() {
        String[] packages = {
                "com.topjohnwu.magisk",
                "eu.chainfire.supersu",
                "com.koushikdutta.superuser",
                "com.noshufou.android.su",
                "com.noshufou.android.su.elite",
                "com.thirdparty.superuser",
                "com.yellowes.su",
                "com.kingroot.kinguser",
                "com.kingo.root",
                "com.oasisfeng.greenify",
                "com.devadvance.rootcloak",
                "com.devadvance.rootcloakplus",
                "de.robv.android.xposed.installer",
                "com.saurik.substrate",
                "com.zachspong.temprootremovejb",
                "com.amphoras.hidemyroot",
                "com.amphoras.hidemyrootadd",
                "com.formyhm.hiderootpremium",
                "com.formyhm.hideroot"
        };

        PackageManager pm = this.cordova.getActivity().getPackageManager();
        for (String packageName : packages) {
            try {
                pm.getPackageInfo(packageName, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                // Not installed, continue
            }
        }
        return false;
    }

    /**
     * Check for test-keys in Build.TAGS which indicates a custom ROM
     */
    private boolean checkBuildTags() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    /**
     * Try to execute su command via shell
     */
    private boolean checkSuCommand() {
        Process process = null;
        try {
            // Use sh -c to ensure proper shell environment
            process = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", "which su"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            return line != null && line.trim().length() > 0;
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * Check system properties for root/debug indicators
     */
    private boolean checkSystemProperties() {
        // Check ro.debuggable (1 = debuggable/rooted emulator)
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", "getprop ro.debuggable"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.destroy();
            if (line != null && line.trim().equals("1")) {
                // Also verify ro.build.type to avoid false positives on debug builds
                Process process2 = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", "getprop ro.build.type"});
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                String buildType = reader2.readLine();
                process2.destroy();
                if (buildType != null && (buildType.trim().equals("userdebug") || buildType.trim().equals("eng"))) {
                    return true;
                }
            }
        } catch (Exception e) {
            // continue
        }

        // Check ro.secure (0 = not secure / rooted)
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", "getprop ro.secure"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.destroy();
            if (line != null && line.trim().equals("0")) {
                return true;
            }
        } catch (Exception e) {
            // continue
        }
        return false;
    }
}
