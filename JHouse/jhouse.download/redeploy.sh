#!/bin/bash

THISDIR=$PWD

cd $HOME

rm -rf jinvestor*

cd $THISDIR

mvn clean install

cp target/jinvestor*.tar.gz $HOME

cd $HOME

tar zxvf jinvestor*.tar.gz

cd jinvestor
