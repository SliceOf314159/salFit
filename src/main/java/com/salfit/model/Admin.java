package com.salfit.model;

// Prosty model admina - czyli osoby ktora moze zalogowac sie do panelu administracyjnego.
// Bez setterow bo to klasa-rekord danych, pola sa ustawiane przez Gson przy wczytywaniu z JSON-a
// albo przez refleksje
public class Admin {

    private String id;
    private String login;
    private String haslo; // hasłо w plaintext

    public String getId()    { return id; }
    public String getLogin() { return login; }
    public String getHaslo() { return haslo; }
}
