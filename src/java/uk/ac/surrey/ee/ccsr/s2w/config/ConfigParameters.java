package uk.ac.surrey.ee.ccsr.s2w.config;

import javax.servlet.ServletContext;

public class ConfigParameters {

    public String objType = "";
    
    public String ONTOLOGY_LOCAL_URL = "";
    
    public String username_mysql = "";
    public String password_mysql = "";
    public String mysql_db_url = "";
    
    public String onto_URI = "";
    public String objTypeUniqueProperty = "";
    
    public int training_set_min = 1;
    public String STM_1 = "";
    public String STM_2 = "";
    public String LDA_model = "";
    public String unseen_concepts = "";
    public String index = "";
    
    public String sparqlSelectIDs_template = "";
    public String sparqlSelectAll_template = "";
    
    public Thread retrainEngineThread;	// = new Thread(this);
    public Thread retrainAfterTrain;	// = new Thread(this); used for dataset registration.
    
    public String descTypeLinkSuffix = "";
    
    public String datasetFilePath = "";
    public String sparqlAssoc_template ="";

    

    
    public ConfigParameters() {

        this.username_mysql = "root";
        this.password_mysql = "root";

        this.training_set_min = 10;

    }

    public ConfigParameters(ServletContext context) {
        // TODO Auto-generated constructor stub

        this.username_mysql = context.getInitParameter("username_mysql");
        this.password_mysql = context.getInitParameter("password_mysql");

        this.training_set_min = Integer.parseInt(context.getInitParameter("training_set_min"));

    }
    
    public String getSparqlAssoc_template() {
        return sparqlAssoc_template;
    }
    
    public String getDatasetFilePath() {
        return datasetFilePath;
    }
    
    public String getDescTypeLinkSuffix() {
        return descTypeLinkSuffix;
    }
    
     public String getONTOLOGY_LOCAL_URL() {
        return ONTOLOGY_LOCAL_URL;
    }
     
     public Thread getRetrainEngineThread() {
        return retrainEngineThread;
    }
    
    public Thread getRetrainAfterTrain() {
        return retrainAfterTrain;
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
        return STM_1;
    }

    public String getSTM2() {
        return STM_2;
    }

    public String getLdaModel() {
        return LDA_model;
    }

    public String getUnseenConcepts() {
        return unseen_concepts;
    }

    public String getIndex() {
        return index;
    }

    public String getSparqlSelectIDs_template() {
        return sparqlSelectIDs_template;
    }

    public int getTraining_set_min() {
        return training_set_min;
    }

    public String getSparqSelectAll_template() {
        return sparqlSelectAll_template;
    }

    public String getUsername_mysql() {
        return username_mysql;
    }

    public String getPassword_mysql() {
        return password_mysql;
    }
}
