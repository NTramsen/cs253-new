package edu.emory.cs.trie.autocomplete;

import edu.emory.cs.trie.TrieNode;

import java.util.*;

public class AutocompleteTramsen2 extends Autocomplete<List<FrequentWord>>{

    public AutocompleteTramsen2(String dict_file, int max) {
        super(dict_file, max);
    }

    public AutocompleteTramsen2(String dict_file) {
        this(dict_file, 5);
    }

    @Override
    public List<String> getCandidates(String prefix) {

        prefix = prefix.trim();

        Queue<TrieNode> queue = new ArrayDeque<>();
        TrieNode node = find(prefix);
        List<FrequentWord> result;
        if(node==null){
            put(prefix,null); // what should we do if prefix is not found
            result = new ArrayList<FrequentWord>();
            node = find(prefix);
            node.setEndState(false);
        }
        else if(node.getValue()!=null) {
            result = (List<FrequentWord>)node.getValue();
        }
        else{
            result = new ArrayList<FrequentWord>();
        }
        Map children = node.getChildrenMap();

        for (Object key: children.keySet()) {
            queue.add(node.getChild((char)key));
        }

        while(!queue.isEmpty() && result.size()<getMax()){
            node = queue.poll();
            if(node.isEndState()) {
                if(checkRecurrence(result, node)) continue;
                result.add(new FrequentWord(getForm(node),1));
            }
            children = node.getChildrenMap();

            for (Object key: children.keySet()) {
                queue.add(node.getChild((char)key));
            }
        }
        node.setValue(result);

        List<String> candidates = new ArrayList<String>();
        for(FrequentWord w:result){
            candidates.add(w.getWord());
        }

        return candidates;
    }

    private String getForm(TrieNode node){
        String result = "";
        if(node==null){
            return result;
        }
        while(node.getParent()!=null){
            result = ""+node.getKey()+result;
            node = node.getParent();
        }
        return result;

    }

    private boolean checkRecurrence(List<FrequentWord> list, TrieNode node){
        for(FrequentWord word:list){
            if(word.getWord().equals(getForm(node))) return true;
        }
        return false;
    }

    @Override
    public void pickCandidate(String prefix, String candidate) {

        prefix = prefix.trim();

        TrieNode node = find(prefix);
        if(node==null){
            List<FrequentWord> newList = new ArrayList<>();
            FrequentWord w = new FrequentWord(candidate,1);
            newList.add(0,w);
            put(prefix, newList);
            node = find(prefix);
            node.setEndState(false);
        }
        else if(node.getValue()==null){
            List<FrequentWord> newList = new ArrayList<>();
            FrequentWord w = new FrequentWord(candidate,1);
            newList.add(0,w);
            node.setValue(newList);
        }
        else{
            List<FrequentWord> list = (ArrayList<FrequentWord>)node.getValue();
            int frequency = 1;
            for(int i=0; i<list.size();i++){
                if(list.get(i).getWord().equals(candidate)){
                    frequency = list.get(i).getFrequency()+1;
                    list.remove(i);
                    break;
                }
            }
            list.add(0,new FrequentWord(candidate, frequency));
            if(list.size()>1) sink(list);
            if(list.size()>getMax()){
                list.remove(list.size()-1);
            }
        }
    }

    private static void sink(List<FrequentWord> list){
        int i=0;
        while(list.get(i).getFrequency()<list.get(i+1).getFrequency()&&i<list.size()-1){
            FrequentWord temp = list.remove(i);
            list.add(i, list.remove(i));
            list.add(i+1,temp);
            i++;
        }
    }
}

class FrequentWord implements Comparable<FrequentWord>{
    private String word;
    private int frequency;

    public FrequentWord(String word, int frequency){
        this.word = word;
        this.frequency = frequency;
    }

    public void setFrequency(int frequency){
        this.frequency = frequency;
    }
    public void setWord(String word){
        this.word = word;
    }
    public int getFrequency(){
        return frequency;
    }
    public String getWord(){
        return word;
    }

    public int compareTo(FrequentWord word){
        if(word.frequency<this.frequency) return 1;
        if(word.frequency>this.frequency) return -1;
        else return 0;
    }
}
