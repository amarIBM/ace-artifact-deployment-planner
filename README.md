# ace_artifacts_deployment_planner
## How to group together the ACE artifacts for deployment to Container form factor


If you have a large number of artifacts (Applications, REST APIs, Integration Services, Message flows etc) in the form of BAR files or Integration node backup that you are planning to deploy to the ACE Containers, then it is important that you determine an optimal way to group or split the artifacts for deploying to the Integation Runtime in containers to achieve following objectives

- Faster startup times for Integration Runtime
- Optimal memory footprint with logical grouping
- Less disruption to services during config updates 

### **Bar files**

The script `deployment_planner.sh` loops over the directory in which it executes searching for BAR files.
1. For each BAR file discovered, the script creates a work directory, deploys the BAR file in to the work directory, 
2. For each Application/REST API/Service in the 'run' directory , it further creates temporary workspace and moves each Application/REST API/Service there and then runs the ibmint optimize server command against that temporary work directory
3. Once all the applications and bar have been looped over, the script invokes a Java program which parses each of the generated server.components.yaml files to determine which aspects of the integration server's capabilities are needed and amalgamates the data into a consolidated report which shows which applications necessitates the need for each component/functionality.

### **Integration node backup**

The script `deployment_planner.sh` unzips the backup file.
1. Loops over the servers directory and for each server, the script loops over all the applications in the run directory. 
2.  For each Application/REST API/Service in the 'run' directory , it further creates temporary workspace and moves each Application/REST API/Service there and then runs the ibmint optimize server command against that temporary work directory
3. Once all the applications and bar have been looped over, the script invokes a Java program which parses each of the generated server.components.yaml files to determine which aspects of the integration server's capabilities are needed and amalgamates the data into a consolidated report which shows which applications necessitates the need for each component/functionality.

