package org.bihe.bean;

import org.bihe.controll.MainController;
import org.bihe.exception.ExistElementException;

import java.util.*;

public class Graph {

    private final HashMap<String, Node> nodes;
    private final HashMap<Node, List<Edge>> adjacencyList;
    private final HashMap<Node, List<Feature>> features;

    public Graph() {
        nodes = new HashMap<>();
        adjacencyList = new HashMap<>();
        features = new HashMap<>();
    }

    /*
    Methods for changing Graph (Add and Remove)
     */
    public void addNode(String id, String label, String type) throws ExistElementException {
        var node = new Node(id, label, NodeType.valueOf(type));
        if (nodes.putIfAbsent(id, node) != null) throw new ExistElementException("This id exists!");
        if ((NodeType.valueOf(type).equals(NodeType.user))) {
            adjacencyList.putIfAbsent(node, new LinkedList<>());
        } else {
            adjacencyList.putIfAbsent(node, new LinkedList<>());
            features.putIfAbsent(node, new LinkedList<>());
        }
    }

    public boolean removeNode(String id) {
        Node node = nodes.get(id);
        if (node == null) return false;

        for (var n : adjacencyList.keySet()) {
            while (true) {
                if (!adjacencyList.get(n).removeIf(edge -> edge.getDestination().equals(node))) break;
            }

        }
        adjacencyList.remove(node);
        nodes.remove(id);
        return true;
    }

    public void addEdge(String from, String to, String type) {
        var fromNode = nodes.get(from);
        if (fromNode == null) throw new NoSuchElementException("Your source node not be found!");

        var toNode = nodes.get(to);
        if (toNode == null) throw new NoSuchElementException("Your destination node not be found!");

        if (!validateEdge(fromNode, toNode, EdgeType.valueOf(type)))
            throw new IllegalStateException("You cannot set this type between these nodes!");

        var edge = new Edge(toNode, EdgeType.valueOf(type));
        adjacencyList.get(fromNode).add(edge);
    }

    private boolean validateEdge(Node from, Node to, EdgeType type) {
        return (from.getType().equals(NodeType.user) && to.getType().equals(NodeType.user) && type.equals(EdgeType.follow))
                || (from.getType().equals(NodeType.user) && (to.getType().equals(NodeType.post) || to.getType().equals(NodeType.message))
                && (type.equals(EdgeType.like) || type.equals(EdgeType.comment)));
    }

    public boolean removeEdge(String from, String to, String type) {
        var fromNode = nodes.get(from);
        var toNode = nodes.get(to);
        return removeEdge(fromNode, toNode, EdgeType.valueOf(type), adjacencyList);
    }

    private boolean removeEdge(Node fromNode, Node toNode, EdgeType type, HashMap<Node, List<Edge>> adjList) {
        if (fromNode == null || toNode == null) return false;
        Edge e = new Edge(toNode, type);
        return adjList.get(fromNode).removeIf(edge -> edge.equals(e));
    }

    public void addFeature(String nodeID, int featureID) throws ExistElementException {
        var node = nodes.get(nodeID);
        if (node == null) throw new NoSuchElementException("Your node not be found!");
        if (node.getType().equals(NodeType.user))
            throw new IllegalArgumentException("Please enter node that type is post or message!");

        var feature = MainController.features.get(featureID);
        if (feature == null) throw new NoSuchElementException("Your feature not be found!");
        if (features.get(node).contains(feature))
            throw new ExistElementException("This feature is exits for this node!");
        features.get(node).add(feature);
    }

    public boolean removeFeature(String nodeID, int featureID) {
        var node = nodes.get(nodeID);
        var feature = MainController.features.get(featureID);
        if (node == null || feature == null) return false;
        if (node.getType().equals(NodeType.user)) throw new IllegalArgumentException("Node type cannot be user!");
        features.get(node).remove(feature);
        return true;
    }

    //-----------------------------Methods for 1,2,3,4 questions---------------------------

    /**
     * Gets node type for example user or post and edge type for example following for user and like for posts
     * then calculate number of followers for each users or number of likes for each posts
     *
     * @param nodeType Type of node
     * @param edgeType Type of edge
     * @return Hashtable that the key is label of node and the value is number of followers or likes
     */
    public HashMap<String, Integer> calculateFollowersOrLikes(NodeType nodeType, EdgeType edgeType) {
        HashMap<String, Integer> itemsNumber = new HashMap<>();
        for (var node : nodes.values()) {
            if (!node.getType().equals(nodeType)) continue;
            int count = 0;
            Edge e = new Edge(node, edgeType);
            for (var list : adjacencyList.values()) {
                if (list.contains(e)) {
                    count++;
                }
            }
            itemsNumber.putIfAbsent(node.getLabel(), count);
        }
        return itemsNumber;
    }

