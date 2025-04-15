#!/bin/bash
BITCOIN_HOME="/Volumes/X1_Mac/Blockchains/Bitcoin"

if [ -d "$BITCOIN_HOME" ]; then
  echo "Using Bitcoin data directory: $BITCOIN_HOME"
  bitcoind -conf="$BITCOIN_HOME/testnet-bitcoin.conf" -datadir="$BITCOIN_HOME"
else
  echo "Data directory $BITCOIN_HOME not found. Using default Bitcoin data directory."
  bitcoind -conf="$BITCOIN_HOME/testnet-bitcoin.conf"
fi