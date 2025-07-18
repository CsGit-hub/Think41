import java.util.*;

public class SpreadsheetDependencyResolver {

    // Adjacency list to store cell dependencies
    private Map<String, Set<String>> dependencyGraph = new HashMap<>();

    // Add a cell and its dependencies (precedents)
    public void addCellDependencies(String cell, List<String> precedents) {
        dependencyGraph.putIfAbsent(cell, new HashSet<>());
        for (String dep : precedents) {
            dependencyGraph.get(cell).add(dep);
            dependencyGraph.putIfAbsent(dep, new HashSet<>()); // ensure node exists
        }
    }

    // Topological Sort using Kahn's Algorithm
    public List<String> getRecalculationOrder() throws Exception {
        // Calculate in-degree
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : dependencyGraph.keySet()) {
            inDegree.putIfAbsent(node, 0);
        }

        for (String node : dependencyGraph.keySet()) {
            for (String dep : dependencyGraph.get(node)) {
                inDegree.put(dep, inDegree.getOrDefault(dep, 0) + 1);
            }
        }

        // Queue for zero in-degree nodes
        Queue<String> queue = new LinkedList<>();
        for (String node : inDegree.keySet()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        List<String> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            for (String neighbor : dependencyGraph.getOrDefault(current, new HashSet<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        if (result.size() != dependencyGraph.size()) {
            throw new Exception("Cycle detected! Circular dependency exists.");
        }

        return result;
    }

    // Driver code
    public static void main(String[] args) {
        try {
            SpreadsheetDependencyResolver resolver = new SpreadsheetDependencyResolver();

            // Define dependencies (example)
            resolver.addCellDependencies("C1", Arrays.asList("B2"));
            resolver.addCellDependencies("B2", Arrays.asList("A1"));
            resolver.addCellDependencies("D1", Arrays.asList("A1", "C1"));
            resolver.addCellDependencies("A1", new ArrayList<>()); // no dependencies

            List<String> order = resolver.getRecalculationOrder();
            System.out.println("Recalculation order: " + order);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}