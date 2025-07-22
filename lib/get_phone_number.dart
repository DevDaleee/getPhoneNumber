import 'package:flutter/services.dart';

class GetPhoneNumber {
  static const MethodChannel _channel = MethodChannel('get_phone_number');

  static Future<String?> getPhoneNumber() async {
    try {
      return await _channel.invokeMethod<String>('getPhoneNumber');
    } on PlatformException catch (e) {
      print('Erro ao obter número: ${e.message}');
      return null;
    }
  }
}
