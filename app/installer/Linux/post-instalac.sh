#!/bin/bash
sed "s/usuario/$USER/g" pawlink.desktop > pawlink.desktop.bak
mv pawlink.desktop.bak pawlink.desktop
chmod 777 pawlink.desktop
echo Copiando .desktop a /usr/share/applications
sudo cp pawlink.desktop /usr/share/applications
echo pawlink.desktop copiado