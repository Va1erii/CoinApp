package com.valerii.coinapp.network

object ApiKey {
    init {
        System.loadLibrary("native-lib")
    }
    external fun apiKey(): String
}