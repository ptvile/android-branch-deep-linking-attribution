package io.branch.referral;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import io.branch.referral.Defines.ModuleNameKeys;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.UI_MODE_SERVICE;

public class BranchPluginSupport {
    private final SystemObserver systemObserver_;
    private final Context context_;

    /**
     * Get the singleton instance for this class
     *
     * @return {@link BranchPluginSupport} instance if already initialised or null
     */
    public static BranchPluginSupport getInstance() {
        Branch b = Branch.getInstance();
        if (b == null) return null;
        return b.getBranchPluginSupport();
    }

    BranchPluginSupport(Context context) {
        context_ = context;
        systemObserver_ = new BranchPluginSupport.SystemObserverInstance();
    }

    public JSONObject deviceDescription() {
        JSONObject deviceDataObj = new JSONObject();

        try {
            String osName = SystemObserver.getOS(context_);
            if (!isNullOrEmptyOrBlank(osName)) {
                deviceDataObj.put(Defines.Jsonkey.OS.getKey(), osName);
            }
            deviceDataObj.put(Defines.Jsonkey.OSVersionAndroid.getKey(), SystemObserver.getOSVersion());

            SystemObserver.UniqueId hardwareID = getHardwareID();
            if (!isNullOrEmptyOrBlank(hardwareID.getId()) && hardwareID.isReal()) {
                deviceDataObj.put(Defines.Jsonkey.AndroidID.getKey(), hardwareID.getId());
            } else {
                deviceDataObj.put(Defines.Jsonkey.UnidentifiedDevice.getKey(), true);
            }

            String countryCode = SystemObserver.getISO2CountryCode();
            if (!TextUtils.isEmpty(countryCode)) {
                deviceDataObj.put(Defines.Jsonkey.Country.getKey(), countryCode);
            }

            String languageCode = SystemObserver.getISO2LanguageCode();
            if (!TextUtils.isEmpty(languageCode)) {
                deviceDataObj.put(Defines.Jsonkey.Language.getKey(), languageCode);
            }

            String localIpAddr = SystemObserver.getLocalIPAddress();
            if ((!TextUtils.isEmpty(localIpAddr))) {
                deviceDataObj.put(Defines.Jsonkey.LocalIP.getKey(), localIpAddr);
            }

            String brandName = SystemObserver.getPhoneBrand();
            if (!isNullOrEmptyOrBlank(brandName)) {
                deviceDataObj.put(Defines.Jsonkey.Brand.getKey(), brandName);
            }

            String appVersion = SystemObserver.getAppVersion(context_);
            deviceDataObj.put(Defines.Jsonkey.AppVersion.getKey(), appVersion);

            String modelName = SystemObserver.getPhoneModel();
            if (!isNullOrEmptyOrBlank(modelName)) {
                deviceDataObj.put(Defines.Jsonkey.Model.getKey(), modelName);
            }

            DisplayMetrics displayMetrics = SystemObserver.getScreenDisplay(context_);
            deviceDataObj.put(Defines.Jsonkey.ScreenDpi.getKey(), displayMetrics.densityDpi);
            deviceDataObj.put(Defines.Jsonkey.ScreenHeight.getKey(), displayMetrics.heightPixels);
            deviceDataObj.put(Defines.Jsonkey.ScreenWidth.getKey(), displayMetrics.widthPixels);

        } catch (JSONException ignore) { }

        return deviceDataObj;
    }

    /**
     * @return the device Hardware ID.
     * Note that if either Debug is enabled or Fetch has been disabled, then return a "fake" ID.
     */
    public SystemObserver.UniqueId getHardwareID() {
        return getSystemObserver().getUniqueID(context_, Branch.isDeviceIDFetchDisabled());
    }

    /**
     * Concrete SystemObserver implementation
     */
    private class SystemObserverInstance extends SystemObserver {
        public SystemObserverInstance() {
            super();
        }
    }

    /**
     * @return the current SystemObserver instance
     */
    SystemObserver getSystemObserver() {
        return systemObserver_;
    }

    public static boolean isNullOrEmptyOrBlank(String str) {
        return TextUtils.isEmpty(str) || str.equals(SystemObserver.BLANK);
    }
}
