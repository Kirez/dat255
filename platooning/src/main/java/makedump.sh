export PATH=$PATH:$HOME/can-utils

cd $HOME

candump can0 > can0.dump &
candump can1 > can1.dump &

echo "Collecting 30s of data"

sleep 30

kill %
kill %

echo "Done"

