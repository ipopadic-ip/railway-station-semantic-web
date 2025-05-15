# Railway Station Semantic Web Model

This project is a task for the Web-Based Information Systems course. The goal is to model a railway station using RDF, load and query it with Apache Jena, and demonstrate reasoning and SPARQL querying capabilities.

## 🧠 Project Description

The task includes:

1. **Creating an RDF model** for a railway station using the namespace `http://www.singidunum.ac.rs/voz` (prefix: `voz`).
2. **Defining ontology classes**, including:
   - `Zaposleni` (Employee) – abstract class
   - `StanicniRadnik` (StationWorker) – subclass of `Zaposleni`
   - `Masinovodja` (TrainDriver) – subclass of `Zaposleni`
   - `Voz` (Train)
   - `Vagon` (Wagon)
   - `Lokomotiva` (Locomotive) – subclass of `Vagon`
   - `PutnickiVagon` (PassengerWagon) – subclass of `Vagon`
3. **Defining properties**, including:
   - `upravlja` (drives) – domain: `Masinovodja`, range: `Voz`
   - `imaIme` (hasName) – domain: `Zaposleni`, range: `xsd:string`
   - `imaPlatu` (hasSalary) – domain: `Zaposleni`, range: `xsd:int`
   - `imaLokomotivu` (hasLocomotive) – domain: `Voz`, range: `Lokomotiva`
   - `imaSledeciVagon` (hasNextWagon) – domain: `Vagon`, range: `Vagon`
   - `imaBrojSedista` (hasSeatCount) – domain: `PutnickiVagon`, range: `xsd:int`
4. **Creating multiple RDF instances** of employees, trains, locomotives, and wagons.
5. **Loading the RDF model using Apache Jena** and applying reasoning using an inference model.
6. **Printing all RDF types** of a selected instance.
7. **Using `SimpleSelector` in Apache Jena** to:
   - Retrieve all employees.
   - Retrieve employees named "Marko" with salary > 300.
   - Retrieve all passenger wagons with more than 20 seats.
8. **Writing SPARQL queries** for:
   - Calculating the average salary of all employees.
   - Finding all passenger wagons with more than 20 seats.
   - Finding wagons that are **not** the last in the train.
   - Finding wagons that **are** the last in the train.
   - Finding trains that have a passenger wagon as the **second** wagon after the locomotive (pattern: locomotive → any wagon → passenger wagon).

## 🚀 Technologies Used

- Apache Jena
- RDF / RDFS / OWL
- SPARQL
- Java
