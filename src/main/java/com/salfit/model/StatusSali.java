package com.salfit.model;

// Enum okreslajacy w jakim stanie jest sala treningowa.
public enum StatusSali {
    DOSTEPNA,    // sala wolna, mozna ja zarezerwowac/uzywac
    ZAJETA,      // sala aktualnie w uzyciu
    W_REMONCIE   // sala niedostepna
}
