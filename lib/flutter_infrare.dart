import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/services.dart';

class FlutterInfrare {
  static const MethodChannel _channel = const MethodChannel('flutter_infrare');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> isSupport() async {
    if (Platform.isAndroid) {
      return await _channel.invokeMethod('isSupport');
    } else {
      return false;
    }
  }

  static Future<void> send(int userCode1, int userCode2, int data) async {
    if (Platform.isAndroid) {
      Map<String, int> params = Map();
      params['userCode1'] = userCode1;
      params['userCode2'] = userCode2;
      params['data'] = data;
      return await _channel.invokeMethod('send', json.encode(params));
    }
  }
}
