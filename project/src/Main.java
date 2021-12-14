import GUI.GUI;
import experiments.*;
import test.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("\nStarting the GUI.\nRun with -i for other available commands.\n");
            runGUI();
        } else {
            switch (args[0]) {
                case "-i":
                    printInfo();
                    break;
                case "-t":
                    runGeneralTests();
                    break;
                case "-rp":
                    runResultPathTest();
                    break;
                case "-e": {
                    if (args.length == 1) {
                        runAllExperiments();
                    } else {
                        runExperiment(Integer.parseInt(args[1]));
                    }
                    break;
                }
                default:
                    wrongArgument();
            }
        }
    }

    // runs the interface
    private static void runGUI() {
        GUI gui = new GUI();
        gui.run();
    }

    private static void runAllExperiments() {
        Experiment[] experiments = new Experiment[] {
                new HeuristicOneExperiment("heuristic_one.csv"),
                new H2kmaxExperiment("heuristic_two_kmax_random.csv", true),
                new H2kmaxExperiment("heuristic_two_kmax_fixedRatio.csv", false),
                new H2ratioExperiment("heuristic_two_ratio.csv"),
                new H2percentExperiment("heuristic_two_percent.csv"),
                new HeuristicComparison("heuristic_comparison.csv")
        };

        for (Experiment e : experiments) {
            runSingleExperiment(e);
        }
    }

    private static void runExperiment(int experiment) {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss").format(new Date());
        String fileName;
        Experiment e = null;

        switch (experiment) {
            case 1: {
                fileName = "h_one_" + timeStamp + ".csv";
                e = new HeuristicOneExperiment(fileName);
                break;
            }
            case 2: {
                fileName = "h_two_kmax_rand_" + timeStamp + ".csv";
                e = new H2kmaxExperiment(fileName, true);
                break;
            }
            case 3: {
                fileName = "h_two_kmax_fix_" + timeStamp + ".csv";
                e = new H2kmaxExperiment(fileName, false);
                break;
            }
            case 4: {
                fileName = "h_two_ratio_" + timeStamp + ".csv";
                e = new H2ratioExperiment(fileName);
                break;
            }
            case 5: {
                fileName = "h_two_percent_" + timeStamp + ".csv";
                e = new H2percentExperiment(fileName);
                break;
            }
            case 6: {
                fileName = "h_comparison_" + timeStamp + ".csv";
                e = new HeuristicComparison(fileName);
                break;
            }
            default:
                wrongArgument();
        }

        runSingleExperiment(e);
    }

    private static void runSingleExperiment(Experiment e) {
        System.out.printf("%nRUNNING %s...", e.getExperimentName());
        e.run();
        System.out.printf("DONE!%nResults saved in %s%n%n", e.getResultFileName());
        timeout(2);
    }

    private static void runResultPathTest() {
        Scanner sc = new Scanner(System.in);

        // select heuristic method
        System.out.println(" - Choose heuristic method.\n - Enter one, two, three or four:");
        String heuristic = sc.nextLine();

        // select the starting vertex
        System.out.println(" - Choose starting vertex.\n - Enter a number between 0 and 90:");
        int startVertex = Integer.parseInt(sc.nextLine());

        // select the number of agents
        System.out.println(" - Choose the number of sales representatives.\n - Enter a number between 1 and 10:");
        int agentsNumber = Integer.parseInt(sc.nextLine());

        // select desired profit
        System.out.println(" - Choose desired minimum collected profit.\n - Enter value between 5 and 299:");
        int minProfit = Integer.parseInt(sc.nextLine());

        ResultPathTest rpt = new ResultPathTest();
        rpt.getResultPath(heuristic, startVertex, agentsNumber, minProfit);
    }

    // general tests for the two solutions and the data reader
    private static void runGeneralTests() {
        Scanner sc = new Scanner(System.in);
        System.out.println(" - In order to get correct test result, you need to run the program with assertions enabled.\nWas it ran correctly? [y/N]");
        String input = sc.nextLine();
        if (input.toLowerCase().contains("y")) {
            System.out.println("\nStarting data tests.\nWill read through the input data and validate it.\n");
            timeout(2);
            System.out.print(" - RUNNING TESTS...");
            ReaderTest rt = new ReaderTest();
            rt.runTests();
            System.out.print("DONE!\n\n\n");
            timeout(1);
            System.out.println("Starting heuristic solutions test. This takes couple of minutes.\nRunning each solution from each starting vertex.\nEach run will test different number of agents and different profits.\n");
            timeout(2);
            System.out.print(" - RUNNING TESTS...");
            HeuristicTest ht = new HeuristicTest();
            ht.runTests();
            System.out.print("DONE!\n\n\n");
            timeout(1);
            System.out.println(" + ALL TESTS PASSES + \n");
        } else {
            System.out.println("## Please re-run using '-ea' tag, e.g. java -ea Main -t");
        }
    }

    // print information about the running options
    private static void printInfo() {
        System.out.println("\nAvailable commands:");
        System.out.println(" - java Main -> run the GUI");
        System.out.println(" - java -ea Main -t -> run tests");
        System.out.println(" - java Main -rp -> run the program without hte GUI");
        System.out.println(" - java Main -e -> run all the experiments");
        System.out.println(" - java Main -e {number 1-6} -> run a single experiment");
        System.out.println("\nFor more info, visit https://github.com/dobrevkalm/PCTSR\n");
    }

    private static void wrongArgument() {
        System.out.println("## Wrong argument entered ##");
        System.out.println("## Available arguments: [-i, -t, -rp, -e, -e {number}] ##");
        System.out.println("## Run with -i or visit https://github.com/dobrevkalm/PCTSR for more info ##\n");
        System.exit(0);
    }

    // just a simple timeout, wrapped in a method to handle the exception
    private static void timeout(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
