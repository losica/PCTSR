package heuristics;

import model.Place;

import java.util.List;
import java.util.Random;

/**
    This class extends the logic and the computations from HeuristicTwo.

    The difference is in the way we add vertices when performing the modifications on the routes.
    Instead of adding to the same agent when performing a mutation, after removing a vertex,
    we consider all the sales representatives for the following addition, instead of adding back to agent we removed from.
 */

public class HeuristicFour extends HeuristicTwo {

    public HeuristicFour(double[][] distanceMatrix, Place[] places, int startVertex, int agentsNumber, int minProfit) {
        super(distanceMatrix, places, startVertex, agentsNumber, minProfit);
    }

    @Override
    void performMutations() {
        // ensure that the first route created will be overwritten with one of the routes created by mutations
        double previousMinLength = Double.MAX_VALUE;

        Random random = new Random();
        // the number of mutations
        int kmax = 25;
        if (agentsNumber > 3) {
            kmax = 35;
        }

        for (int k = 0; k < kmax; k++) {
            // randomly modify 0 to 80% of the route (determined by experiments)
            int modificationPercent = random.nextInt(80);
            previousMinLength = generateMutations(modificationPercent, previousMinLength);
        }
    }

    // perform the mutations for all the agents (sales representatives)
    @Override
    double generateMutations(int percent, double previousMinLength) {
        // for every agent's pathResult remove given "percent" of vertices
        for (int agent = 0; agent < agentsNumber; agent++) {
            generateAgentRouteMutation(agent, percent);
        }

        // calculate total collected profit after changes
        sumProfit = 0.0;
        for (int a = 0; a < agentsNumber; a++) {
            sumProfit += pathResult[a].getActualProfit();
        }

        // repeat insert operation until condition of the minimum required profit is satisfied
        // gather profit method adds vertices
        gatherProfit();

        // calculate the total distance
        double sumLength = 0.0;
        for (int i = 0; i < agentsNumber; i++) {
            sumLength += pathResult[i].getPathLength();
        }

        if (sumLength < previousMinLength) {
            previousMinLength = sumLength;
            // save the previous mutations of the routes
            savePreviousRoute();
        } else {
            updateAgentsPathResult(true);
        }
        return previousMinLength;
    }

    private void gatherProfit() {
        // repeat insert operation until we collect minimum desired profit
        while (sumProfit < minProfit) {
            sumProfit += insert();
        }
    }

    private double insert() {
        double bestH = -1.0;
        // index of the vertex to be inserted to the resultPath
        int besti = -1;
        // place to be inserted to the resultPath
        int bestj = -1;
        double plusLength = 0.0;
        int agent = -1;

        // find the agent and vertex with the best removeOperationRatio: profit / increase of travelled distance
        for (int agentNo = 0; agentNo < agentsNumber; agentNo++) {
            int n = pathResult[agentNo].getResultPath().size();

            // iterate through all the vertices
            for (int i = 0; i < visited.length; i++) {
                // consider only not visited vertices
                if (!visited[i]) {
                    // find the best place to insert a new vertex
                    for (int j = 1; j < n; j++) {

                        List<Place> resPath = pathResult[agentNo].getResultPath();
                        // minus - it represents the distance between vertices j-1 and j, which basically is the edge
                        //         we need to break to insert a new new vertex between vertex j-1 and vertex j
                        double minus = distanceMatrix[resPath.get(j - 1).getId()][resPath.get(j).getId()];
                        // plus - it represents the distance after adding a new vertex; as we add a new vertex,
                        //        two new edges will be added, one connecting to new vertex with vertex j-1 and the other
                        //        connecting that new vertex with j
                        double plus = distanceMatrix[resPath.get(j - 1).getId()][i] + distanceMatrix[i][resPath.get(j).getId()];
                        double plusProfit = places[i].getFirmProfit();

                        // plusProfit - profit of the new vertex (company's net profit)
                        // (plus - minus) - the increase in distance caused by adding a new vertex
                        // we are looking for a combination of an agent, vertex and index in the pathResult that gives
                        // us the biggest value of (company's net profit / increase in the travelled distance) ratio
                        if (plusProfit / (plus - minus) > bestH) {
                            // save best (company's net profit / increase in the travelled distance) ratio
                            bestH = plusProfit / (plus - minus);
                            // save the ID of the vertex to insert
                            besti = i;
                            // save the index in the pathResult where the new vertex should be inserted
                            bestj = j;
                            // save the distance increase caused by adding the new vertex
                            plusLength = plus - minus;
                            // save the agent number
                            agent = agentNo;
                        }
                    }
                }
            }
        }

        double profitIncrease = 0.0;
        if (besti == -1) {
            return profitIncrease;
        }

        Place placeToInsert = places[besti];
        profitIncrease = placeToInsert.getFirmProfit();
        // update the PathResult for given agent
        pathResult[agent].increasePathLength(plusLength);
        pathResult[agent].increaseActualProfit(profitIncrease);
        pathResult[agent].getResultPath().add(bestj, placeToInsert);
        // add the newly inserted vertex to the list of visited
        visited[besti] = true;

        return profitIncrease;
    }
}
