#!/bin/bash

# a finir :token + landement au démarrage de l'instance
openstack token issue

openstack server create -flavor m1.small --image CentOS7 --security-group Groupe1 --key-name ClePublique1 PovRay


tar xzvf ./zzpovray.tar.gz          #dezipage du fichier
cd ./povray                         #deplacement dans le dossier

#creation des images selon le job en cours
./povray +A +W10 +H10 +Lshare/povray-3.6/include/ +SF1 +EF20 glsbng.ini

openstack container create MonConteneur
swift upload MonConteneur ./povray/glsbng*.png

openstack server stop PovRay


#!/bin/bash

#idem

# a finir :token + landement au démarrage de l'instance
openstack token issue

openstack server create -flavor m1.small --image CentOS7 --security-group Groupe1 --key-name ClePublique1 InstancePostTraitement

sudo-i
yum install -y ImageMagick
exit

swift download MonConteneur ./povray/glsbng*.png

convert glsbng*.png -delay 6 -quality 100 glsbng.gif 

swift upload MonConteneur ./povray/glsbng.gif

openstack server stop InstancePostTraitement
