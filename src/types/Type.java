package types;

public abstract class Type {

    /**
     * ***************************
     */
    /*  Every type has a name ... */
    /**
     * ***************************
     */
    public String name;

    /**
     * **********
     */
    /* isClass() */
    /**
     * **********
     */
    public boolean isClass() {
        return false;
    }

    /**
     * **********
     */
    /* isArray() */
    /**
     * **********
     */
    public boolean isArray() {
        return false;
    }

    public boolean isSameType(Type other) {
        if (other == null) {
            return false;
        }

        // Handle nil - nil can be assigned to any class or array type
        if (other instanceof TypeNil) {
            return (this instanceof TypeClass || this instanceof TypeArray);
        }
        if (this instanceof TypeNil) {
            return (other instanceof TypeClass || other instanceof TypeArray);
        }

        // Same primitive types
        if (this instanceof TypeInt && other instanceof TypeInt) {
            return true;
        }
        if (this instanceof TypeString && other instanceof TypeString) {
            return true;
        }
        if (this instanceof TypeVoid && other instanceof TypeVoid) {
            return true;
        }

        // Same named types (classes/arrays)
        if (this.name != null && other.name != null) {
            return this.name.equals(other.name);
        }

        return false;
    }

    public boolean canAssignTo(Type target) {
        // Same type can always be assigned
        if (isSameType(target)) {
            return true;
        }

        // Nil can be assigned to any class or array
        if (this instanceof TypeNil && (target instanceof TypeClass || target instanceof TypeArray)) {
            return true;
        }

         if (this instanceof TypeArray && target instanceof TypeArray) {
            return false;
        }

        // Check class inheritance
        if (this instanceof TypeClass && target instanceof TypeClass) {
            TypeClass thisClass = (TypeClass) this; // type of expression e (e.g., Son)
            TypeClass targetClass = (TypeClass) target; // type of variable x (e.g., Father)

            // Check if thisClass is a subclass of targetClass
            TypeClass current = thisClass;
            while (current != null) {
                if (current.name.equals(targetClass.name)) {
                    return true;
                }
                current = current.father;
            }
        }

        return false;
    }
}
