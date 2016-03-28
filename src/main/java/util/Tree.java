package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gguliash on 3/26/16.
 */
public class Tree<T> {
    private List<Tree<T>> children;
    private T value;

    public T getValue() {
        return value;
    }

    public List<Tree<T>> getChildren() {
        if(children == null) return Collections.emptyList();
        return children;
    }

    public Tree(T value) {
        this.value = value;
    }
    public void addChild(Tree<T> child){
        if(children == null) {
            children = new ArrayList<>(1);
        }
        children.add(child);
    }
}