    /**
     * Gets feature type and calculate number of each feature according to their type
     *
     * @param featureType Type of feature
     * @return Hashtable that the key is label of feature and the value is number of that feature
     */
    public HashMap<String, Integer> calculateHashtagsOrMentions(FeatureType featureType) {
        HashMap<String, Integer> itemsNumber = new HashMap<>();
        for (var feature : MainController.features.values()) {
            if (!feature.getType().equals(featureType)) continue;
            int count = 0;
            for (var list : features.values()) {
                if (list.contains(feature)) {
                    count++;
                }
            }
            itemsNumber.putIfAbsent(feature.getLabel(), count);
        }
        return itemsNumber;
    }

    /**
     * Gets Hashtable of items that have the number for value then return keys that have max value
     *
     * @param calculateItems Hashtable of items
     * @return Array of max keys
     */
    public String[] threeTop(HashMap<String, Integer> calculateItems) {
        HashMap<String, Integer> itemsNumber = new HashMap<>(calculateItems);
        String[] threeTop = new String[3];
        for (int i = 0; i < threeTop.length; i++) {
            Pair<String, Integer> pair = getMaxFromHashTable(itemsNumber);
            String selectedItem = pair.getFirst();
            itemsNumber.remove(selectedItem);
            threeTop[i] = selectedItem;
        }
        return threeTop;
    }

    /**
     * Gets Hashtable that the keys are labels and the values are numbers and return max key
     *
     * @param map Hashtable
     * @return Pair of key value that value is max
     */
    private Pair<String, Integer> getMaxFromHashTable(Map<String, Integer> map) {
        int max = 0;
        var selectedItem = "";
        for (var item : map.keySet()) {
            int numbers = map.get(item);
            if (numbers > max) {
                max = numbers;
                selectedItem = item;
            }
        }
        return new Pair<>(selectedItem, max);
    }

    //-------------------------------------------------------------------------
    //-------------------------Methods of 5 and 6 questions--------------------

    /**
     * Gets id of user and return lists of friends
     *
     * @param userID User id
     * @return List of friends id
     */
    public List<String> listOfFriends(String userID) {
        var userNode = nodes.get(userID);
        if (userNode == null) throw new NoSuchElementException("This user not be found!");
        if (!userNode.getType().equals(NodeType.user))
            throw new IllegalArgumentException("Your input type is not user!");
        LinkedList<String> friends = new LinkedList<>();
        for (var edge : adjacencyList.get(userNode)) {
            if (!edge.getType().equals(EdgeType.follow)) continue;
            Edge e = new Edge(userNode, EdgeType.follow);
            if (adjacencyList.get(edge.getDestination()).contains(e))
                friends.add(edge.getDestination().getId());
        }
        return friends;
    }

    /**
     * Gets id of user and returns list of followings
     *
     * @param userID Id of user
     * @return List of users id that given user id follows them
     */
    public List<String> listOfFollowings(String userID) {
        var userNode = nodes.get(userID);
        if (userNode == null) throw new NoSuchElementException("This user not be found!");
        LinkedList<String> followings = new LinkedList<>();
        for (var edge : adjacencyList.get(userNode)) {
            if (edge.getType().equals(EdgeType.follow)) followings.add(edge.getDestination().getId());
        }
        return followings;
    }

    /**
     * Gets id of user and returns posts that user like or comment on this posts
     *
     * @param userID Id of user
     * @return List of posts id
     */
    public List<String> listOfPosts(String userID) {
        var userNode = nodes.get(userID);
        if (userNode == null) throw new NoSuchElementException("This user not be found!");
        LinkedList<String> posts = new LinkedList<>();
        for (var edge : adjacencyList.get(userNode)) {
            if (edge.getDestination().getType().equals(NodeType.post)) posts.add(edge.getDestination().getId());
        }
        return posts;
    }

