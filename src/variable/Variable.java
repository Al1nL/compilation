package variable;

import java.util.*;

public class Variable implements Comparable<Variable> {

    public String name;
    public final int scope;
    public boolean isGlobal = false;
    public int offset;
    private static final Map<String, Variable> pool = new HashMap<>();

    public Variable(String name, int scope) {
        this.name = name;
        this.scope = scope;
        isGlobal = (scope == 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) o;
        return scope == other.scope && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + scope;
    }

    @Override
    public int compareTo(Variable other) {
        int cmp = this.name.compareTo(other.name);
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.scope, other.scope);
    }

    public static Variable get(String name, int scope) {
        String key = name + "#" + scope;
        return pool.computeIfAbsent(key, k -> new Variable(name, scope));
    }

// New overload that includes function context
    public static Variable get(String name, int scope, String functionName) {
        String key = name + "#" + scope + "#" + functionName;
        return pool.computeIfAbsent(key, k -> new Variable(name, scope));
    }

}
