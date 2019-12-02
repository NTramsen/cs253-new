package edu.emory.cs.graph.span;

import edu.emory.cs.graph.Graph;
import edu.emory.cs.graph.path.AStar;
import edu.emory.cs.graph.path.Dijkstra;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MSTWordTramsen extends MSTWord{

    public MSTWordTramsen(InputStream in) {
        super(in);
    }

    @Override
    protected Graph createGraph() {
        Graph graph = new Graph(vertices.size());
        for(int i=0; i<vertices.size();i++){
            for(int j=0; j<vertices.size();j++){
                if(i!=j){
                    WVPair source = vertices.get(i);
                    WVPair target = vertices.get(j);
                    graph.setUndirectedEdge(i,j,cosineDistance(source, target));
                }
            }
        }
        return graph;
    }

    private double cosineDistance(WVPair source, WVPair target){
        double[] sV = source.getVector();
        double[] tV = target.getVector();
        double dot=0, A=0, B =0;
        for(int i = 0; i<Math.max(sV.length,tV.length);i++){
            dot+=(sV[i]*tV[i]);
            A+=(sV[i]*sV[i]);
            B+=(tV[i]*tV[i]);
        }
        A = Math.sqrt(A);
        B = Math.sqrt(B);
        return 1-(dot/(A*B));
    }

    @Override
    public SpanningTree getMinimumSpanningTree() {
        MSTAlgorithm engine = new MSTPrim();
        SpanningTree MST = engine.getMinimumSpanningTree(graph);
        return MST;
    }

    @Override
    public List<String> getShortestPath(int source, int target) {
        if(source<0||source>=vertices.size()||target<0||target>=vertices.size()){
            System.out.println("Vertex out of bounds.");
            return null;
        }
        AStar engine = new Dijkstra();
        List<String> result = new ArrayList<>();
        Integer[] path = engine.getShortestPath(graph, source, target);
        int vertex = source;
        while(vertex!=target){
            //System.out.println(vertex);
            result.add(vertices.get(vertex).getWord());
            vertex = path[vertex];
        }
        //System.out.println(vertex);
        result.add(vertices.get(target).getWord());
        return result;
    }

    static public void main(String[] args) throws Exception {
    final String INPUT_FILE = "src/main/resources/word_vectors.txt";
    final String OUTPUT_FILE = "src/main/resources/word_vectors.dot";

    MSTWord mst = new MSTWordTramsen(new FileInputStream(INPUT_FILE));
    SpanningTree tree = mst.getMinimumSpanningTree();
    mst.printSpanningTree(new FileOutputStream(OUTPUT_FILE), tree);
    System.out.println(tree.getTotalWeight());
    System.out.println(mst.getShortestPath(0,316));
    }
}
