# SalFit

> System do zarządzania siłownią i salami fitness — projekt dydaktyczny realizowany w Java + JavaFX.

---

## Zespół

| Imię i nazwisko | GitHub |
|---|---|
| Konrad Mytnik | [@SliceOf314159](https://github.com/SliceOf314159) |
| Bartek Małecki | [@malkiinho](https://github.com/malkiinho) |

---

## Opis projektu

SalFit to system służący do kompleksowego zarządzania organizacją siłowni i sal fitness. Umożliwia:

- **Administratorom** — dodawanie i edytowanie profili trenerów, zarządzanie statusem sal (dostępna / zajęta / w remoncie), tworzenie grafiku zajęć oraz przypisywanie trenerów do konkretnych sal i terminów
- **Obsłudze** — rejestrację członków, śledzenie karnetów oraz generowanie podstawowych raportów obłożenia

Celem systemu jest usprawnienie codziennej obsługi obiektu i zastąpienie ręcznego planowania jednym centralnym narzędziem.

### Stack technologiczny

| Warstwa | Technologia |
|---|---|
| Język | Java 21 |
| Interfejs graficzny | JavaFX |
| Build tool | Maven |
| Persystencja | Lokalny plik (mock bazy danych) |

> Dane przechowywane są lokalnie w pliku jako celowe uproszczenie na potrzeby projektu dydaktycznego — bez użycia zewnętrznego silnika bazodanowego.

---

## Wymagania

- **Java 21+** — [pobierz](https://adoptium.net/)
- **Maven 3.8+** — [pobierz](https://maven.apache.org/download.cgi)

Sprawdź wersje:
```bash
java -version
mvn -version
```

---

## Uruchomienie

### 1. Klonowanie repozytorium

```bash
git clone https://github.com/<org>/salfit.git
cd salfit
```

### 2. Kompilacja

```bash
mvn clean compile
```

### 3. Uruchomienie

```bash
mvn javafx:run
```

### 4. Budowanie paczki (JAR)

```bash
mvn clean package
```

Plik JAR zostanie wygenerowany w katalogu `target/`.

---

## Struktura projektu

```
salfit/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/salfit/
│   │   │       ├── controller/   # Kontrolery JavaFX
│   │   │       ├── model/        # Modele danych
│   │   │       ├── service/      # Logika biznesowa
│   │   │       └── repository/   # Warstwa persystencji (mock)
│   │   └── resources/
│   │       └── fxml/             # Widoki FXML
│   └── test/
│       └── java/                 # Testy jednostkowe
├── pom.xml
└── README.md
```

---
