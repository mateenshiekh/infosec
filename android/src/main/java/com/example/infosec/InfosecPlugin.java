package com.example.infosec;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.security.MessageDigest;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** InfosecPlugin */
public class InfosecPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private static final String PLAY_STORE_APP_ID = "com.android.vending";
  private static final String MATCH_APP_SIGNATURE = "matchAppSignature";
  private static final String GET_APP_SIGNATURE = "getAppSignature";
  private static final String CHECK_EMULATOR = "checkEmulator";
  private static final String VERIFY_INSTALLER = "verifyInstaller";
  private static final String CHECK_DEBUGGABLE = "checkDebuggable";
  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "infosec");
    channel.setMethodCallHandler(this);
    this.context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals(CHECK_DEBUGGABLE)) {
      boolean res = checkDebuggable(context);
      result.success(res);
    } else if (call.method.equals(VERIFY_INSTALLER)) {
      boolean res = verifyInstaller(context);
      result.success(res);
    } else if (call.method.equals(CHECK_EMULATOR)) {
      boolean res = checkEmulator();
      result.success(res);
    } else if (call.method.equals(MATCH_APP_SIGNATURE)) {
      final String sign = call.argument("signature");
      boolean res = checkAppSignature(context, sign);
      result.success(res);
    } else if (call.method.equals(GET_APP_SIGNATURE)) {
      result.success(getAppSignature(context));
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  public static boolean verifyInstaller(final Context context) {

    final String installer = context.getPackageManager()
            .getInstallerPackageName(context.getPackageName());

    return installer != null

            && installer.startsWith(PLAY_STORE_APP_ID);

  }

  public static boolean checkDebuggable(Context context){

    return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

  }

  public static boolean checkEmulator() {

    try {

      return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
              || Build.FINGERPRINT.startsWith("generic")
              || Build.FINGERPRINT.startsWith("unknown")
              || Build.HARDWARE.contains("goldfish")
              || Build.HARDWARE.contains("ranchu")
              || Build.MODEL.contains("google_sdk")
              || Build.MODEL.contains("Emulator")
              || Build.MODEL.contains("Android SDK built for x86")
              || Build.MANUFACTURER.contains("Genymotion")
              || Build.PRODUCT.contains("sdk_google")
              || Build.PRODUCT.contains("google_sdk")
              || Build.PRODUCT.contains("sdk")
              || Build.PRODUCT.contains("sdk_x86")
              || Build.PRODUCT.contains("vbox86p")
              || Build.PRODUCT.contains("emulator")
              || Build.PRODUCT.contains("simulator");

    } catch (Exception e) {

    }

    return false;

  }

  public static boolean checkAppSignature(Context context, String matchingSignature) {

    try {

      PackageInfo packageInfo = context.getPackageManager()

              .getPackageInfo(context.getPackageName(),

                      PackageManager.GET_SIGNATURES);

      for (Signature signature : packageInfo.signatures) {


        MessageDigest md = MessageDigest.getInstance("SHA");

        md.update(signature.toByteArray());

        final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);

        //compare signatures

        if (currentSignature.trim().equals(matchingSignature.trim())) {
          return true;

        }
      }

    } catch (Exception e) {
//assumes an issue in checking signature., but we let the caller decide on what to do.
    }

    return false;
  }


  public static String getAppSignature(Context context) {

    try {

      PackageInfo packageInfo = context.getPackageManager()

              .getPackageInfo(context.getPackageName(),

                      PackageManager.GET_SIGNATURES);

      for (Signature signature : packageInfo.signatures) {


        MessageDigest md = MessageDigest.getInstance("SHA");

        md.update(signature.toByteArray());

        final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);

        return  currentSignature;
      }

    } catch (Exception e) {
//assumes an issue in checking signature., but we let the caller decide on what to do.
    }

    return null;
  }
  }