    /**
     * Gets type of relation and id of user then calculates number of relations for each node by type of relations
     * that have relationships between given user and other users
     *
     * @param type   Type of relation
     * @param userID Id of user
     * @return Hashtable
     */
    private HashMap<String, Integer> relation(RelationType type, String userID) {
        HashMap<String, Integer> relation = new HashMap<>();
        switch (type) {
            case FriendsOfFriends -> {
                for (String uID : listOfFriends(userID)) {
                    for (String u2ID : listOfFriends(uID)) {
                        if (u2ID.equals(userID)) continue;
                        relation.merge(u2ID, 1, Integer::sum);
                    }
                }
            }
            case FollowingsOfFriends -> {
                for (String uID : listOfFriends(userID)) {
                    for (String u2ID : listOfFollowings(uID)) {
                        if (u2ID.equals(userID)) continue;
                        relation.merge(u2ID, 1, Integer::sum);
                    }
                }
            }
            case FriendsOfFollowings -> {
                for (String uID : listOfFollowings(userID)) {
                    for (String u2ID : listOfFriends(uID)) {
                        if (u2ID.equals(userID)) continue;
                        relation.merge(u2ID, 1, Integer::sum);
                    }
                }
            }
            case FollowingsOfFollowings -> {
                for (String uID : listOfFollowings(userID)) {
                    for (String u2ID : listOfFollowings(uID)) {
                        if (u2ID.equals(userID)) continue;
                        relation.merge(u2ID, 1, Integer::sum);
                    }
                }
            }
            case PostsOfFriends -> {
                for (String uID : listOfFriends(userID)) {
                    for (String postID : listOfPosts(uID)) {
                        relation.merge(postID, 1, Integer::sum);
                    }
                }
            }
            case PostsOfFollowings -> {
                for (String uID : listOfFollowings(userID)) {
                    for (String postID : listOfPosts(uID)) {
                        relation.merge(postID, 1, Integer::sum);
                    }
                }
            }
        }
        return relation;
    }

    /**
     * Gets user ID and type of node that can be post or user then returns suggested item according to number of
     * relationships
     *
     * @param userID id of user
     * @param type   Type of node
     * @return Suggested item
     */
    public String suggestedItem(String userID, NodeType type) {
        String suggestedItem = null;
        if (type.equals(NodeType.user)) {
            suggestedItem = getMaxFromHashTable(relation(RelationType.FriendsOfFriends, userID)).getFirst();
            if (suggestedItem.isEmpty())
                suggestedItem = getMaxFromHashTable(relation(RelationType.FollowingsOfFriends, userID)).getFirst();
            if (suggestedItem.isEmpty())
                suggestedItem = getMaxFromHashTable(relation(RelationType.FriendsOfFollowings, userID)).getFirst();
            if (suggestedItem.isEmpty())
                suggestedItem = getMaxFromHashTable(relation(RelationType.FollowingsOfFollowings, userID)).getFirst();
        } else if (type.equals(NodeType.post)) {
            suggestedItem = getMaxFromHashTable(relation(RelationType.PostsOfFriends, userID)).getFirst();
            if (suggestedItem.isEmpty())
                suggestedItem = getMaxFromHashTable(relation(RelationType.PostsOfFollowings, userID)).getFirst();
        }
        return nodes.get(suggestedItem).getLabel();
    }

    //-------------------------------------------------------------------------
    //----------------------------Methods of Question 7------------------------

    /**
     * Calculates number of all post in graph
     *
     * @return Number of posts
     */
    public int numberOfAllPosts() {
        int counter = 0;
        for (var node : nodes.values()) {
            if (node.getType().equals(NodeType.post)) counter++;
        }
        return counter;
    }

    /**
     * Calculates numbers of relations between All users
     *
     * @return List of numbers
     */
    public LinkedList<Integer> numbersOfRelationsBetweenEachUserWithOtherUsers() {
        LinkedList<Integer> numbers = new LinkedList<>();
        for (var nodeID : nodes.keySet()) {
            if (!nodes.get(nodeID).getType().equals(NodeType.user)) continue;
            HashSet<String> relations = new HashSet<>(listOfFollowings(nodeID));
            for (var node : nodes.values()) {
                if (!node.getType().equals(NodeType.user)) continue;
                Edge e = new Edge(nodes.get(nodeID), EdgeType.follow);
                if (adjacencyList.get(node).contains(e)) {
                    relations.add(node.getId());
                }
            }
            numbers.add(relations.size());
        }
        return numbers;
    }

