package com.salfit.model;

// Enum ze statusami karnetu - liczony "na zywo" na podstawie daty waznosci
public enum StatusKarnetu {
    AKTYWNY,         // karnet dziala normalnie, jeszcze daleko do konca
    WYGASL,          // karnet juz nie dziala, data "do" minela
    WYGASA_WKROTCE   // karnet jeszcze dziala, ale konczy sie w najblizszych dniach
}
