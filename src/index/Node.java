package index;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Node implements Serializable {
    Map<Integer, Node> nodes;
    Set<Integer> resultIndices;
//    int []charIndexInResult; //todo if required
}
