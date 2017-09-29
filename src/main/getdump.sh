REMOTE="192.168.42.82"

echo "Transfering script to pi and running it"
scp makedump.sh pi@$REMOTE:~/
ssh pi@$REMOTE "~/makedump.sh"
echo "Transfering dumps from pi"
scp pi@$REMOTE:~/can0.dump .
scp pi@$REMOTE:~/can1.dump .

