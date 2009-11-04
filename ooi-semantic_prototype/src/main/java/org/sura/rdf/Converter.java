package org.sura.rdf;

import java.net.URI;
import java.net.URL;
import java.util.List;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.RDF;

public class Converter {

	public Converter() {
		Model model = ModelFactory.createOntologyModel();
		URL url = null;
		// read ontology model
		url = getClass().getResource("/om.owl");
		model.read(url.toExternalForm());

		// read netcdf cdm model
		url = getClass().getResource("/cdm.owl");
		model.read(url.toExternalForm());
		// System.out.println(model);
		List rules = Rule.rulesFromURL("fui.rules");

		GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
		// reasoner.setOWLTranslation(true); // not needed in RDFS case
		reasoner.setTransitiveClosureCaching(true);

		InfModel inf = ModelFactory.createInfModel(reasoner, model);

		Resource mbari = ResourceFactory
				.createResource("http://mmisw.org/ont/ooi-ci/fui#MBARI");
		Resource obs1 = ResourceFactory
				.createResource("http://mmisw.org/ont/ooi-ci/cdm#Dataset_1");
		Property omProperty = ResourceFactory
				.createProperty("http://mmisw.org/ont/ooi-ci/om#property");
		String cf_ns = "http://mmisw.org/ont/cf/parameter/";

		StmtIterator iter = inf.listStatements(obs1, null, (RDFNode) null);
		boolean Dataset_1IsMBARIObservation = false;
		boolean cfVariableIsConvertedToMMICfResource = false;
		while (iter.hasNext()) {
			Statement statement = (Statement) iter.next();
			System.out.println(statement);
			if (statement.getPredicate().equals(RDF.type)
					&& statement.getObject().equals(mbari)) {
				Dataset_1IsMBARIObservation = true;
			}

			if (statement.getPredicate().equals(omProperty)
					&& statement.getObject().toString().startsWith(cf_ns)) {
				cfVariableIsConvertedToMMICfResource = true;

			}

			// System.out.println(statement);

		}
		System.out.println("Dataset_1IsMBARIObservation: "
				+ Dataset_1IsMBARIObservation);
		System.out.println("cfVariableIsConvertedToMMICfResource: "
				+ cfVariableIsConvertedToMMICfResource);

	}

	public static void main(String[] args) {
		Converter conv = new Converter();
		conv.convert();
	}

	private void convert() {

	}

}
