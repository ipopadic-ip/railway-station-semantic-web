# RDF Project – Hospital Ontology (Task 2)

This project demonstrates the creation, loading, and querying of RDF data using **Apache Jena** and **SPARQL**, based on a given hospital ontology specification.

## Description

The goal of this task is to model a hospital ontology, define classes, properties, and instances, and perform various types of queries and inferences.

### 1. RDF Document

An RDF document is created using the following namespace:

- **Namespace URI:** `http://www.singidunum.ac.rs/hospital`
- **Prefix:** `hsp`

### 2. Classes

The following classes and subclass relationships are defined:

- `Osoba` (Person)
  - `Doktor` (Doctor) — subclass of `Osoba`
    - `Hirurg` (Surgeon) — subclass of `Doktor`
  - `Pacijent` (Patient) — subclass of `Osoba`
- `Dijagnoza` (Diagnosis)

### 3. Properties

The following properties are defined, with domain and range:

- `imaImeIPrezime`  
  - Domain: `Osoba`  
  - Range: `xsd:string`

- `imaSpecijalizaciju`  
  - Domain: `Doktor`  
  - Range: `Specijalizacija` (not defined in RDF but from the same namespace)

- `imaDijagnozu`  
  - Domain: `Osoba`  
  - Range: `Dijagnoza`

- `imaPlatu`  
  - Domain: `Doktor`  
  - Range: `xsd:int`

### 4. Instances

Defined individuals with assigned properties:

#### Patients

- `pacijent155`
  - `imaImeIPrezime`: Marko Markovic  
  - `imaDijagnozu`: anosmija, fraktura nosa

- `pacijent65`
  - `imaImeIPrezime`: Dejan Pavlovic  
  - `imaDijagnozu`: anosmija, prehlada

#### Doctors

- `doktor15`
  - `imaImeIPrezime`: Petar Petrovic  
  - `imaSpecijalizaciju`: Ortoped  
  - `imaPlatu`: 500

- `hirurg5`
  - `imaImeIPrezime`: Stevan Stevanovic  
  - `imaSpecijalizaciju`: Rekonstrukcija, Neurohirurgija  
  - `imaPlatu`: 800

### 5. Apache Jena Tasks

Using the **Apache Jena** framework:

- The RDF model is loaded programmatically.
- A reasoning model is created using Jena’s inference capabilities.
- All RDF types of the instance `hirurg5` are listed.

### 6. Selectors (SimpleSelector API)

The following custom Jena selectors are implemented:

- Select all instances of class `Osoba`
- Select all specializations (`imaSpecijalizaciju`) for a specific doctor

### 7. SPARQL Queries (via Protégé or Jena)

The following SPARQL queries are created and executed:

1. Retrieve names of all people who have "anosmija" as a diagnosis
2. Retrieve the minimum salary of surgeons specialized in "Neurohirurgija"
3. Retrieve all orthopedists with a salary greater than 450
4. Count all doctors (excluding surgeons), grouped by specialization

---

## How to Run

Make sure you have Maven and Java installed. From the `task2` directory, run:

```bash
mvn clean install
# Then run your main Java class (via IDE or exec plugin)
