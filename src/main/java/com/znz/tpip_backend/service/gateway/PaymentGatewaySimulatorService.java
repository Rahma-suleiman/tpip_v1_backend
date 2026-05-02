package com.znz.tpip_backend.service.gateway;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentGatewaySimulatorService {

    public String simulateMpesaPayment(String reference, Double amount) {

        return "MPESA-" + new Random().nextInt(999999);
    }

    public String simulateTigoPesaPayment(String reference, Double amount) {

        return "TIGO-" + new Random().nextInt(999999);
    }
}