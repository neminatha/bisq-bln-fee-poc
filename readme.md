# Bitcoin Lightning Network Fee PoC

A Proof of Concept for implementing a 1% marketplace fee payment via the Bitcoin Lightning Network using Java.

## Overview

This project demonstrates how to:

1. Create and handle Lightning Network invoices using Java
2. Calculate and transfer a 1% marketplace fee via Lightning Network
3. Implement privacy-preserving payments

The project uses:
- Java 24
- (bitcoinj for Bitcoin operations)
- lightningj for Lightning Network integration
- JavaFX for the desktop UI
- Maven for dependency management

## Prerequisites

### Install LND (Lightning Network Daemon)

#### Option 1: Using Homebrew (macOS)

```bash
brew install lnd
```

#### Option 2: Download Binary

Download the latest release for your platform from [LND Releases](https://github.com/lightningnetwork/lnd/releases)

### Configure LND

There are two ways to run LND:

#### 1. Neutrino Mode (lightweight, no full node required)

Create `~/.lnd/lnd.conf` with:

```
[Application Options]
alias=bisq-bln-test-neutrino-node
color=#68f442
listen=127.0.0.1:9735
rpclisten=127.0.0.1:10009
restlisten=127.0.0.1:8080
debuglevel=info
maxpendingchannels=3
accept-keysend=true
allow-circular-route=1

[Bitcoin]
bitcoin.testnet=true
bitcoin.node=neutrino

[Neutrino]
neutrino.connect=btcd-testnet.lightning.computer
# neutrino.connect= ...

[protocol]
protocol.wumbo-channels=true

[Wtclient]
wtclient.active=true

```

#### 2. Bitcoin Core Mode (requires running Bitcoin Core)

If you have Bitcoin Core running, you can use:

```
[Application Options]
alias=bisq-bln-test-node
color=#68f442
listen=127.0.0.1:9735
rpclisten=127.0.0.1:10009
restlisten=127.0.0.1:8080
debuglevel=info
maxpendingchannels=3
accept-keysend=true
allow-circular-route=1

[Bitcoin]
bitcoin.testnet=true
bitcoin.node=bitcoind

[bitcoind]
bitcoind.rpcuser=bitcoinrpc
bitcoind.rpcpass=testnet-password
bitcoind.rpchost=127.0.0.1
bitcoind.rpcport=18332
bitcoind.zmqpubrawblock=tcp://127.0.0.1:28332
bitcoind.zmqpubrawtx=tcp://127.0.0.1:28333

[protocol]
protocol.wumbo-channels=true

[Wtclient]
wtclient.active=true
```

## Start LND

### Using the provided scripts

This repo includes helper scripts:

1. `testnet-lnd.sh` - Starts LND with Bitcoin Core configuration
2. `testnet-neutrino-lnd.sh` - Starts LND in lightweight Neutrino mode
3. `testnet-bitcoind.sh` - Starts Bitcoin Core in testnet mode (if needed)

Make the scripts executable:

```bash
chmod +x *.sh
```

Start LND (choose one):

```bash
# For Neutrino mode (recommended for testing)
./testnet-neutrino-lnd.sh

# OR for Bitcoin Core mode
./testnet-bitcoind.sh
./testnet-lnd.sh
```

### First-time setup

If this is your first time running LND, you'll need to create a wallet:

```bash
lncli create
```

Follow the prompts to create a new wallet. Make sure to securely store your seed phrase!

### Getting Testnet Bitcoin

To test Lightning payments, you'll need some testnet Bitcoin:

1. Generate a new Bitcoin address:
```bash
lncli --network=testnet newaddress p2tr
```

2. Request testnet coins from a faucet:
   - [Bitcoin Testnet Faucet](https://coinfaucet.eu/en/btc-testnet/)

3. Open a Lightning channel:
```bash
# Connect to a node
lncli connect 0269a94e8b32c005e4336bfb743c08a6e9beb13d940d57c479d95c8e687ccbdb9f@97.113.235.238:9735

# Open a channel with 50000 satoshis
lncli openchannel 0269a94e8b32c005e4336bfb743c08a6e9beb13d940d57c479d95c8e687ccbdb9f 50000
```

## Build and Run the PoC

1. Clone this repository:
```bash
git clone https://github.com/neminatha/bisq-bln-fee-poc.git
cd bisq-bln-fee-poc
```

2. Build with Maven:
```bash
mvn clean package
```

3. Run the application:
```bash
java -jar target/bisq-bln-fee-poc-1.0-SNAPSHOT.jar
```

4. Or Run with Maven:
```bash
mvn javafx:run
```

## Features

- Create trades with configurable amounts
- Automatically calculate 1% marketplace fees
- Generate Lightning Network invoices for fee payment
- Process Lightning Network payments
- Monitor node status and balance

## Project Structure

```
bisq-bln-fee-poc/
├── pom.xml                                 # Maven configuration file
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── network/bisq/blnfeepoc/
│   │   │       ├── controller/          
│   │   │       │   └── MainController.java  # UI controller
│   │   │       ├── MainApp.java             # Main application class
│   │   │       ├── model/
│   │   │       │   └── Trade.java           # Trade data model
│   │   │       └── service/
│   │   │           └── LightningNetworkService.java  # LN integration
│   │   └── resources/
│   │       └── network/bisq/blnfeepoc/
│   │           └── MainView.fxml           # JavaFX UI definition
├── testnet-bitcoind.sh                     # Script to start bitcoind
├── testnet-lnd.sh                          # Script to start LND with bitcoind
└── testnet-neutrino-lnd.sh                 # Script to start LND in Neutrino mode
```

## Next Steps

This PoC can be extended to:

1. Implement more robust error handling
2. Add privacy enhancements using LN features
3. Integrate with a complete P2P trading system
4. Add support for multi-path payments
5. Implement secure key management

## Contributing

Contributions are welcome! This project is part of Bisq's exploration of privacy-preserving fee mechanisms.

## License

Bitcoin Lightning Network Fee PoC is licensed under the AGPL-3.0 license. All contributions are subject to this license.
