package org.bihe.controll;

import org.bihe.bean.*;
import org.bihe.exception.ExistElementException;

import java.util.*;

public class MainController {
    public static HashMap<Integer, Feature> features = new HashMap<>();

    /**
     * Add feature to features list
     * @param id Id of feature
     * @param type Type of feature
     * @param label Label of feature
     * @throws ExistElementException throw if this feature exists
     */
    public static void addFeature(int id, FeatureType type, String label) throws ExistElementException {
        if (!validateFeature(type, label)) throw new IllegalStateException("This type cannot set to this label!");
        var feature = new Feature(id, type, label);
        if (features.putIfAbsent(id, feature) != null) throw new ExistElementException("This feature exists!");
    }

    /**
     * Return true if given label matches with given type
     * @param type Feature type
     * @param label Feature label
     * @return true if given label matches with given type
     */
    private static boolean validateFeature(FeatureType type, String label) {
        return (type.equals(FeatureType.HASHTAG) && label.charAt(0) == '#') || (type.equals(FeatureType.MENTION) && label.charAt(0) == '@');
    }

    private static void initializeGraph(Graph graph) {
        FileManagement.initFeatures(FileManagement.FEATURES_PATH);
        FileManagement.initNodes(graph, FileManagement.NODES_PATH);
        FileManagement.initEdges(graph, FileManagement.EDGES_PATH);
        FileManagement.initFeatureMap(graph, FileManagement.FEATURES_MAP_PATH);
    }

    private static void writeData(Graph graph) {
        FileManagement.writeToEdges(graph);
        FileManagement.writeToFeatureMap(graph);
        FileManagement.writeToFeatures();
        FileManagement.writeToNodes(graph);
    }

