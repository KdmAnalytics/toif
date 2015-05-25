#! /bin/bash

# ./findrep.sh <old FindbugsConfig> <new RawData File>

OLD=$1
NEWRAW=$2
NEW="fb.new"

if [ -f fb2 ]; then
  rm fb2
  echo "Deleted fb2"
fi 

if [ -f fb.new ]; then
  rm fb.new
  echo "Deleted fb2.new"
fi

if [ -f oldNotNew.csv ]; then
  rm oldNotNew.csv
  echo "Deleted oldNotNew.csv"
fi

if [ -f fb.new.csv ]; then
  rm fb.new.csv
  echo "Deleted fb.new.csv"
fi

if [ -f old ]; then
  rm old
  echo "Deleted old"
fi

if [ -f new ]; then
  rm new
  echo "Deleted new"
fi



while read line
do
  grep -v "TODO" | awk '$0 ~ /^[A-Za-z]*:/ {print "#"$0"\n"substr($NF, 2, length($NF)-2)"=\n"substr($NF, 2, length($NF)-2)"Msg=\n" }' >> fb2
done < $NEWRAW

while read line
do
  #if is not the description
  if [[ "$line" =~ ^[A-Z] ]]; then
    
    #if we can find the matching string use the new one
    if $(grep -q $line $OLD); then
      grep -m1 $line $OLD >> $NEW
    #if we cant find the matching string just print the line
    else 
      echo $line >> $NEW
    fi

  # if it is the description 
  else
    echo $line >> $NEW
  fi
done < fb2

grep -e "[A-Z]=" $NEW | awk -F'=' '{print $1}' > new

grep -e "[A-Z]=" $OLD | awk -F'=' '{print $1}' > old

grep -v -i -f new old > oldNotNew

grep -v ":" $NEW | grep "=" | grep -v "Msg=" | awk -F';' '{print $1"\t"$2"\t"$3}' | tr -d "=" > fc.new.csv

grep -f oldNotNew $OLD | grep -v ":" | grep "=" | grep -v "Msg=" | awk -F';' '{print $1"\t"$2"\t"$3}' | tr -d "=" > oldNotNew.csv
