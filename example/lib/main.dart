import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_infrare/flutter_infrare.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String status;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    isSupport();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await FlutterInfrare.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
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
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              Text('状态: $status'),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> isSupport() async {
    bool isSupport = await FlutterInfrare.isSupport();
    if (!mounted) return;
    setState(() {
      if (isSupport) {
        status = 'Mobile hardware supports infrared module';
      } else {
        status = "Infrared module is not supported in mobile hardware";
      }
    });
  }
}
