package com.gavin.mpesatest

data class StkCallback(
    val CallbackMetadata: CallbackMetadata,
    val CheckoutRequestID: String,
    val MerchantRequestID: String,
    val ResultCode: Int,
    val ResultDesc: String
)