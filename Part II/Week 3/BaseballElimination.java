import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.Map;


public class BaseballElimination {
    private HashMap<String, Integer> teamMap;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] games;
    private int numberOfTeams;
    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        teamMap = new HashMap<>();

        In in = new In(filename);
        numberOfTeams = in.readInt();
        in.readLine(); // mark the numberOfTeams line as read
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remaining = new int[numberOfTeams];
        games = new int[numberOfTeams][numberOfTeams];
        String[] teamInfo;
        int i = 0;
        while (in.hasNextLine()) {
            teamInfo = in.readLine().trim().split("\\s+");
            if (teamInfo.length != numberOfTeams + 4) {
                throw new IllegalArgumentException("Invalid filename format. TeamInfo is lenght: " + teamInfo.length + " instead of " + (numberOfTeams + 4));
            }
            teamMap.put(teamInfo[0], i);
            wins[i] = Integer.parseInt(teamInfo[1]);
            losses[i] = Integer.parseInt(teamInfo[2]);
            remaining[i] = Integer.parseInt(teamInfo[3]);
            for (int j = 0; j < numberOfTeams; j++) {
                games[i][j] = Integer.parseInt(teamInfo[4+j]);
            }
            i++;
        }
    }
    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }
    // all teams
    public Iterable<String> teams() {
        return teamMap.keySet();
    }
    // number of wins for given team
    public int wins(String team) {
        checkTeam(team);
        return wins[teamMap.get(team)];
    }
    // number of losses for given team
    public int losses(String team) {
        checkTeam(team);
        return losses[teamMap.get(team)];
    }
    // number of remaining games for given team
    public int remaining(String team) {
        checkTeam(team);
        return remaining[teamMap.get(team)];
    }
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkTeam(team1);
        checkTeam(team2);
        return games[teamMap.get(team1)][teamMap.get(team2)];
    }
    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkTeam(team);
        if (triviallyEliminated(team)) {
            return true;
        }
        FordFulkerson ff = buildNetwork(team);
        int fullCapacity = getFullCapacity(team);
        return fullCapacity > ff.value();
    }
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        checkTeam(team);
        Bag<String> eliminatedTeams = new Bag<>();
        int x = teamMap.get(team);
        if (triviallyEliminated(team)) {
            for (Map.Entry<String, Integer> entry: teamMap.entrySet()) {
                if (!entry.getKey().equals(team)) {
                    int i = entry.getValue();
                    if (wins[x] + remaining[x] < wins[i]) {
                        eliminatedTeams.add(entry.getKey());
                    }
                }
            }
            return eliminatedTeams;
        }
        FordFulkerson ff = buildNetwork(team);
        for (Map.Entry<String, Integer> entry: teamMap.entrySet()) {
            int i = entry.getValue();
            int mappedI;
            if (i < x) {
                mappedI = i + 1;
            }
            else if (i == x) {
                continue;
            }
            else {
                mappedI = i + 2;
            }
            if (ff.inCut(mappedI)) {
                eliminatedTeams.add(entry.getKey());
            }
        }
        return eliminatedTeams;
    }

    private int sinkCapacity(int i, int x) {
        return Math.max(wins[x] + remaining[x] - wins[i], 0);
    }

    private boolean triviallyEliminated(String team) {
        int x = teamMap.get(team);
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == x) {
                continue;
            }
            if (wins[x] + remaining[x] < wins[i]) {
                return true;
            }
        }
        return false;
    }

    private int getFullCapacity(String team) {
        int x = teamMap.get(team);
        int capacity = 0;
        for (int i = 0; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (i == x || j == x) {
                    continue;
                }
                capacity += games[i][j];
            }
        }
        return capacity;
    }

    private FordFulkerson buildNetwork(String team) {
        // (n-1)!/2!*(n-3)!: number of matches is combinations of (n-1) with 2
        int numberOfMatchVertices = (numberOfTeams*numberOfTeams - 3*numberOfTeams + 3)/2;
        // V = source + sink + team vertices + match vertices
        int numberOfVertices = 2 + (numberOfTeams - 1) + numberOfMatchVertices;
        FlowNetwork G = new FlowNetwork(numberOfVertices);
        int x = teamMap.get(team);
        // create team-sink edges
        // i is index of the vertex that represents the teams on the network, beginning in 1
        // there are numberOfTeams - 1 teams in the network
        // i matches team id offseted by 1 on map for every i less than x
        // from i == x, there is no offset between network and map representation
        FlowEdge matchSinkEdge;
        for (int i = 1; i < numberOfTeams; i++) {
            if (i - 1 < x) {
                matchSinkEdge = new FlowEdge(i, numberOfVertices - 1, sinkCapacity(i - 1, x));
            }
            else {
                matchSinkEdge = new FlowEdge(i, numberOfVertices - 1, sinkCapacity(i, x));
            }
            G.addEdge(matchSinkEdge);
        }
        // first match vertex has index == numberOfTeams
        int k = numberOfTeams;
        FlowEdge sourceMatchEdge;
        FlowEdge matchTeamEdge;
        for (int i = 1; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j - 1 < x && i - 1 < x) {
                    sourceMatchEdge = new FlowEdge(0, k, games[i-1][j-1]);
                }
                else if (i - 1 < x) {
                    sourceMatchEdge = new FlowEdge(0, k, games[i-1][j]);
                }
                else {
                    sourceMatchEdge = new FlowEdge(0, k, games[i][j]);
                }
                G.addEdge(sourceMatchEdge);
                matchTeamEdge = new FlowEdge(k, i, Double.POSITIVE_INFINITY);
                G.addEdge(matchTeamEdge);
                matchTeamEdge = new FlowEdge(k, j, Double.POSITIVE_INFINITY);
                G.addEdge(matchTeamEdge);
                k++;
            }
        }
        FordFulkerson ff = new FordFulkerson(G, 0, numberOfVertices - 1);
        return ff;
    }

    private void checkTeam(String team) {
        if (!teamMap.containsKey(team)) {
            throw new IllegalArgumentException("Team " + team + "is not a part of this division");
        }
    }

    // unit tests
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
