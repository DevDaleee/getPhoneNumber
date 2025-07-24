
import 'dart:async';

import 'package:flutter/services.dart';

class GetPhoneNumber {
  static const MethodChannel _channel = MethodChannel('get_phone_number');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
