package index;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Index implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String FileName = "index.ser";
    private static Index instance;
    private final static ReadWriteLock lock = new ReentrantReadWriteLock();
    private ArrayList<SearchResult> searchResults;
    private Node root;
    private static final int minQueryLength = 4;
    private static final int maxQueryLength = 8; //8 chars max
    private static final int maxFuzzyDistance = 1; //8 chars max

    public static Index getInstance() {
        return instance;
    }

    public static boolean LoadIndexFromFile() {
    try{
        instance = (Index) utils.FileUtils.LoadFromSerializedFile(FileName);
        return true;
        } catch (FileNotFoundException _) {
            CreateNewIndex();
            return false;
        }
    }

    public static void CreateNewIndex() {
        instance = new Index();
        instance.searchResults = new ArrayList<>();
        instance.root = new Node();
        instance.root.resultIndices = new HashSet<>();
        instance.root.nodes = new HashMap<>();
    }

    //wait till other tasks?
    public static void StoreIndexInFile() {
        utils.FileUtils.StoreSerializedInFile(FileName, instance);
    }

    public static void indexData(SearchResult searchResult) {
        lock.writeLock().lock();
        System.out.println("locked write lock");
        try {
            String text = searchResult.TextDescription;
//            System.out.println("TEXT");
//            System.out.println(text);
            char[] chars = text.toLowerCase().toCharArray();
            instance.searchResults.add(searchResult);
            int searchResultIndex = instance.searchResults.size() - 1;
            searchResult.id = searchResultIndex;
            int startIndex = 0;
            for (int currentIndex = startIndex; currentIndex < chars.length - 4; currentIndex++) {
                IndexData(chars, currentIndex, searchResultIndex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error indexing", e);
        } finally {
            System.out.println("unlocked write lock");
            lock.writeLock().unlock();
        }
    }

    private static void IndexData(char[] chars, int currentIndex, int searchResultIndex) {
        if (currentIndex >= chars.length) {
            return;
        }
        Node node = instance.root;
        for (int i = currentIndex; i < chars.length && i < currentIndex+maxQueryLength; i++) {
            int c = chars[i];
            //Search was becoming too slow. Hence, not accommodating characters beyond 128.
            if (c > 128) {
                c = 128;
            }
            if (node.nodes.get(c) == null) {
                node.nodes.put(c, new Node());
                node = node.nodes.get(c);
                node.resultIndices = new HashSet<>();
                node.nodes = new HashMap<>();
            } else {
                node = node.nodes.get(c);
            }
            node.resultIndices.add(searchResultIndex);
        }

        IndexData(chars, currentIndex+1, searchResultIndex);
    }

    public static ArrayList<SearchResult> search(String query) {
        lock.readLock().lock();
        try {
            if (query.length() < minQueryLength || query.length() > maxQueryLength) {
                return new ArrayList<>();
            }
            Map<Node, Integer> nodeMap = new HashMap<>();
            nodeMap.put(instance.root, 0);
            Iterator<Integer> iterator = query.chars().iterator();
            return Search(nodeMap, iterator); //max fuzzy distance = 1
        } finally {
            lock.readLock().unlock();
        }
    }

    //todo: max 100 results?
    private static ArrayList<SearchResult> Search(Map<Node, Integer> nodeMap, Iterator<Integer> iterator) {
        if (iterator.hasNext()) {
            int c = iterator.next();
            Node[] keys = nodeMap.keySet().toArray(new Node[0]);
            for (Node prevNode : keys) {
                int v = nodeMap.remove(prevNode);
                Node node = prevNode.nodes.get(c);
                if (node != null) {
                    nodeMap.put(node, v);
                }

                prevNode.nodes.forEach((i, nextNode) -> {
                    if (nextNode != null && i != c) {
                        if (nodeMap.getOrDefault(nextNode, v) < maxFuzzyDistance) {
                            nodeMap.put(nextNode, nodeMap.getOrDefault(nextNode, v) + 1);
                        }
                    }
                });
            }
            return Search(nodeMap, iterator);
        } else {
            ArrayList<SearchResult> currentResults = new ArrayList<>();
            nodeMap.forEach((node, _) -> {
                for (int resultIndex : node.resultIndices) {
                    currentResults.add(instance.searchResults.get(resultIndex));
                }
            });
            return currentResults;
        }
    }
}
