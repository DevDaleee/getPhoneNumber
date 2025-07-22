import 'package:flutter_test/flutter_test.dart';
import 'package:get_phone_number/get_phone_number.dart';

void main() {
  test('getPhoneNumber should return a result or null', () async {
    final number = await GetPhoneNumber.getPhoneNumber();
    expect(number, isA<String?>());
  });
}
