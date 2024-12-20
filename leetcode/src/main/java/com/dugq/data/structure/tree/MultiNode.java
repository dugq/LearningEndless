package com.dugq.data.structure.tree;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiNode {
    private int value;
    private MultiNode parent;
    private double weight;
    private List<MultiNode> children;
}