## Sample Output File

   ```
   OVERALL RESULTS OF ANALYZING OPTIMIZATIONS AS FOLLOWS ...

   Type: Bar
   Input: BarFolder
   List of bar files: [TCP_Deploy, BarWithMultipleApps]
   List of apps: [TCP_Deploy|TCPIPClient,  TCP_Deploy|TCPIPServer, BarWithMultipleApps|ExampleRoutingJCN,  BarWithMultipleApps|ExampleRoutingRoute,  BarWithMultipleApps|TCPIPClient,  BarWithMultipleApps|ExampleFileIterator,  BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|TemperatureConverter,  BarWithMultipleApps|ExampleMQ,  BarWithMultipleApps|LargeMessages,  BarWithMultipleApps|HTTPInputApplication]
   Count: 11
   CLR: [TCP_Deploy|TCPIPClient,  TCP_Deploy|TCPIPServer, BarWithMultipleApps|ExampleRoutingJCN,  BarWithMultipleApps|ExampleRoutingRoute,  BarWithMultipleApps|TCPIPClient,  BarWithMultipleApps|ExampleFileIterator,  BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|TemperatureConverter,  BarWithMultipleApps|ExampleMQ,  BarWithMultipleApps|LargeMessages,  BarWithMultipleApps|HTTPInputApplication]
   Deploy: []
   JVM: [TCP_Deploy|TCPIPClient,  TCP_Deploy|TCPIPServer, BarWithMultipleApps|ExampleRoutingJCN,  BarWithMultipleApps|ExampleRoutingRoute,  BarWithMultipleApps|TCPIPClient,  BarWithMultipleApps|ExampleFileIterator,  BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|TemperatureConverter,  BarWithMultipleApps|ExampleMQ,  BarWithMultipleApps|LargeMessages,  BarWithMultipleApps|HTTPInputApplication]
   SSL: []
   NodeJS: [TCP_Deploy|TCPIPClient,  TCP_Deploy|TCPIPServer, BarWithMultipleApps|ExampleRoutingJCN,  BarWithMultipleApps|ExampleRoutingRoute,  BarWithMultipleApps|TCPIPClient,  BarWithMultipleApps|ExampleFileIterator,  BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|TemperatureConverter,  BarWithMultipleApps|ExampleMQ,  BarWithMultipleApps|LargeMessages,  BarWithMultipleApps|HTTPInputApplication]
   Nodes_Dotnet: []
   Nodes_Aggregation: []
   Nodes_Basic: [BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|TemperatureConverter]
   Nodes_CallableFlow: []
   Nodes_Database_ODBC: []
   Nodes_Connector: []
   Nodes_ESQL: [TCP_Deploy|TCPIPClient,  TCP_Deploy|TCPIPServer, BarWithMultipleApps|TCPIPClient,  BarWithMultipleApps|ExampleFileIterator,  BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|ExampleMQ]
   Nodes_Group: []
   Nodes_JMSTransform: []
   Nodes_MQ: [BarWithMultipleApps|ExampleMQ]
   Nodes_REST: [BarWithMultipleApps|CustomerDatabaseV1]
   Nodes_Http: [TCP_Deploy|TCPIPServer, BarWithMultipleApps|ExampleRoutingJCN,  BarWithMultipleApps|ExampleRoutingRoute,  BarWithMultipleApps|HTTPInputApplication]
   Nodes_SecurityPEP: []
   Nodes_Timer: []
   JavaNodes_Adapters: []
   JavaNodes_CICS: []
   JavaNodes_CORBA: []
   JavaNodes_ChangeDataCapture: []
   JavaNodes_Collection: []
   JavaNodes_Email: []
   JavaNodes_File: [TCP_Deploy|TCPIPClient, BarWithMultipleApps|ExampleRoutingJCN,  BarWithMultipleApps|ExampleRoutingRoute,  BarWithMultipleApps|TCPIPClient,  BarWithMultipleApps|ExampleFileIterator,  BarWithMultipleApps|ExampleMQ,  BarWithMultipleApps|LargeMessages]
   JavaNodes_IMS: []
   JavaNodes_JDBC: [BarWithMultipleApps|ExampleRoutingRoute]
   JavaNodes_JMS: []
   JavaNodes_JavaCompute: [BarWithMultipleApps|ExampleRoutingJCN,  BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|LargeMessages]
   JavaNodes_JavaDesignerConnectors: []
   JavaNodes_Kafka: []
   JavaNodes_MQTT: []
   JavaNodes_Mapping: [TCP_Deploy|TCPIPClient, BarWithMultipleApps|TCPIPClient,  BarWithMultipleApps|CustomerDatabaseV1,  BarWithMultipleApps|TemperatureConverter,  BarWithMultipleApps|ExampleMQ,  BarWithMultipleApps|HTTPInputApplication]
   JavaNodes_ODM: []
   JavaNodes_SOAP: [BarWithMultipleApps|TemperatureConverter]
   JavaNodes_TCPIP: [TCP_Deploy|TCPIPClient,  TCP_Deploy|TCPIPServer, BarWithMultipleApps|TCPIPClient]
   JavaNodes_WS-Security: []
   JavaNodes_WSRR: []
   JavaNodes_XSLT: []
   JavaScriptNodes_LoopBack: []
   JavaScriptNodes_Salesforce: []
   ResourceManagers_FTE: []
   ResourceManagers_GlobalCache: []
   ResourceManagers_OpenTelemetry: []
   FlowSecurityProviders_LDAP: []
   FlowSecurityProviders_TFIM: []
   FlowSecurityProviders_WS_Trust: []

   ```

### How to setup and use the utility on Linux:

  1. git clone the repo

  2. Note that deployment_planner.sh sources mqsiprofile so that ibmint command can be executed.  Currently the script defaults to installation path "/opt/IBM/ace-13.0.2.0/server/bin/mqsiprofile".
  If you have used a different product version (12.0.12.0 or above) or a different install location, you can simply edit deployment_planner.sh accordingly by editing the line
      ```
      source "/opt/IBM/ace-13.0.2.0/server/bin/mqsiprofile"
      ```

  3. run deployment_planner.sh as 
   
      -    To Analyze BAR files
      ```
      #deployment_planner.sh  <Path to Folder containing BARs>  <temp workspace dir>  <temp output dir>
      ```

      - To Analyze Broker backup
      
      ```
      #deployment_planner.sh  <Absolute path to backup zip>  <temp workspace dir>  <temp output dir>
      ```


   4. At the end of the script's execution, it will launch a npm server for you to view the analysis report on browser UI. Open your browser and load the results by entering the host and portnumber (3000) http://localhost:3000 (replace localhost with the actual IP of your Linux machine where the script was run).






