package com.mystical.basicessence;

/**
 * @Description:
 * @author: Administrator
 * @Date: 2021/9/14
 */
public class SkipTable<k, v> {
    Node<k, v> headNode;
    Node<k, v> tailNode;
    int maxLevel;
    int len;
    Node[] lines = new Node[32];


    public boolean insert(k key, v value) {
        Node<k, v> node = new Node<>(key, value);

        boolean result = false;
        int level = 1;
        node.level = level;
        len++;
        if (headNode == null) {
            headNode = node;
            tailNode = node;
            result = true;
            lines[0] = node;
        } else {
            node.preNode = tailNode;
            tailNode.nextNode = node;
            tailNode = node;
            Node<k, v> nodeRise;
            Node<k, v> nodeRiseTmp = node;
            while (randomLevel() >= 0.5) {
                nodeRise = new Node<>(key, value);
                nodeRise.level = ++level;
                nodeRise.bottomNode = nodeRiseTmp;
                Node firstRiseNode = lines[++maxLevel];
                if (firstRiseNode == null) {
                    lines[maxLevel] = nodeRise;
                } else {
                    Node nextLineNode = firstRiseNode.nextNode;
                    while (nextLineNode != null) {
                        nextLineNode = nextLineNode.nextNode;
                    }
                    nextLineNode.nextNode = nodeRise;
                    nodeRise.preNode = nextLineNode;
                }
                nodeRiseTmp = nodeRise;
            }


        }
        return result;
    }

    public boolean del(k key) {
        return false;
    }

    public Object get(int key) {
        Node resultNode = getNode(key);
        return resultNode.value;
    }

    private Node getNode(int key) {
        int maxLevel = this.maxLevel;
        Node resultNode = null;
        Node nextNode = lines[maxLevel--];
        while (nextNode != null) {
            if (key > (double) nextNode.key) {
                if (nextNode.nextNode == null) {
                    if (nextNode.bottomNode == null) {
                        resultNode = nextNode;
                    }
                    nextNode = nextNode.bottomNode;
                } else {
                    nextNode = nextNode.nextNode;
                }
            } else if (key == (double) nextNode.key) {
                Node bottomNode = nextNode.bottomNode;
                while (bottomNode != null) {
                    if (bottomNode.bottomNode == null) {
                        resultNode = bottomNode;
                    }
                    bottomNode = bottomNode.bottomNode;
                }
            } else {
                if (nextNode.bottomNode == null) {
                    resultNode = nextNode;
                }
                nextNode = nextNode.bottomNode;
            }
        }
        return resultNode;
    }

    private double randomLevel() {

        return 0;
    }

    public static void main(String[] args) {
        SkipTable<Integer, String> stringSkipTable = new SkipTable<>();
        stringSkipTable.insert(1, "1");
        stringSkipTable.insert(2, "2");
        stringSkipTable.insert(3, "3");
        stringSkipTable.insert(4, "4");
        stringSkipTable.insert(5, "5");
    }

}

class Node<k, v> {
    Node<k, v> nextNode;
    Node<k, v> preNode;
    k key;
    v value;
    Node<k, v> bottomNode;

    int level;

    public Node(k key, v value) {
        this.key = key;
        this.value = value;
    }


}
