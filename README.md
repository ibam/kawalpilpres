kawalpilpres
============

Public repo for the backend code of [kawalpilpres.appspot.com](http://kawalpilpres.appspot.com/), a web app for collaborative public crowdsourcing of ballot box verifications for Indonesian Presidential Election of 2014.

The web app is hosted on Google App Engine.

List of technical features:
1. Single-page application.
2. Integrated with the [API provided by Komisi Pemilihan Umum](http://dapil.kpu.go.id/dokumentasi.php) (KPU - General Elections Commission in Indonesia).
3. Triple cached data access layer to reduce load on the KPU servers.
4. Encrypted ballot box identifier for additional layer of security.
5. Designed for high scalability.
