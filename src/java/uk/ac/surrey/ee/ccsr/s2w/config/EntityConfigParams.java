package uk.ac.surrey.ee.ccsr.s2w.config;

import javax.servlet.ServletContext;

public class EntityConfigParams extends ConfigParameters {

    public static final String objType = "entity";
    public static final String ONTOLOGY_LOCAL_URL = "/IoTA-Models/VirtualEntityModel.owl";
    public static final String mysql_db_url = "jdbc:mysql://localhost:3306/entitydb";
    public static final String onto_URI = "http://www.surrey.ac.uk/ccsr/ontologies/VirtualEntityModel.owl#"; //early version was "/IoT-A"
    public static final String objTypeUniqueProperty = onto_URI + "hasLocalIdentifier";
    public static final String STM_1 = "/prob_engine/iot-a/Entity/STM1.data";
    public static final String STM_2 = "/prob_engine/iot-a/Entity/STM2.data";
    public static final String LDA_model = "/prob_engine/iot-a/Entity/LDA_Model.data";
    public static final String unseen_concepts = "/prob_engine/iot-a/Entity/unseen.data";
    public static final String index = "/prob_engine/iot-a/Entity/index.db4o";
    public static Thread retrainEngineThread;	// = new Thread(this);
    public static Thread retrainAfterTrain;	// = new Thread(this); used for dataset registration.
    public static String descTypeLinkSuffix = "/iot-a/" + objType;
    public static final String sparqlSelectIDs_template = "/sparql-templates/iot-a/entity/sparql-select-id.txt";
    public static final String sparqlSelectAll_template = "/sparql-templates/iot-a/entity/sparql-select-all.txt";
    public static final String datasetFilePath = "/dataset/iot-a/entity/dataset.txt";
    public static final String sparqlAssoc_template = "/sparql-templates/iot-a/entity/sparql-select-assoc.txt";
    
    public static final String IDE_project_local_Link = "web"; //NETBEANS
    public ServletContext context;

    public EntityConfigParams() {

        //super(objType, mysql_db_url, onto_URI, objTypeUniqueProperty, STM_1, STM_2, LDA_model, unseen_concepts, index, sparqlSelectIDs_template, sparqlSelectAll_template);
        super();
    }

    public EntityConfigParams(ServletContext context) {
        // TODO Auto-generated constructor stub

        //super(context, objType, mysql_db_url, onto_URI, objTypeUniqueProperty, STM_1, STM_2, LDA_model, unseen_concepts, index, sparqlSelectIDs_template, sparqlSelectAll_template);
        super(context);
        this.context = context;
    }
    
    public Thread getRetrainAfterTrain() {
        return retrainAfterTrain;
    }

    public String getSparqlAssoc_template() {

        try {
            return context.getRealPath(sparqlAssoc_template);
        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + sparqlAssoc_template;
        }
    }

    public String getDatasetFilePath() {
        
        try {
            return context.getRealPath(datasetFilePath);
        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + datasetFilePath;
        }
        
    }

    public String getDescTypeLinkSuffix() {
        return descTypeLinkSuffix;
    }

    public Thread getRetrainEngineThread() {
        return retrainEngineThread;
    }

    public void setRetrainEngineThread(Thread retrainEngineThread) {
        EntityConfigParams.retrainEngineThread = retrainEngineThread;
    }

    public String getONTOLOGY_LOCAL_URL() {

        try {
            return context.getRealPath(ONTOLOGY_LOCAL_URL);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + ONTOLOGY_LOCAL_URL;
        }
    }

    public String getObjType() {
        return objType;
    }

    public String getMysqlDbUrl() {
        return mysql_db_url;
    }

    public String getOntoUri() {
        return onto_URI;
    }

    public String getObjTypeUniqueProperty() {
        return objTypeUniqueProperty;
    }

    public String getSTM1() {
        
        try {
            return context.getRealPath(STM_1);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + STM_1;
        }        
        
    }

    public String getSTM2() {
        
        try {
            return context.getRealPath(STM_2);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + STM_2;
        } 
        
    }

    public String getLdaModel() {
        
        try {
            return context.getRealPath(LDA_model);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + LDA_model;
        } 
        
    }

    public String getUnseenConcepts() {
        
        try {
            return context.getRealPath(unseen_concepts);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + unseen_concepts;
        } 
        
    }

    public String getIndex() {
        
        try {
            return context.getRealPath(index);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + index;
        } 
    }

    public String getSparqlSelectIDs_template() {
        
        try {
            return context.getRealPath(sparqlSelectIDs_template);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + sparqlSelectIDs_template;
        } 
    }

    public String getSparqSelectAll_template() {
        
        try {
            return context.getRealPath(sparqlSelectAll_template);

        } catch (NullPointerException e) {
            System.out.println("no context");
            return IDE_project_local_Link + sparqlSelectAll_template;
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
