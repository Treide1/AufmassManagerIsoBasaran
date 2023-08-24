# Aufmass Manager Iso-Basaran GmbH

INFO: Dies ist ein Prototyp für Iso-Basaran GmbH. Dies stellt nicht die endgültige Version dar. 

## Architektur

Android <-> Firebase (Firestore NoQSL-Datenbank in der Cloud) -> Zapier (Webhook) -> Google Sheets (Tabellen)

### Einschränkungen

Firestore: Free Tier, Könnte ab gewisser Größe kostenpflichtig werden.
Zapier: "Free Trial (Professional) ends on September 06, 2023" -> Übergang in Free Tier (?)