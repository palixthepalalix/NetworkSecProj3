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

java NgramStats 1 1 outfiles/stats_ examples/ ${EXAMPLES[@]}             


java NgramStats 3 1 outfiles/stats_ examples/ ${EXAMPLES[@]}             

java NgramStats 3 3 outfiles/stats_ examples/ ${EXAMPLES[@]}             

c=0
while [ $c -lt $tLen ]
do
    strings -n 5 examples/${EXAMPLES[$c]} > strings_outputs/strings_prog$c
    sort strings_outputs/strings_prog$c > strings_outputs/sorted_prog$c
    echo strings ${EXAMPLES[$c]}
    c=$((c+1))
done



