package cn.mordernpoem.command;

import java.util.HashMap;
import java.util.Map;

class Node {
    private final char c;
    boolean endChar = false;
    private final Map<Character, Node> next = new HashMap<>();

    Node(char c) {
        this.c = c;
    }

    void add(char[] chars, int index) {
        char c = chars[index];
        Node n = this.next.getOrDefault(c, new Node(chars[index++]));
        if (index < chars.length) {
            n.add(chars, index);
        } else {
            n.endChar = true;
        }

        this.next.put(c, n);
    }

    Node get(char c) {
        return this.next.get(c);
    }

    @Override
    public String toString() {
        return this.endChar ? "T" : "F" + this.c;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && ((Node) obj).c == this.c;
    }
}
