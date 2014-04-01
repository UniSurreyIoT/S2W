/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.ClientAcceptFormatter;

/**
 * Resource which has only one representation.
 */
public class RestletSparqlResource extends ServerResource {

    Parameters param = new Parameters();

    @Get
    public Representation sparqlGet() {

        String objType = (String) getRequest().getResourceRef().getPath();
        String queryString = (String) getQuery().getFirstValue("sparql");
        String format = (String) getQuery().getFirstValue("resultFormat");
        MediaType mt = getClientInfo().getAcceptedMediaTypes().get(0).getMetadata();
        
        try{
        if (format==null)
            format="XML";
        }catch (NullPointerException npe){
        format="XML";
        }

        String queryResult = "";
        queryResult = executeQuery(objType, queryString, format);

        StringRepresentation resultInFormat = new StringRepresentation(queryResult);
        
        ClientAcceptFormatter sparqlFormat= new ClientAcceptFormatter();
        resultInFormat = sparqlFormat.formatSparql(resultInFormat, format, mt);

        return resultInFormat;

    }

    @Post
    public Representation sparqlPost(Representation entity) throws ResourceException, IOException {

        String objType = (String) getRequest().getResourceRef().getPath();
        String queryString = entity.getText();
        String format = (String) getQuery().getFirstValue("resultFormat");
        MediaType mt = getClientInfo().getAcceptedMediaTypes().get(0).getMetadata();
        System.out.println("Request Accepts is: "+mt);

//        if (StringUtils.equals(null, (String) getQuery().getFirstValue("resultFormat"))) {
//            format = "XML";
//        }

        try{
        if (format==null)
            format="XML";
        }catch (NullPointerException npe){
        format="XML";
        }

        String queryResult = "";
        queryResult = executeQuery(objType, queryString, format);
        
        StringRepresentation resultInFormat = new StringRepresentation(queryResult);
        
        ClientAcceptFormatter sparqlFormat= new ClientAcceptFormatter();
        resultInFormat = sparqlFormat.formatSparql(resultInFormat, format, mt);

        return resultInFormat;

    }

    public String executeQuery(String objType, String queryString, String format) {

        ServletContext context = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
        System.out.println(context.getRealPath(""));

        ConfigParameters cParams = null;
        if (objType.contains(ResourceConfigParams.objType)) {
            cParams = new ResourceConfigParams(context);
        } else if (objType.contains(EntityConfigParams.objType)) {
            cParams = new EntityConfigParams(context);
        } else if (objType.contains(ServiceConfigParams.objType)) {
            cParams = new ServiceConfigParams(context);
        } else {
            return param.invalidRepoMsg;
        }

//        if (objType.contains(ResourceConfigParams.objType)) {
//            cParams = new ResourceConfigParams();
//        } else if (objType.contains(EntityConfigParams.objType)) {
//            cParams = new EntityConfigParams();
//        } else if (objType.contains(ServiceConfigParams.objType)) {
//            cParams = new ServiceConfigParams();
//        } else {
//            return param.invalidRepoMsg;
//        }

        SdbAccessHandler sdbAH = new SdbAccessHandler(cParams);
        Dataset dataset = DatasetStore.create(sdbAH.getStore());

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, dataset);

        String queryResult = "";
        
        format=format.toUpperCase();

        if (qe.getQuery().isSelectType()) {
            try {

                ResultSet rs = qe.execSelect();
                //ResultSetFormatter.out(rs);
                if (format.equalsIgnoreCase("TXT")) {
                    queryResult = ResultSetFormatter.asText(rs);
                } else if (format.equalsIgnoreCase("JSON")) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ResultSetFormatter.outputAsJSON(bos, rs);
                    queryResult = bos.toString();
                } else if (format.equalsIgnoreCase("CSV")) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ResultSetFormatter.outputAsCSV(bos, rs);
                    queryResult = bos.toString();
                } else if (format.equalsIgnoreCase("TSV")) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ResultSetFormatter.outputAsTSV(bos, rs);
                    queryResult = bos.toString();
                } else if (format.equalsIgnoreCase("BIO")) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ResultSetFormatter.outputAsBIO(bos, rs);
                    queryResult = bos.toString();
                } else {
                    queryResult = ResultSetFormatter.asXMLString(rs);
                }
            } catch (Exception e) {
                return e.getMessage();
            } finally {
                qe.close();
                dataset.close();
                sdbAH.getStore().close();
            }
        } else if (qe.getQuery().isConstructType()) {
            try {
                Model results = qe.execConstruct();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                results.write(bos, format);
                //ResultSetFormatter.outputAsJSON(bos, true);
                queryResult = bos.toString();

            } finally {
                qe.close();
                dataset.close();
                sdbAH.getStore().close();
            }
        } else if (qe.getQuery().isDescribeType()) {
            try {

                Model resultModel = qe.execDescribe();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                resultModel.write(bos, format);
                //ResultSetFormatter.outputAsJSON(bos, true);
                queryResult = bos.toString();

            } finally {
                qe.close();
                dataset.close();
                sdbAH.getStore().close();
            }

        } else if (qe.getQuery().isAskType()) {
            try {
                boolean result = qe.execAsk();
                queryResult = String.valueOf(result);
            } finally {
                qe.close();
                dataset.close();
                sdbAH.getStore().close();
            }

        }

        return queryResult;
    }

    public static void main(String[] args) {

        String queryString = "";

        ConfigParameters cParams = new ResourceConfigParams();
        cParams.password_mysql = "root";
        cParams.username_mysql = "root";

        try {
            queryString = org.apache.commons.io.FileUtils.readFileToString(new File("web/" + ResourceConfigParams.sparqlSelectIDs_template));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        queryString = queryString + "http://www.surrey.ac.uk/ccsr/ontologies/ResourceModel.owl#Resource_36_BA_01_power_sensor>)}";


        String queryResult = "";
        RestletSparqlResource sRes = new RestletSparqlResource();
        queryResult = sRes.executeQuery(ResourceConfigParams.objType, queryString, "xml");
        System.out.println("Result is: \n" + queryResult);

    }
}