    /**
     * Calculates numbers of relations between each user with posts
     *
     * @return List of numbers
     */
    public LinkedList<Integer> numbersOfRelationsBetweenEachUserWithPosts() {
        LinkedList<Integer> numbers = new LinkedList<>();
        for (var nodeID : nodes.keySet()) {
            if (!nodes.get(nodeID).getType().equals(NodeType.user)) continue;
            HashSet<String> relations = new HashSet<>(listOfPosts(nodeID));
            numbers.add(relations.size());
        }
        return numbers;
    }

    /**
     * Density just between users
     *
     * @return Density percent
     */
    public float densityOfUsersPercent() {
        int sum = 0;
        LinkedList<Integer> numbers = numbersOfRelationsBetweenEachUserWithOtherUsers();
        for (var num : numbers) sum += num;
        return (float) (Math.round(((float) sum / numbers.size() / numbers.size()) * 100 * 100.0) / 100.0);
    }

    /**
     * Density Just between users and posts
     *
     * @return Density percent
     */
    public float densityJustBetweenUsersAndPostsPercent() {
        int sum = 0;
        int numberOfPosts = numberOfAllPosts();
        LinkedList<Integer> numbers = numbersOfRelationsBetweenEachUserWithPosts();
        for (var num : numbers) sum += num;
        return (float) (Math.round((float) sum / (numberOfPosts * numbers.size()) * 100 * 100.0) / 100.0);
    }

    /**
     * Density between Users and Posts
     *
     * @return Density percent
     */
    public float densityTotalBetweenUsersAndPostsPercent() {
        int sum = 0;
        int numberOfPosts = numberOfAllPosts();
        LinkedList<Integer> numbers1 = numbersOfRelationsBetweenEachUserWithOtherUsers();
        LinkedList<Integer> numbers2 = numbersOfRelationsBetweenEachUserWithPosts();
        for (var num : numbers1) sum += num;
        for (var num : numbers2) sum += num;
        return (float) (Math.round((float) sum / (numberOfPosts + numbers1.size()) / (numberOfPosts + numbers1.size()) * 100 * 100.0) / 100.0);
    }

    //-------------------------------------------------------------------------
    //--------------------------Methods of Question 8--------------------------

    /**
     * Gets first node, second node and byte input that input specifies what algorithm do we want for
     * finding path (BFS or DFS)
     * @param nodeID1 first node id
     * @param nodeID2 second node id
     * @param in The input that specifies the algorithm
     * @return Set of nodes (path) between two entered node
     */
    public Set<Node> findPath(String nodeID1, String nodeID2, byte in) {
        var node1 = nodes.get(nodeID1);
        var node2 = nodes.get(nodeID2);
        if (node1 == null) throw new NoSuchElementException("The source node not be found!");
        if (node2 == null) throw new NoSuchElementException("The destination node not be found!");
        if (!node1.getType().equals(NodeType.user)) throw new IllegalArgumentException("Source node type is not user!");
        if (!node2.getType().equals(NodeType.user)) throw new IllegalArgumentException("Destination type is not user!");
        switch (in) {
            case 1:
                return findPathDFS(node1, node2, adjacencyList);
            case 2:
                Set<Node> ret = findPathBFS(nodeID1, nodeID2);
                return (ret.isEmpty()) ? findPathBFS(nodeID2, nodeID1) : ret;
            default:
                return null;
        }
    }

    /**
     * Supplementary method of findPathDFS
     * @param node1 First node
     * @param node2 Second node
     * @param adjList Adjacency List
     * @return Set of nodes that start with from node to second node (Path)
     */
    private Set<Node> findPathDFS(Node node1, Node node2, HashMap<Node, List<Edge>> adjList) {
        Set<Node> all = new LinkedHashSet<>(nodes.values());
        Set<Node> visiting = new LinkedHashSet<>();
        Set<Node> visited = new LinkedHashSet<>();
        findPathDFS(node1, node2, all, visiting, visited, adjList);
        if (visiting.isEmpty()) findPathDFS(node2, node1, all, visiting, visited, adjList);
        return visiting;
    }

    /**
     * Finding Path between two nodes by DFS algorithm
     *
     * @param n1       First node
     * @param n2       Second node
     * @param all      Set of all nodes
     * @param visiting Set of nodes that are in visiting state
     * @param visited  Set of nodes that are in visited state
     * @param adjList  Adjacency List
     */
    private void findPathDFS(Node n1, Node n2, Set<Node> all,
                             Set<Node> visiting, Set<Node> visited, HashMap<Node, List<Edge>> adjList) {
        if (visiting.contains(n2) || visiting.contains(n1) || adjList.get(n1) == null) return;
        all.remove(n1);
        visiting.add(n1);
        if (containsByNodeID(adjList.get(n1), n2.getId())) {
            visiting.add(n2);
            return;
        }
        for (var neighbour : adjList.get(n1)) {
            if (visited.contains(neighbour.getDestination()))
                continue;
            if (visiting.contains(n2)) return;
            else {
                findPathDFS(neighbour.getDestination(), n2, all, visiting, visited, adjList);
            }
        }

        visiting.remove(n1);
        visited.add(n1);
    }

