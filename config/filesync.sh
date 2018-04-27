#!/bin/sh
if [ $# -ne 3 ]; then
        echo "paramenter must 3  $2  $1:$3"
        exit 0
fi
if [ ! -n "$3" ];then
        echo "you have not input a path!"
        exit 0
fi
rm -rf $3/*
cp $2/*  $3/

