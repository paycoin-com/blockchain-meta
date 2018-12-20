# Block Explorer by Horizontal Systems

## Requirements
- [Maven](http://maven.apache.org/) is used as project management tool
- JDK 8 or higher to build projects; JRE should be enough on target environments to run project artifacts


## Modules

- **bex-blocknode** 

  Manages (fullnode) nodes, setup to use with BlockExplorer. Connects to specified node (included in a config file) and manages node network connections, data synchronization and node status. Shortly, this is a "Management and Monitoring" module for varios BlockChain nodes (BTC,ETH,BCH ...).
        
- **bex-blockchain**
  Blockchain data manager. Works with **blocknode** module to parse and manage blockchain data (Blocks/Transactions info, [Fee-Estimation](https://ipfs.horizontalsystems.xyz/ipns/Qmd4Gv2YVPqs6dmSy1XEq7pQRSgLihqYKL2JjK7DMUFPVz/io-hs/data/docs/block-explorer/bex-blockchain-fee.html) etc).
 
## Fiat Currency Module

Allows to store and interact with essential data relevant to fiat currencies like [exchange-rates and other info](https://ipfs.horizontalsystems.xyz/ipns/Qmd4Gv2YVPqs6dmSy1XEq7pQRSgLihqYKL2JjK7DMUFPVz/io-hs/data/docs/block-explorer/bex-currency.html). Such information is often critical for any type of crypto currency applications.

Storing Data:

Currency data obtained from public data providers and stored on decentralized storage medium like IPFS from where it can be obtained on demand, from anywhere.

- Real-time crypto to USD exchange rates (obtained via https://www.cryptocompare.com)
- Real-time crypto to EUR, RUB, AUD, CAD, CHF, CNY, GBP, JPY exchange rates are calculated by converting USD rate to the other currency (via https://exchangeratesapi.io)

This module uses below mentioned **datastore** module to store exchange rate information above to IPFS node, which is also available for public use. 

It essentially enables anyone to get latest crypto to fiat exchange rates (with up-to 3 minute ineterval) as well as lookup historical exchange rates.

Reading Data:

Exchange rate vailability periods:

- Average Daily Exchange Rate for the period Jan 1st, 2015 - Nov 23rd, 2018
- Up to the minute rates for the period after Nov 23rd, 2018

Refer to the [documentation](https://ipfs.horizontalsystems.xyz/ipns/Qmd4Gv2YVPqs6dmSy1XEq7pQRSgLihqYKL2JjK7DMUFPVz/io-hs/data/docs/block-explorer/bex-currency.html) for instructions about reading the stored currency data from IPFS.


  
- **bex-datastore**
  Data storage module. Used by other modules to manage (store/retrieve) data on a various storage options.
  For now module support mainly IPFS and FileSystem. Support for  IPLD and various databases will be added soon.
  
- **bex-common** 
  Common libraries, utilities, tools used by other modules.
   
- **bex-web**
  Web Interface (Control Panel) for Block-Explorer


## Full Documentation

https://ipfs.horizontalsystems.xyz/ipns/Qmd4Gv2YVPqs6dmSy1XEq7pQRSgLihqYKL2JjK7DMUFPVz/io-hs/data/docs/block-explorer/




