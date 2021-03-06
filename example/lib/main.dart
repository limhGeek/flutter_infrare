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
              RaisedButton(
                child: Text('发送数据'),
                textColor: Colors.white,
                color: Theme.of(context).accentColor,
                onPressed: () {
                  send(0x08, 0XE6, 0x41);
                },
              ),
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

  ///发送命令
  ///userCodeH：用户码1
  ///userCodeL：用户码2
  ///data：键值
  Future<void> send(int userCodeH, int userCodeL, int data) async {
    await FlutterInfrare.send(userCodeH, userCodeL, data);
  }
}
