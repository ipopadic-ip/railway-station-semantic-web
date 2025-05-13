package rs.ac.singidunum.app;

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

import java.io.InputStream;

public class Main {
	 // ==========================================
    // Autor: Ilija
    // ==========================================

	public static void main(String[] args) {
		 // ==========================================
        // 1. Učitavanje RDF modela iz resources/
        // ==========================================

        // Koristimo ClassLoader da učitamo fajl iz src/main/resources
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream in = classLoader.getResourceAsStream("stanica.rdf");

        if (in == null) {
            System.err.println("Nije moguće pronaći rdf u resources folderu.");
            return;
        }

        // Kreiramo prazni bazni model i učitavamo RDF sadržaj u njega
        Model baseModel = ModelFactory.createDefaultModel();
        baseModel.read(in, null, FileUtils.langXML); // RDF/XML format

        // ==========================================
        // 2. Kreiranje inference modela
        // ==========================================

        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner(); // koristi rdfs:subClassOf itd.
        InfModel infModel = ModelFactory.createInfModel(reasoner, baseModel);

        // ==========================================
        // Ispisivanje svih RDF tipova za instancu
        // ==========================================

        String ns = "http://www.singidunum.ac.rs/voz#";
        Resource instanca = infModel.getResource(ns + "marko123");

        System.out.println("Tipovi za instancu: marko123");
        StmtIterator stmtIterator = infModel.listStatements(instanca, RDF.type, (RDFNode) null);
        while (stmtIterator.hasNext()) {
            Statement stmt = stmtIterator.nextStatement();
            RDFNode tip = stmt.getObject();
            System.out.println("→ " + tip.toString());
        }
        
        // ==========================================
        // Selektor za sve zaposlene
        // ==========================================
        System.out.println("\n--- Svi zaposleni (uključujući podklase) ---");

        Resource zaposleniClass = infModel.getResource(ns + "Zaposleni");

        StmtIterator sviZaposleni = infModel.listStatements(null, RDF.type, zaposleniClass);
        while (sviZaposleni.hasNext()) {
            Statement stmt = sviZaposleni.nextStatement();
            System.out.println("Zaposleni: " + stmt.getSubject());
        }
        
	     // ========================================== 
	     // Zaposleni po imenu Marko i plata > 300
	     // ==========================================
	     System.out.println("\n--- Zaposleni koji se zovu Marko i imaju platu > 300 ---");
	
	     Property imaIme = infModel.getProperty(ns + "imaIme");
	     Property imaPlatu = infModel.getProperty(ns + "imaPlatu");
	
	     // Umesto direktnog stringa "Marko", koristi se Literal za poređenje
	     Literal imeMarko = infModel.createLiteral("Marko");
	
	     ResIterator sviSubjekti = infModel.listSubjectsWithProperty(imaIme, imeMarko);
	
	     while (sviSubjekti.hasNext()) {
	         Resource r = sviSubjekti.nextResource();
	
	         Statement plataStmt = r.getProperty(imaPlatu);
	         if (plataStmt != null && plataStmt.getObject().isLiteral()) {
	             Literal plataLiteral = plataStmt.getLiteral();
	
	             try {
	                 int plata = Integer.parseInt(plataLiteral.getLexicalForm());
	                 if (plata > 300) {
	                     System.out.println("Zaposleni: " + r.getURI());
	                 }
	             } catch (NumberFormatException e) {
	                 System.err.println("Greška pri parsiranju plate za: " + r.getURI());
	             }
	         }
	     }

        
		  // ==========================================
		  // Putnički vagoni sa više od 20 mesta
		  // ==========================================
		  System.out.println("\n--- Putnički vagoni sa više od 20 mesta ---");
	
		  Resource putnickiVagonClass = infModel.getResource(ns + "PutnickiVagon");
		  Property imaBrojSedista = infModel.getProperty(ns + "imaBrojSedista");
	
		  ResIterator sviVagoni = infModel.listSubjectsWithProperty(RDF.type, putnickiVagonClass);
	
		  while (sviVagoni.hasNext()) {
		      Resource vagon = sviVagoni.nextResource();
		      Statement brojSedistaStmt = vagon.getProperty(imaBrojSedista);
	
		      if (brojSedistaStmt != null && brojSedistaStmt.getObject().isLiteral()) {
		          Literal sedistaLiteral = brojSedistaStmt.getLiteral();
	
		          try {
		              int brojSedista = Integer.parseInt(sedistaLiteral.getLexicalForm());
		              if (brojSedista > 20) {
		                  System.out.println("Putnički vagon: " + vagon.getURI());
		              }
		          } catch (NumberFormatException e) {
		              System.err.println("Nevalidan broj sedišta za: " + vagon.getURI());
		          }
		      }
		  }


		  // ==========================================
		  // Prosečna plata svih zaposlenih
		  // ==========================================
		  
		  System.out.println("\n--- Prosečna plata svih zaposlenih ---");

		  String query1 = """
				    PREFIX voz: <http://www.singidunum.ac.rs/voz#>
				    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
				    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
				    PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>

				    SELECT (AVG(xsd:integer(?plata)) AS ?prosecnaPlata)
				    WHERE {
				        ?zaposleni rdf:type ?tip .
				        ?tip rdfs:subClassOf* voz:Zaposleni .
				        ?zaposleni voz:imaPlatu ?plata .
				    }
				    """;
		  QueryExecution qe1 = QueryExecutionFactory.create(QueryFactory.create(query1), infModel);
		  ResultSet rs1 = qe1.execSelect();

		  ResultSetFormatter.out(rs1);

		  qe1.close();
		  
		  System.out.println("\n--- Putnički vagoni sa više od 20 mesta ---");

		  String query2 = """
		      PREFIX voz: <http://www.singidunum.ac.rs/voz#>
		      PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
		      PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

		      SELECT ?vagon ?brojSedista
		      WHERE {
		          ?vagon rdf:type voz:PutnickiVagon .
		          ?vagon voz:imaBrojSedista ?brojSedista .
		          FILTER(xsd:integer(?brojSedista) > 20)
		      }
		      """;

		  QueryExecution qe2 = QueryExecutionFactory.create(QueryFactory.create(query2), infModel);
		  ResultSet rs2 = qe2.execSelect();

		  ResultSetFormatter.out(rs2);

		  qe2.close();


		  
		  System.out.println("\n--- Vagoni koji NISU poslednji u vozu ---");

		  String query3 = """
		  PREFIX voz: <http://www.singidunum.ac.rs/voz#>

		  SELECT DISTINCT ?vagon
		  WHERE {
		    ?vagon voz:imaSledeciVagon ?sledeci .
		    ?prethodni voz:imaSledeciVagon ?vagon .  # Vagon mora imati prethodnika
		  }
		  """;

		  QueryExecution qe3 = QueryExecutionFactory.create(QueryFactory.create(query3), infModel);
		  ResultSet rs3 = qe3.execSelect();
		  ResultSetFormatter.out(rs3);
		  qe3.close();



		  
		  System.out.println("\n--- Poslednji vagoni u vozu ---");

		  String query4 = """
		  PREFIX voz: <http://www.singidunum.ac.rs/voz#>
		  SELECT DISTINCT ?vagon
		  WHERE {
		    ?vagon a voz:Vagon .
		    FILTER NOT EXISTS { ?vagon voz:imaSledeciVagon ?biloSta }
		  }
		  """;

		  QueryExecution qe4 = QueryExecutionFactory.create(QueryFactory.create(query4), infModel);
		  ResultSet rs4 = qe4.execSelect();
		  ResultSetFormatter.out(rs4);
		  qe4.close();

		  
		  System.out.println("\n--- Voz sa putničkim vagon kao 2. vagon posle lokomotive ---");

		  String query5 = """
		  PREFIX voz: <http://www.singidunum.ac.rs/voz#>
		  SELECT ?voz ?drugiVagon
		  WHERE {
		    ?voz voz:imaLokomotivu ?lok .
		    ?lok voz:imaSledeciVagon ?prviVagon .
		    ?prviVagon voz:imaSledeciVagon ?drugiVagon .
		    ?drugiVagon a voz:PutnickiVagon .
		  }
		  """;

		  QueryExecution qe5 = QueryExecutionFactory.create(QueryFactory.create(query5), infModel);
		  ResultSet rs5 = qe5.execSelect();
		  ResultSetFormatter.out(rs5);
		  qe5.close();

	}

}
