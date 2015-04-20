#!/bin/bash

javac Ngram.java

declare -a EXAMPLES=('prog1' 'prog2' 'prog3' 'prog4' 'prog5' 'prog6.java')

echo ${EXAMPLES[@]}


n=1
s=1
count=0
tLen=${#EXAMPLES[@]} 
while [ $count -lt 6 ]
do
    while [ $n -le 3 ]
    do
        while [ $s -le $n ]
        do
            echo java Ngram $n $s examples/${EXAMPLES[$count]} outfiles/out${EXAMPLES[$count]}_${n}_${s}
            java Ngram $n $s examples/${EXAMPLES[$count]} outfiles/out${EXAMPLES[$count]}_${n}_${s}
            s=$((s+1))
        done
        s=1
        n=$((n+1))
    done
    count=$((count+1))
    n=1
    s=1
done