    /**
     * Finding path between two nodes by BFS algorithm
     *
     * @param from First node
     * @param to   Second node
     * @return Set of nodes that start with from node to second node
     */
    private Set<Node> findPathBFS(String from, String to) {
        var fromNode = nodes.get(from);
        var toNode = nodes.get(to);

        Map<Node, Node> previousNodes = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        Set<Node> path = new LinkedHashSet<>();
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(fromNode);
        while (!queue.isEmpty()) {
            var current = queue.remove();
            visited.add(current);
            if (adjacencyList.get(current).isEmpty()) continue;
            for (var neighbor : adjacencyList.get(current)) {
                if (visited.contains(neighbor.getDestination())) continue;
                previousNodes.putIfAbsent(neighbor.getDestination(), current);
                queue.add(neighbor.getDestination());
            }
        }
        Stack<Node> stack = new Stack<>();
        stack.push(toNode);
        var previous = previousNodes.get(toNode);
        while (previous != null) {
            stack.push(previous);
            previous = previousNodes.get(previous);
        }
        while (!stack.isEmpty()) path.add(stack.pop());
        if (!path.contains(fromNode) || !path.contains(toNode)) path.clear();
        return path;
    }
    //-------------------------------------------------------------------------
    //-------------------------Methods of Question 9---------------------------

