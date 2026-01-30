# LSM-Tree Key-Value Storage Engine

A high-performance, persistent Key-Value store implemented in Java. This project demonstrates a deep understanding of **distributed systems**, **concurrency**, and **storage engine architecture** by implementing a Log-Structured Merge-Tree (LSM-Tree).

##  Key Engineering Features

* **LSM-Tree Architecture**: Optimized for high write throughput by transforming random I/O into sequential writes using a multi-tiered storage strategy (MemTable & SSTables).
* **Write-Ahead Log (WAL)**: Ensures **100% Data Durability** and atomicity. The system recovers its state after a crash by replaying the operation log.
* **Multi-Threaded Server**: Built with a **Fixed Thread Pool** and **TCP Sockets**, allowing simultaneous multi-user access.
* **Thread Safety**: Utilizes `ReentrantReadWriteLock` to manage concurrent access, ensuring data integrity.
* **Soft Deletion (Tombstones)**: Implements industry-standard "Tombstone" markers for $O(1)$ deletions, avoiding disk-seek overhead.



##  Project Structure

```text
MySimpleDB/
├── src/
│   ├── MemTable.java         
│   ├── WAL.java              
│   ├── SSTable.java           
│   ├── DatabaseServer.java    
│   └── DatabaseClient.java   
├── .gitignore                 
└── README.md                  


