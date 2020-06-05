import 'dart:async';

import 'package:flutter/services.dart';

class FlutterInfrare {
  static const MethodChannel _channel = const MethodChannel('flutter_infrare');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> isSupport() async {
    return await _channel.invokeMethod('isSupport');
  }
}