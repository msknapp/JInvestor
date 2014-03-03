#!/bin/bash

hbase shell -e "disable 'homes'"
hbase shell -e "disable 'homes_index_address'"
hbase shell -e "drop 'homes'"
hbase shell -e "drop 'homes_index_address'"
hbase shell -e "create 'homes', {BLOOMFILTER => 'ROW', COMPRESSION => 'SNAPPY', VERSIONS => '1'}"
hbase shell -e "create 'homes_index_address', {BLOOMFILTER => 'ROW', COMPRESSION => 'SNAPPY', VERSIONS => '1', BLOCKSIZE => '30000'}"