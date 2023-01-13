# S-Calvin
## Overview
A simple version of calvin (determinstic distributed databse) implmentation based on vanillaDB project. All of our implementations are in the folder `calvin`.
> This is a final project repository for NTHU/Database course
- VanillaDB: https://github.com/vanilladb/vanillacore

## Architecture
![final-project-architecture.png](final-project-architecture.png)

Here is the workflow of executing a stored procedure:

1. The clinet (VanillaBench) sends a stored procedure request.
2. The group communication module receives the request and then passes the request to VanillaComm for total-ordering.
3. The group communication module receives total-ordered requests from VanillaComm, and then it passes these requests to the scheduler.
4. The scheduler analyzes each request, logs each request, generates an execution plan, registers locks for conservative locking, and then creates a stored procedure instance and dispatches the instance to a thread for execution.
    - During the analysis, it may have to query the metadata module for the partitions (the locations) of the requested records.
5. During the execution of a stored procedure, it retrieves records from the cache module, which further retrieves records from the storage engine of VanillaCore.
6. If a stored procedure needs to deliver a record to a remote node, it will send the record using P2p messaging API provided by VanillaComm. VanillaComm may also pass the received records to the cache module.
7. After the stored procedure finishes the execution, it commits the transaction and then sends the result back to the clinet (VanillaBench).

## Details

- Communication
  - A client-side module to send requests to servers.
  - A server-side module to send total-ordering requests and receive total-ordered messages and P2p messages.
- Scheduler
  - To analyze requests, generate execution plan and dispatch requests to threads.
- Metadata
  - To store data partition information so that other moduels can query data partitions.
- Recovery
  - To log stored procedure requests.
- Concurrency
  - Conservative Locking
- Stored Procedures
  - An abstract class for VanillaBench to implement detailed transaction logics.
  - Able to send records to remote nodes.
  - Able to send the result back to clients (VanillaBench).
- Cache
  - To cache remote records.
  - To retrieve/modify/insert/delete records from/in VanillaCore.
