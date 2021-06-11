
import 'dart:async';

import 'package:flutter/services.dart';

class Infosec {
  static const MethodChannel _channel =
      const MethodChannel('infosec');

  static Future<bool> get checkDebuggable async {
    // true if the app is running in debug mode
    final bool res = await _channel.invokeMethod('checkDebuggable');
    return res;
  }

  static Future<bool> get verifyInstaller async {
    // true if the app is downloaded from playstore
    final bool res = await _channel.invokeMethod('verifyInstaller');
    return res;
  }

  static Future<bool> get checkEmulator async {
    // true if the app is running on emulator device
    final bool res = await _channel.invokeMethod('checkEmulator');
    return res;
  }

  static Future<bool> matchAppSignature(String signature) async {
    // true if the signature have matched
    final bool res = await _channel.invokeMethod('matchAppSignature', <String, dynamic>{
      'signature': signature,
    });
    return res;
  }

  static Future<String> get getAppSignature async {
    // get your app signature
    final String res = await _channel.invokeMethod('getAppSignature');
    return res;
  }
}