    /**
     * Gets root that is node id and gives nodes that visited with DFS on root
     * @param root node id
     * @return Set of Visited nodes
     */
    public Set<Node> DFS(String root) {
        var node = nodes.get(root);
        if (node == null) throw new NoSuchElementException("Root node not be found!");
        HashSet<Node> visited = new HashSet<>();
        Stack<Node> nodeStack = new Stack<>();
        Node current;
        nodeStack.push(node);
        while (!nodeStack.isEmpty()) {
            current = nodeStack.pop();
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            if (adjacencyList.get(current) == null) continue;
            for (var neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor.getDestination())) {
                    nodeStack.push(neighbor.getDestination());
                }
            }
        }
        return visited;
    }

    /**
     * Gives set of nodes by DFS on each nodes
     * @return Hashtable -> key: node id, value: set of nodes
     */
    private HashMap<String, Set<Node>> DFS_foreachNodes() {
        HashMap<String, Set<Node>> DFSs = new HashMap<>();
        for (var id : nodes.keySet()) {
            DFSs.put(id, DFS(id));
        }
        return DFSs;
    }

    /**
     * Calculates all connected graphs in this graph
     * @return Set of set of nodes that are connected
     */
    public Set<Set<Node>> connectedGraphs() {
        HashSet<Set<Node>> connectedGraphs = new HashSet<>();
        for (var DFS_list : DFS_foreachNodes().values()) {
            for (var connectedGraph : connectedGraphs) {
                if (hasCommonItem(DFS_list, connectedGraph)) {
                    DFS_list.addAll(connectedGraph);
                }
            }
            connectedGraphs.add(DFS_list);
        }
        HashSet<Set<Node>> cg = new HashSet<>();
        for (var connectedGraph : connectedGraphs) {
            for (var connectedGraph2 : connectedGraphs) {
                if (hasCommonItem(connectedGraph, connectedGraph2)) {
                    connectedGraph.addAll(connectedGraph2);
                }
            }
            cg.add(connectedGraph);
        }

        return cg;
    }

    /**
     * Calculates the connected graph that has most number of users
     * @return Set of nodes of this connected graph
     */
    public Set<Node> mostNumberOfUsersConnectedGraph() {
        Set<Set<Node>> connectedGraphs = connectedGraphs();
        Set<Node> max = new HashSet<>();
        int maxCounter = 0;
        for (var connectedGraph : connectedGraphs) {
            int counter = 0;
            for (var node : connectedGraph) if (node.getType().equals(NodeType.user)) counter++;
            if (counter > maxCounter) {
                maxCounter = counter;
                max = connectedGraph;
            }
        }
        return max;
    }

    /**
     * Checks that list of edges have this node id as destination
     * @param edges list of edges
     * @param nodeID id of node
     * @return true if one of edges has this node as destination
     */
    private boolean containsByNodeID(List<Edge> edges, String nodeID) {
        for (var e : edges) if (e.getDestination().getId().equals(nodeID)) return true;
        return false;
    }
    //-------------------------------------------------------------------------
    //-------------------------Methods of Question 10--------------------------

    /**
     * Gives shortest path between given two nodes
     * @param first First node id
     * @param second Second node id
     * @return Set of nodes between two given nodes
     */
    public Set<Node> getShortestPath(String first, String second) {
        Set<Node> firstPath = findPathBFS(first, second);
        Set<Node> secondPath = findPathBFS(second, first);
        if (!firstPath.isEmpty() && !secondPath.isEmpty()) {
            if (firstPath.size() < secondPath.size()) return firstPath;
            else return secondPath;
        } else if (firstPath.isEmpty() && !secondPath.isEmpty()) return secondPath;
        else if (!firstPath.isEmpty()) return firstPath;
        else return firstPath;
    }

    /**
     * Calculates shortest path between pair of nodes
     * @param nodes Nodes
     * @return Hashtable -> key: pair of nodes, value: set of nodes between these two nodes (Shortest Path)
     */
    private HashMap<Pair<Node, Node>, Set<Node>> getShortestPathsForPairNodes(Collection<Node> nodes) {
        HashMap<Pair<Node, Node>, Set<Node>> shortestPaths = new HashMap<>();
        for (var n1 : nodes) {
            for (var n2 : nodes) {
                Pair<Node, Node> pair = new Pair<>(n1, n2);
                if (containsPair(shortestPaths.keySet(), pair)) continue;
                shortestPaths.put(pair, getShortestPath(n1.getId(), n2.getId()));
            }
        }
        return shortestPaths;
    }

    /**
     * Return true if given pair of nodes in set of pairs
     * @param set Set of pairs of nodes
     * @param pair Pair of nodes
     * @return true if given pair of nodes in set of pairs
     */
    private static boolean containsPair(Set<Pair<Node, Node>> set, Pair<Node, Node> pair) {
        for (var p : set) {
            if (p.equals(pair)) return true;
        }
        return false;
    }

    /**
     * Shortest path of all pair of nodes in this graph
     * @return Hashtable -> key: pair of nodes, value: set of nodes between these two nodes (Shortest Path)
     */
    public HashMap<Pair<Node, Node>, Set<Node>> getShortestPathsForPairNodesOfGraph() {
        return getShortestPathsForPairNodes(nodes.values());
    }

    /**
     * Shortest path of all pair of nodes in most users connected graph
     * @return Hashtable -> key: pair of nodes, value: set of nodes between these two nodes (Shortest Path)
     */
    public HashMap<Pair<Node, Node>, Set<Node>> getShortestPathsForPairNodesOfConnectedGraph() {
        return getShortestPathsForPairNodes(mostNumberOfUsersConnectedGraph());
    }

    /**
     * Gets id of user and calculate betweenness centrality of this user
     * @param userID id of user
     * @return betweenness centrality
     */
    public int betweennessCentrality(String userID) {
        var userNode = nodes.get(userID);
        if (userNode == null) throw new NoSuchElementException("This node not be found!");
        if (!userNode.getType().equals(NodeType.user))
            throw new IllegalArgumentException("This node type is not user!");
        int counter = 0;
        for (var path : getShortestPathsForPairNodesOfConnectedGraph().values()) {
            if (path.contains(userNode)) counter++;
        }
        return counter;
    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Vertices: " + nodes.values().toString() + "\n\nAdjacencyList: " + adjacencyList.toString() + "\n\nFeatures: " + features.toString();
    }


    public String adjacencyListToString() {
        StringBuilder sb = new StringBuilder();
        for (var node : adjacencyList.keySet()) {
            sb.append(node.getId());
            sb.append(":[");
            for (var e : adjacencyList.get(node)) {
                sb.append(e.getDestination().getId()).append(", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    /**
     * Return true if common item exists in two given collections
     * @param c1 First collection
     * @param c2 Second collection
     * @return true if common item exists in two given collections
     */
    public static boolean hasCommonItem(Collection<?> c1, Collection<?> c2) {
        for (var item : c1) if (c2.contains(item)) return true;
        return false;
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    public HashMap<Node, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public HashMap<Node, List<Feature>> getFeatures() {
        return features;
    }
}
