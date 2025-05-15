package rs.ac.singidunum.app;

import java.io.InputStream;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.*;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;
import org.apache.jena.reasoner.*;
import org.apache.jena.vocabulary.RDF;

public class Main {

	public static void main(String[] args) {
		// ==========================================
        // Učitavanje RDF modela iz resources/
        // ==========================================

        // Koristimo ClassLoader da učitamo fajl iz src/main/resources
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream in = classLoader.getResourceAsStream("k2.rdf");

        if (in == null) {
            System.err.println("Nije moguće pronaći rdf u resources folderu.");
            return;
        }

        // Kreiramo prazni bazni model i učitavamo RDF sadržaj u njega
        Model baseModel = ModelFactory.createDefaultModel();
        baseModel.read(in, null, FileUtils.langXML); // RDF/XML format

        // ==========================================
        // Kreiranje inference modela
        // ==========================================

        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner(); // koristi rdfs:subClassOf itd.
        InfModel infModel = ModelFactory.createInfModel(reasoner, baseModel);

        // ==========================================
        // 3. Ispisivanje RDF tipova za instancu hirurg5
        // ==========================================

        String ns = "http://www.singidunum.ac.rs/hospital#";
        Resource hirurg = infModel.getResource(ns + "hirurg5");

        System.out.println("Tipovi za instancu: hirurg5");
        StmtIterator stmtIterator = infModel.listStatements(hirurg, RDF.type, (RDFNode) null);
        while (stmtIterator.hasNext()) {
            Statement stmt = stmtIterator.nextStatement();
            RDFNode tip = stmt.getObject();
            System.out.println("→ " + tip.toString());
        }
        
        System.out.println("\n--- Sve osobe ---");

        String queryOsobe = """
            PREFIX hsp: <http://www.singidunum.ac.rs/hospital#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

            SELECT DISTINCT ?osoba
            WHERE {
                ?osoba rdf:type ?tip .
                ?tip rdfs:subClassOf* hsp:Osoba .
            }
            """;

        QueryExecution qeOsobe = QueryExecutionFactory.create(QueryFactory.create(queryOsobe), infModel);
        ResultSet rsOsobe = qeOsobe.execSelect();
        ResultSetFormatter.out(rsOsobe);
        qeOsobe.close();


        
        System.out.println("\n--- Specijalizacije za doktora: hirurg5 ---");

        String querySpecijalizacije = """
            PREFIX hsp: <http://www.singidunum.ac.rs/hospital#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

            SELECT ?specijalizacija
            WHERE {
                hsp:hirurg5 hsp:imaSpecijalizaciju ?specijalizacija .
            }
            """;

        QueryExecution qeSpec = QueryExecutionFactory.create(QueryFactory.create(querySpecijalizacije), infModel);
        ResultSet rsSpec = qeSpec.execSelect();
        ResultSetFormatter.out(rsSpec);
        qeSpec.close();
        
        System.out.println("\n--- Osobe sa dijagnozom anosmija ---");

        String query1 = """
            PREFIX hsp: <http://www.singidunum.ac.rs/hospital#>

            SELECT ?ime
            WHERE {
                ?osoba hsp:imaDijagnozu hsp:anosmija .
                ?osoba hsp:imaImeIPrezime ?ime .
            }
            """;

        QueryExecution qe1 = QueryExecutionFactory.create(QueryFactory.create(query1), infModel);
        ResultSet rs1 = qe1.execSelect();
        ResultSetFormatter.out(rs1);
        qe1.close();

        
        System.out.println("\n--- Najmanja plata hirurga koji se bave neurohirurgijom ---");

        String query2 = """
            PREFIX hsp: <http://www.singidunum.ac.rs/hospital#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

            SELECT (MIN(xsd:integer(?plata)) AS ?najmanjaPlata)
            WHERE {
                ?hirurg rdf:type hsp:Hirurg .
                ?hirurg hsp:imaSpecijalizaciju hsp:Neurohirurgija .
                ?hirurg hsp:imaPlatu ?plata .
            }
            """;

        QueryExecution qe2 = QueryExecutionFactory.create(QueryFactory.create(query2), infModel);
        ResultSet rs2 = qe2.execSelect();
        ResultSetFormatter.out(rs2);
        qe2.close();
        
        System.out.println("\n--- Ortopedi čija je plata veća od 450 ---");

        String query3 = """
            PREFIX hsp: <http://www.singidunum.ac.rs/hospital#>
            PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

            SELECT ?ime ?plata
            WHERE {
                ?doktor rdf:type hsp:Doktor .
                ?doktor hsp:imaSpecijalizaciju hsp:Ortoped .
                ?doktor hsp:imaImeIPrezime ?ime .
                ?doktor hsp:imaPlatu ?plata .
                FILTER(xsd:integer(?plata) > 450)
            }
            """;

        QueryExecution qe3 = QueryExecutionFactory.create(QueryFactory.create(query3), infModel);
        ResultSet rs3 = qe3.execSelect();
        ResultSetFormatter.out(rs3);
        qe3.close();



        System.out.println("\n--- Broj doktora (koji nisu hirurzi) po specijalizaciji ---");

        String query4 = """
            PREFIX hsp: <http://www.singidunum.ac.rs/hospital#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

            SELECT ?specijalizacija (COUNT(?doktor) AS ?brojDoktora)
            WHERE {
                ?doktor rdf:type hsp:Doktor .
                FILTER NOT EXISTS { ?doktor rdf:type hsp:Hirurg } .
                ?doktor hsp:imaSpecijalizaciju ?specijalizacija .
            }
            GROUP BY ?specijalizacija
            """;

        QueryExecution qe4 = QueryExecutionFactory.create(QueryFactory.create(query4), infModel);
        ResultSet rs4 = qe4.execSelect();
        ResultSetFormatter.out(rs4);
        qe4.close();

	}

}
