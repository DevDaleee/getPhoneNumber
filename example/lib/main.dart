import 'package:flutter/material.dart';
import 'package:get_phone_number/get_phone_number.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: PhoneNumberScreen(),
    );
  }
}

class PhoneNumberScreen extends StatefulWidget {
  const PhoneNumberScreen({Key? key}) : super(key: key);
  @override
  State<PhoneNumberScreen> createState() => _PhoneNumberScreenState();
}

class _PhoneNumberScreenState extends State<PhoneNumberScreen> {
  String? phone;

  void _getPhone() async {
    final status = await Permission.phone.request();
    if (!status.isGranted) {
      setState(() => phone = 'Permissão negada');
      return;
    }

    final number = await GetPhoneNumber.getPhoneNumber();
    setState(() => phone = number ?? 'Número não disponível');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Número do SIM')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(phone ?? 'Clique no botão abaixo'),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _getPhone,
              child: const Text('Obter número do chip'),
            ),
          ],
        ),
      ),
    );
  }
}
