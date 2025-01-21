package com.example.app.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.common.apiutil.CommonException;
import com.common.apiutil.printer.UsbThermalPrinter;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

@CapacitorPlugin(name = "PosPrinter")
public class PosPrinterPlugin extends Plugin {
  private UsbThermalPrinter printer;

  @Override
  public void load() {
    Context context = getContext();
    printer = new UsbThermalPrinter(context);

    // Initialize the printer in a separate thread
    new Thread(() -> {
      try {
        printer.start(0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }

  @PluginMethod
  public void printText(PluginCall call) {
    String text = call.getString("text");

    if (text == null || text.isEmpty()) {
      call.reject("No text provided");
      return;
    }

    new Thread(() -> {
      try {
        printer.reset();
        printer.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
        printer.setTextSize(20);
        printer.setGray(4);

        // Print the provided text
        printer.addString(text);
        printer.printString();

        // Walk paper to make sure the content is printed properly
        printer.walkPaper(20);

        JSObject ret = new JSObject();
        ret.put("status", "printed");
        call.resolve(ret);
      } catch (CommonException e) {
        call.reject("Failed to print text: " + e.getMessage(), e);
      }
    }).start();
  }

  @PluginMethod
  public void checkStatus(PluginCall call) {
    new Thread(() -> {
      try {
        int status = printer.checkStatus();
        JSObject ret = new JSObject();
        ret.put("status", status);
        call.resolve(ret);
      } catch (CommonException e) {
        call.reject("Failed to check status: " + e.getMessage(), e);
      }
    }).start();
  }

  @Override
  protected void handleOnDestroy() {
    try {
      if (printer != null) {
        printer.stop();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    super.handleOnDestroy();
  }
}
