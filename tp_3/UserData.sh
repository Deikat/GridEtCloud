#!/bin/bash

openstack token issue

openstack server create -flavor m1.small --image CentOS7 --security-group Groupe1 --key-name ClePublique1 client
