## Description
The Prize Collecting Traveling Sales Representative (PCTSR) is an NP-hard problem in theoretical computer science. It is a variant of the classic Traveling Salesman Problem (TSP), often refered to as Quota TSP. Given a list of marketplaces, travel costs (e.g. distances between them) and a non-negative prize to collect at each market, the goal is to collect a given quota while minimizing the length of the tour.

## Implementation
The implementation is made using Java 8 and JavaFX.

The algorithm runs on the list of the most profitable companies in Denmark for 2019, based on the data from http://www.largestcompanies.com/toplists/denmark/largest-companies-by-earnings/.

The companies location was gathered using Pyhon 3 and GeoPy (https://geopy.readthedocs.io/en/stable/), and the distances between the companies was calculated based on the real road network thanks to https://github.com/Project-OSRM/.

## The GUI
The simple grapgic user interface allows the user to enter starting vertex (the company they wish to start from), desired profit (the profit to be collected), number of agents (each agent will have an unique route) and choose between different heuristic solutions.

![GUI](./GUI_RAN.png)

## How to run
In order to run the program, you need to have Java 8 configured on your machine (higher versions should also work but were not tested). You also need to make sure your JDK version supports JavaFX as some of them have it removed. More information about JavaFX and how to run it can be found on https://openjfx.io/.

**Navigate to the project source folder**
 - cd ./project/src/

**Compile all the classes**
 - javac *.java

**Choose what to run**
 - java Main -> *run the GUI*
 - java Main -i -> *information about available commands*
 - java -ea Main -t -> *run tests (remember -ea to enable assertions)*
 - java Main -rp -> *compute a tour and output it on the console (without the GUI)*
 - java Main -e -> *run all the experiments (results are saved to .csv files)*

**To run a specific experiment, add a number between 1 and 6**
 - java Main -e 1 -> *run experiment for HeuristicOne coefficient*
 - java Main -e 2 -> *kmax parameter with random ratio for vertex removal for HeuristicTwo*
 - java Main -e 3 -> *kmax parameter with fixed ratio for vertex removal for HeuristcTwo*
 - java Main -e 4 -> *remove operation ratio for HeuristicTwo*
 - java Main -e 5 -> *route modification percent for HeuristicTwo*
 - java Main -e 6 -> *comparisson between all heuristic methods*

## Authors
Adrianna Wiacek (https://github.com/losica) & Kalin Dobrev (https://github.com/dobrevkalm)