    public static void mainMenu() {
        System.out.println("WELCOME");
        Scanner sc = new Scanner(System.in);
        Graph graph = new Graph();
        initializeGraph(graph);
        Loop:
        while (true) {
            printMainMenu();
            try {
                byte in = sc.nextByte();
                switch (in) {
                    case 1 -> modelMenu(graph, sc);
                    case 2 -> changeModelMenu(graph, sc);
                    case 3 -> {
                        writeData(graph);
                        break Loop;
                    }
                    default -> System.out.println("Please enter a correct number!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number!");
                sc.next();
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("Please choose one of choices by entering number:\n" +
                "1. Menu\n2. Change Model\n3. Exit");
    }

    private static void modelMenu(Graph graph, Scanner sc) {
        Loop:
        while (true) {
            printModelMenu();
            try {
                byte in = sc.nextByte();
                switch (in) {
                    case 1:
                        printThreeTop(graph.threeTop(graph.calculateFollowersOrLikes(NodeType.user, EdgeType.follow)));
                        break;
                    case 2:
                        printThreeTop(graph.threeTop(graph.calculateFollowersOrLikes(NodeType.post, EdgeType.like)));
                        break;
                    case 3:
                        printThreeTop(graph.threeTop(graph.calculateHashtagsOrMentions(FeatureType.HASHTAG)));
                        break;
                    case 4:
                        printThreeTop(graph.threeTop(graph.calculateHashtagsOrMentions(FeatureType.MENTION)));
                        break;
                    case 5:
                        System.out.println("Please enter a userID:");
                        var userID = sc.next();
                        System.out.println("----------------------------------------");
                        try {
                            String suggested = graph.suggestedItem(userID, NodeType.user);
                            System.out.println(suggested);
                        } catch (NoSuchElementException | IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("----------------------------------------");
                        break;
                    case 6:
                        System.out.println("Please enter a userID:");
                        var userID2 = sc.next();
                        System.out.println("----------------------------------------");
                        try {
                            String suggested = graph.suggestedItem(userID2, NodeType.post);
                            System.out.println(suggested);
                        } catch (NoSuchElementException | IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("----------------------------------------");
                        break;
                    case 7:
                        densityMenu(graph, sc);
                        break;
                    case 8:
                        forFindingPath(graph, sc);
                        break;
                    case 9:
                        printConnectedGraph(graph);
                        break;
                    case 10:
                        System.out.println("Please enter a userID: ");
                        String userID3 = sc.next();
                        System.out.println("----------------------------------------");
                        try {
                            int betweenneesCentrality = graph.betweennessCentrality(userID3);
                            System.out.println(betweenneesCentrality);
                        } catch (IllegalArgumentException | NoSuchElementException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println("----------------------------------------");
                        break;
                    case 11:
                        break Loop;
                    default:
                        System.out.println("Please enter correct number!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter number!");
                sc.next();
            }

        }
    }

    private static void printModelMenu() {
        System.out.println("1. Print the 3 users who have the most followers\n" +
                "2. Print 3 posts with the most number of likes\n" +
                "3. Print 3 most repetitive hashtags\n" +
                "4. Print 3 most repetitive mentions\n" +
                "5. Give you suggested user\n" +
                "6. Give you suggested post\n" +
                "7. Density of relationships\n" +
                "8. Find path\n" +
                "9. Connective most users\n" +
                "10. Betweenness centrality\n" +
                "11. Exit");
    }

    private static void densityMenu(Graph graph, Scanner sc) {
        Loop:
        while (true) {
            printDensityMenu();
            try {
                byte in = sc.nextByte();
                System.out.println("----------------------------------------");
                switch (in) {
                    case 1:
                        System.out.println(graph.densityOfUsersPercent() + "%");
                        break;
                    case 2:
                        System.out.println(graph.densityJustBetweenUsersAndPostsPercent() + "%");
                        break;
                    case 3:
                        System.out.println(graph.densityTotalBetweenUsersAndPostsPercent() + "%");
                        break;
                    case 4:
                        break Loop;
                    default:
                        System.out.println("Please enter correct number");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter number!");
                sc.nextLine();
            }
            System.out.println("----------------------------------------");

        }

    }

    private static void printDensityMenu() {
        System.out.println("1. Density of users\n2. Density just between users and posts\n" +
                "3. Density between users and posts totally\n" +
                "4. Exit");
    }

    private static void changeModelMenu(Graph graph, Scanner sc) {
        Loop:
        while (true) {
            printChangeModelMenu();
            try {
                byte in = sc.nextByte();
                System.out.println("----------------------------------------");
                switch (in) {
                    case 1:
                        addNode(graph, sc);
                        break;
                    case 2:
                        addEdge(graph, sc);
                        break;
                    case 3:
                        addFeatureUI(graph, sc);
                        break;
                    case 4:
                        removeNode(graph, sc);
                        break;
                    case 5:
                        removeEdge(graph, sc);
                        break;
                    case 6:
                        removeFeature(graph, sc);
                        break;
                    case 7:
                        break Loop;
                    default:
                        System.out.println("Please enter a correct number!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number!");
                sc.next();
            }
            System.out.println("----------------------------------------");

        }
    }

    private static void printChangeModelMenu() {
        System.out.println("1. Add node\n2. Add edge\n3. Add feature\n4. Remove node\n5. Remove edge\n6. Remove feature" +
                "\n7. Exit");
    }

    private static void printThreeTop(String[] items) {
        System.out.println("----------------------------------------");
        for (int i = 0; i < items.length; i++) {
            System.out.println((i + 1) + ". " + items[i]);
        }
        System.out.println("----------------------------------------");
    }

    private static void forFindingPath(Graph graph, Scanner sc) {
        System.out.println("Please enter first userID: ");
        String userID1 = sc.next();
        System.out.println("Please enter second userID: ");
        String userID2 = sc.next();
        findingPathMenu();
        byte in;
        try {
            in = sc.nextByte();
            System.out.println("----------------------------------------");
            Set<Node> path = graph.findPath(userID1, userID2, in);
            if (path.isEmpty()) System.out.println("No path found!");
            for (var node : path) {
                System.out.println("ID: " + node.getId() + " Name: " + node.getLabel());
            }
        } catch (InputMismatchException e) {
            System.out.println("Please enter a number");
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Please enter correct number!");
        }
        System.out.println("----------------------------------------");
    }

    private static void findingPathMenu() {
        System.out.println("Please choose algorithm:\n1. Find path DFS\n2. Find path BFS");
    }

    private static void printConnectedGraph(Graph graph) {
        StringBuilder sb = new StringBuilder();
        for (var node : graph.mostNumberOfUsersConnectedGraph()) {
            sb.append(node.getId());
            sb.append(":[");
            for (var e : graph.getAdjacencyList().get(node)) {
                sb.append(e.getDestination().getId()).append(", ");
            }
            sb.append("]\n");
        }
        System.out.println("----------------------------------------");
        System.out.println(sb);
        System.out.println("----------------------------------------");
    }

    private static void addNode(Graph graph, Scanner sc) {
        System.out.println("Please enter a ID: ");
        String id = sc.next();
        System.out.println("Please enter a label: ");
        String label = sc.next();
        System.out.println("Please enter a type: ");
        String type = sc.next();
        try {
            graph.addNode(id, label, type);
            System.out.println("Node added successfully.");
        } catch (ExistElementException e) {
            System.out.println("Unfortunately, this node cannot be added!\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Unfortunately, this node cannot be added!\nThis type not exists. You can enter these types: " + Arrays.toString(NodeType.values()));
        }
    }

    private static void addEdge(Graph graph, Scanner sc) {
        System.out.println("Please enter a source node ID: ");
        String to = sc.next();
        System.out.println("Please enter a destination node ID: ");
        String from = sc.next();
        System.out.println("Please enter a type: ");
        String type = sc.next();
        try {
            graph.addEdge(to, from, type);
            System.out.println("Edge added successfully.");
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("Unfortunately, this edge cannot be added!\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Unfortunately, this edge cannot be added!\nThis type not exists. You can enter these types: " + Arrays.toString(EdgeType.values()));
        }
    }

    private static void addFeatureUI(Graph graph, Scanner sc) {
        System.out.println("Please enter a post ID or message ID:");
        String nodeID = sc.next();
        System.out.println("Do you want add new feature or you want to use a existing feature?\n1. Add new feature\n" +
                "2. Use a exiting feature");
        try {
            byte in = sc.nextByte();
            switch (in) {
                case 1 -> {
                    System.out.println("Please enter a feature ID:");
                    String id = sc.next();
                    int idC = Integer.parseInt(id);
                    System.out.println("Please enter a label:");
                    String label = sc.next();
                    System.out.println("Please enter a type: " + Arrays.toString(FeatureType.values()));
                    String type = sc.next();
                    addFeature(idC, FeatureType.valueOf(type.toUpperCase()), label);
                    System.out.println("Feature just added successfully to the feature list.");
                    graph.addFeature(nodeID, idC);
                    System.out.println("Feature added successfully to the node.");
                }
                case 2 -> {
                    System.out.println("Please enter a feature ID:");
                    String id = sc.next();
                    int idC = Integer.parseInt(id);
                    graph.addFeature(nodeID, idC);
                    System.out.println("Feature added successfully to the node.");
                }
                default -> System.out.println("Please enter correct number!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Unfortunately, this feature cannot be added!\n" + "The feature id is a number!");
        } catch (InputMismatchException e) {
            System.out.println("Please enter a number!");
        } catch (ExistElementException | IllegalArgumentException | NoSuchElementException | IllegalStateException e) {
            System.out.println("Unfortunately, this feature cannot be added!\n" + e.getMessage());
        }
    }

    private static void removeNode(Graph graph, Scanner sc) {
        System.out.println("Please enter the node ID: ");
        String id = sc.next();
        if (!graph.removeNode(id)) {
            System.out.println("This node cannot be found!");
        } else System.out.println("Node removed successfully.");
    }

    private static void removeEdge(Graph graph, Scanner sc) {
        System.out.println("Please enter the source ID: ");
        String from = sc.next();
        System.out.println("Please enter the destination ID: ");
        String to = sc.next();
        System.out.println("Please enter the type: ");
        String type = sc.next();
        try {
            if (!graph.removeEdge(from, to, type)) {
                System.out.println("This edge not be found!");
            } else System.out.println("Edge removed successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Unfortunately, this edge cannot be removed!\nThis type not exists. You can enter these types: " + Arrays.toString(EdgeType.values()));
        }
    }

    private static void removeFeature(Graph graph, Scanner sc) {
        System.out.println("Do you want remove totally feature or just remove from a node?\n1. Remove Totally\n2. Remove from a node");
        try {
            byte in = sc.nextByte();
            switch (in) {
                case 1 -> {
                    System.out.println("Please enter a feature ID: ");
                    String fID = sc.next();
                    int featureId = Integer.parseInt(fID);
                    if (features.get(featureId) == null) throw new NoSuchElementException("This feature not be found!");
                    for (var node : graph.getFeatures().keySet()) {
                        graph.getFeatures().get(node).remove(features.get(featureId));
                    }
                    features.remove(featureId);
                    System.out.println("Feature removed successfully.");
                }
                case 2 -> {
                    System.out.println("Please enter a feature ID: ");
                    String fID = sc.next();
                    int featureId = Integer.parseInt(fID);
                    if (features.get(featureId) == null) throw new NoSuchElementException("This feature not be found!");
                    System.out.println("Please enter a nodeID: ");
                    String nodeID = sc.next();
                    if (!graph.removeFeature(nodeID, featureId)) {
                        System.out.println("This node not be found!");
                    } else System.out.println("Feature removed successfully.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number for featureID!");
        } catch (InputMismatchException e) {
            System.out.println("Please enter a number!");
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}

