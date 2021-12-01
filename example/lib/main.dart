import 'package:flutter/material.dart';
import 'dart:async';

import 'package:infosec/infosec.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  bool debugMode = false;
  bool emulator = false;
  bool installer = false;
  bool matchedSignature = false;
  String signature = "";

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    try {
      debugMode = await Infosec.checkDebuggable;
      emulator = await Infosec.checkEmulator;
      installer = await Infosec.verifyInstaller;
      signature = await Infosec.getAppSignature;
      matchedSignature = await Infosec.matchAppSignature(signature);
    } catch (e) {
      throw e;
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Running on debug: $debugMode\n'),
              Text('Running on Emulator: $emulator\n'),
              Text('Installed from Playstore: $installer\n'),
              Text('Matched Signature: $matchedSignature\n'),
              Text('Signature: $signature\n'),
            ],
          ),
        ),
      ),
    );
  }
}
