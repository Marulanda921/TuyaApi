package com.example.app;

import android.os.Bundle;

import com.example.app.plugin.PosPrinterPlugin;
import com.getcapacitor.BridgeActivity;
import com.example.app.plugin.MagneticCardPlugin;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    registerPlugin(MagneticCardPlugin.class);
    registerPlugin(PosPrinterPlugin.class);
    super.onCreate(savedInstanceState);
  }
}
