package com.example.app.plugin;

import android.content.Context;

import com.common.apiutil.TimeoutException;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.common.apiutil.magnetic.MagneticCard;
import com.common.apiutil.CommonException;

@CapacitorPlugin(
  name = "MagneticCard"
)
public class MagneticCardPlugin extends Plugin {
  private MagneticCard magneticCard;
  private static final String TAG = "MagneticCardPlugin";


  @Override
  public void load() {
    try {
      magneticCard = new MagneticCard();
      android.util.Log.d(TAG, "MagneticCard plugin loaded successfully");
    } catch (Exception e) {
      android.util.Log.e(TAG, "Error loading plugin: " + e.getMessage());
    }
  }


  @PluginMethod
  public void init(PluginCall call) {
    try {
      Context context = getContext();
      if (context == null) {
        call.reject("Context is null");
        return;
      }
      MagneticCard.init(context);
      JSObject ret = new JSObject();
      ret.put("status", "initialized");
      call.resolve(ret);
    } catch (Exception e) {
      call.reject("Failed to initialize magnetic card reader: " + e.getMessage(), e);
    }
  }

  @PluginMethod
  public void open(PluginCall call) {
    try {
      Context context = getContext();
      if (context == null) {
        call.reject("Context is null");
        return;
      }
      MagneticCard.open(context);
      JSObject ret = new JSObject();
      ret.put("status", "opened");
      call.resolve(ret);
    } catch (CommonException e) {
      call.reject("Failed to open magnetic card reader: " + e.getMessage(), e);
    }
  }

  @PluginMethod
  public void close(PluginCall call) {
    try {
      MagneticCard.close();
      JSObject ret = new JSObject();
      ret.put("status", "closed");
      call.resolve(ret);
    } catch (Exception e) {
      call.reject("Failed to close magnetic card reader: " + e.getMessage(), e);
    }
  }

  @PluginMethod
  public void check(PluginCall call) {
    try {
      int timeout = call.getInt("timeout", 50);
      String[] trackData = MagneticCard.check(timeout);

      if (trackData == null) {
        throw new TimeoutException("No card detected");
      }

      JSObject ret = new JSObject();
      ret.put("track1", trackData[0] != null ? trackData[0] : "");
      ret.put("track2", trackData[1] != null ? trackData[1] : "");
      ret.put("track3", trackData[2] != null ? trackData[2] : "");
      call.resolve(ret);
    } catch (TimeoutException e) {
      call.reject("timeout", e);
    } catch (CommonException e) {
      call.reject("Failed to check magnetic card: " + e.getMessage(), e);
    }
  }

  @PluginMethod
  public void startReading(PluginCall call) {
    try {
      // Verificar que magneticCard esté inicializado
      if (magneticCard == null) {
        android.util.Log.e(TAG, "MagneticCard instance is null");
        call.reject("MagneticCard instance not initialized");
        return;
      }

      android.util.Log.d(TAG, "Starting card reading...");
      int result = MagneticCard.startReading();
      android.util.Log.d(TAG, "Start reading result: " + result);

      JSObject ret = new JSObject();
      if (result == 0) {
        ret.put("status", "started");
        call.resolve(ret);
      } else {
        call.reject("Failed to start reading magnetic card. Error code: " + result);
      }
    } catch (Exception e) {
      android.util.Log.e(TAG, "Error starting reading: " + e.getMessage());
      call.reject("Failed to start reading magnetic card: " + e.getMessage(), e);
    }
  }
}
