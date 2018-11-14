# Horizontal-Systems Block-Explorer

## Requirements
- [Maven](http://maven.apache.org/) is used as project management tool
- JDK 8 or higher to build projects; JRE should be enough on target environments to run project artifacts


## Modules

- **bex-blocknode** 

  Manages (fullnode) nodes, setup to use with BlockExplorer. Connects to specified node (included in a config file) and manages node network connections, data synchronization and node status. Shortly, this is a "Management and Monitoring" module for varios BlockChain nodes (BTC,ETH,BCH ...).
        
- **bex-blockchain**
  Blockchain data manager. Works with **blocknode** module to parse and manage blockchain data (Blocks/Transactions info, Fee-Estimation etc).
 
- **bex-currency** 
  Currency management module. All functionality related to Currencies like exchange-rates and currency detailed info.
  Utilizes **datastore** module to store Currency information to IPFS. 
  
- **bex-datastore**
  Data storage module. Used by other modules to manage (store/retrieve) data on a various storage options.
  For now module support mainly IPFS and FileSystem. Support for  IPLD and various databases will be added soon.
  
- **bex-common** 
  Common libraries, utilities, tools used by other modules.
   
- **bex-web**
  Web Interface for Block-Explorer
 with




