TESTER=../../tester/Tester.jar

for i in `seq 1 10`; do 
    echo "case:$i"
    java -jar $TESTER -exec "python main.py" -seed $i -vis -save -num
done
