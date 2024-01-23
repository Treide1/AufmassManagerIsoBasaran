# Aufmass-Manager Iso-Basaran

Anwendung zur Verwaltung von Bauvorhaben und dafür vorgenommene Aufmaße für die Firma Iso-Basaran GmbH.

Auftraggeber: Nicolas Lehmann

## Installation

[TODO: als Firebase Beta-Nutzer einrichten, oder apk per Filesharing schicken]

## Verwendung

Es gibt eine Erklärung auf der "Informationen"-Ansicht, dem Startbildschirm.
Dieser erklärt die Verwendung der App.

### Ansichten (Screens)

* Informationen
* Bauvorhaben hinzufügen
* Bauvorhaben auswählen
* Aufmaß hinzufügen
* Spezial-Aufmaß hinzufügen
* Excel exportieren

## Architektur

### Android-Anwendung

Nutzer-Verständnis:
* Erklärung durch Informationen-Ansicht
* Anzeige des Synchronisations-Status
  
Wesentliche Funktionen:
* Eingaben mittels Formulare, werden in Datenbank geschrieben
* Auswahl von aktivem Bauvorhaben durch Suchfeld
* Excel exportieren

Übrige Funktionen:
* Anmeldung mit Firmen-Account [TODO]
* Firmen-Icon als App-Icon

### Backend

Firebase (Google Cloud Platform  + Backend-as-a-Service)

Cloud-Datenbank: Firstore 
* NoSQL, dokumentenorientiert, wird nach Anzahl der Lese- und Schreibzugriffe abgerechnet)

## Datenmodellierung

### Datenbank

Aufbau der Datenbank in Firestore:

* Sammlung: `bauvorhaben`
  * Dokument (Model): `<ID>` (automatisch generiert)
    * `name`: string (eindeutig)
    * `aufmassNummer`: number
    * `auftragsNummer`: number
    * `notiz`: string
    * `zeitstempel`: timestamp (automatisch generiert)
    * Untersammlung: `eintraege` 
      * Dokument (Model):  (siehe: `Eintrag`)
    * Untersammlung: `spezialEintraege` 
      * Dokument (Model):  (siehe: `SpezialEintrag`)

`Eintrag` = Dokument (Model)
  * `bereich`: string
  * `durchmesser`: number
  * `isolierung`: string
  * `gewerk`: string
  * `meterListe`: array<number>
  * `meterSumme`: number
  * `bogen`: number
  * `stutzen`: number
  * `ausschnitt`: number
  * `passstueck`: number
  * `endstelle`: number
  * `halter`: number
  * `flansch`: number
  * `ventil`: number
  * `schmutzfilter`: number
  * `dreiWegeVentil`: number
  * `notiz`: string
  * `zeitstempel`: timestamp (automatisch generiert)

`SpezialEintrag` = Dokument (Model)
  * `bereich`: string
  * `daten`: string
  * `notiz`: string
  * `zeitstempel`: timestamp (automatisch generiert)

* Sammlung: `meta`
  * Dokument: `bauvorhaben`
    * `projection_name`: array<string> (Pseudo-SQL-Abfrage: FROM `bauvorhaben` SELECT `name`)