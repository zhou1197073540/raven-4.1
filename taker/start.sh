#/bin/bash

rm ./nohup.out
nohup java -jar `ls target/*.jar` --spring.profiles.active=test &
