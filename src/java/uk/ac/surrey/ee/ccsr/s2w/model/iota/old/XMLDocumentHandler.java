/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.old;

/**
 *
 * @author Payam
 */
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.DOMSource;
import uk.ac.surrey.ee.ccsr.s2w.util.Utils;

public class XMLDocumentHandler {

    public String createSimpleOntology(Source xsltSource, ArrayList element, ArrayList text, String descType) throws TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        String xmlString = "";

        try {
            docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            createXMLDocument(doc, element, text, descType);

            //transformation
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans = transfac.newTransformer(xsltSource);

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);

            xmlString = sw.toString();

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLDocumentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(xmlString);
        return xmlString;
    }

    Document createXMLDocument(Document doc, ArrayList element, ArrayList value, String descType) throws ParserConfigurationException {

        Element firstLevel=null;
        
        if (descType.equalsIgnoreCase("resource")) {
            
        Element root = doc.createElement("resources");
        doc.appendChild(root);        

        //Element firstLevel = doc.createElement("resource");
        firstLevel = doc.createElement("resource");
        root.appendChild(firstLevel);
        }
        else if (descType.equalsIgnoreCase("entity")) {
            
            Element root = doc.createElement("entities");
        doc.appendChild(root);        

        firstLevel = doc.createElement("entity");
        root.appendChild(firstLevel);        
        }
           else {
            Element root = doc.createElement("services");
        doc.appendChild(root);        

        firstLevel = doc.createElement("service");
        root.appendChild(firstLevel);        
        }  
        
        
        int i = 0;

        while (i < element.size()) {
            if (element != null && value != null && value.size() > 0) {
                String elementNode = (String) element.get(i);
                String elementText = (String) value.get(i);

                if (elementNode != null && elementText != null) {
                    Element child = doc.createElement(elementNode);
                    firstLevel.appendChild(child);
                    //System.out.println("element:" + child.getTagName());

                    Text text = doc.createTextNode(elementText);
                    //System.out.println("element:" + text.getWholeText());
                    child.appendChild(text);
                    System.out.println("child:" + child.getTextContent());
                }
            }
            i++;
        }

        String output = Utils.dom2string(doc);
        System.out.println("xml doc:" + output);
//        System.out.println("xml doc Text Content:" + root.getTextContent());

//        // store doc in a file
//        Utils.writeXmlFile(doc, "dom.xml");

        return doc;

    }
}
