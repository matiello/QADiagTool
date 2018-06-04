# QADiagTool
[![AGPL v3](https://img.shields.io/badge/license-GPL%20v3-brightgreen.svg)](./LICENSE)
[![build](https://api.travis-ci.com/matiello/QADiagTool.svg?branch=master)]

## What is the QA Diag Tool?

It is a command Line tool for load & peformance tests for server to server production validation.

It currently supports performance tests for the following platforms:
* Oracle SQL Test
* HTTP Test
* JMS activeMQ Test
* JMS hornetQ Test

## Examples

### Oracel SQL Databse

#### How to do a 200 bytes query performance test on Oracle Database with 5 threads and 1000 executions each?
```QADiag -db jdbc:oracle:thin:@prodHost:1521:ORCL SCOTT TIGER -bytes 200 -strategy count 1000 -threads 5 ```

#### How to do a specific query performance test on Oracle Database with 30 threads and 1000 executions each?
```QADiag -db jdbc:oracle:thin:@prodHost:1521:ORCL SCOTT TIGER -querySQL "SELECT 1 FROM DUAL" -strategy count 1000 -threads 30```

### HTTP

#### How to do a specific HTTP GET performance test on with 10 threads and 10000 executions each?
```QADiag -http "http://www.website.com" GET -strategy count 10000 -threads 10```
    
## Usage
```
Usage: QADiag <Selection Option> [Optional Parameters]
  Support Selection Options:
  ------- --------- -------
    -h                      : Displays help
    -listPlugins            : Displays the diagnostic plugins loaded
    -about                  : Displays application information and copyright
  Scenario & History Selection Options:
  -------- - ------- --------- -------
    -listScenarios          : List the stored scenarios
    -load <id>              : Loads a previously saved scenario
    -showHistory <id>       : Shows the scenario execution history
    -clearHistory <id>      : Clears the scenario execution history
    -expHistory <id> <file> : Export the execution history to excel file
  Plugin Diagnostic Selection Options:
  ------ ---------- --------- -------
    -http <url> <method> [Optional Parameters] : Execute HTTP load diagnostic
      <url>                          : The http url.
      <method>                       : The http connection method (GET|POST).
      Optional Parameters:
        -testResult <testResult>     : Test HTTP Body result. (Default )
        -connTimeout <connTimeout>   : The connection timeout in seconds. (Default 5000)
        -soTimeout <soTimeout>       : The SO read timeout in seconds. (Default 5000)
        -params <params>             : Add HTTP request parameters (On GET). (Default )
        -bodyParams <bodyParams>     : The file that contains the HTTP Body Request. (Default )
        -contentType <contentType>   : The request content type. (Default text/xml;charset=iso-8859-1)
        -showRequest                 : Shows the HTTP Request. (Default false)
        -showResponse                : Shows the HTTP Response. (Default false)
    -jmsActiveMQ <action> <url> [Optional Parameters] : Execute ActiveMQ JMS load diagnostic
      <action>                       : The jms action [CONSUMER|PRODUCER].
      <url>                          : The jms url to connect.
      Optional Parameters:
        -queueName <queueName>       : The JMS queue name. (Default default_queue)
        -bytes <bytes>               : The JMS message size in bytes. (Default 1)
        -dispatchAsync               : The indication to dispatch messages asynchronously. (Default false)
    -jmsHornetQ <action> <hostname> <port> [Optional Parameters] : Execute HornetQ JMS load diagnostic
      <action>                       : The jms action [CONSUMER|PRODUCER].
      <hostname>                     : The jms hostname to connect.
      <port>                         : The jms port to connect.
      Optional Parameters:
        -queueName <queueName>       : The JMS queue name. (Default default_queue)
        -bytes <bytes>               : The JMS message size in bytes. (Default 1)
    -selftest <sleep> [Optional Parameters] : Execute internal timers validations
      <sleep>                        : Process sleep time in ms
      Optional Parameters:
    -db <url> <username> <password> [Optional Parameters] : Execute Oracle SQL load diagnostic.
      <url>                          : The jdbc url connection.
      <username>                     : The jdbc url username.
      <password>                     : The jdbc url password.
      Optional Parameters:
        -connTimeout <connTimeout>   : The connection establishment timeout in seconds. (Default 10)
        -queryTimeout <queryTimeout> : The query execution timeout in seconds. (Default 1)
        -bytes <bytes>               : The number of bytes per sql interaction. (Default 1)
        -queryKey <queryKey>         : The query identifier Id. (Default 1)
        -querySQL <querySQL>         : The query SQL to be used. (Default SELECT /* ${processName}.${queryKey} */ $Repeat(
${queryKey},${bytes}) FROM DUAL)
    Optional Parameters:
      -strategy <type> <nbr>: The strategy to use <endless|count <interations>|duration <sec>) (Default count 1)
      -threads <nbr>        : The execution of threads (Default 1)
      -execInterval <nbr>   : The execution interval in miliseconds (Default 0)
      -hideParams           : Hides the software used paramters (Default false)
      -saveScenario <id>    : Save the scenario parameters for reuse (Default false)
      -showErrors           : Show any errors that may occour (Default false)
      -showThreadDetail     : Shows the thread parcial results (Default false)
      -showSumary           : Shows the result sumary (Default true)
```

## Cautions

Please take note that this client do use machine resources (such as CPU). In order to obtaion better results, it is better to run in on the "client" server in order to accuratly measure results.

## How to extend it?

This is a plugin based tool. If you want to create new plugins for it, please refer to the qa-diag-plg-selftest project template.

## How to donate?

```
If you want to contribute to the autor, donations are accepted with the following wallets: 
- BITCoin: 123JkdSUS1xw8kKr9nyFowAaMW33Snu9nc
- IOTA: ZNAHGDJBWUQRLCWNPFYDVHXFIBOTFGUJZLJMV9CYXCSPLVUXHRMZUIGGETCGFVEMLGAACERYXMASGKU9DOFEIGZSDW
- Monero: 47G5iPzCxU21azFuvEuTda4CB3Uf8oq6jHvcx6S6vprAcpKpnBempdXaERCZ6DcP1vTcvYQLxHopKBgKK3C8ieuESbtVL9B
```
