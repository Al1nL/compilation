package types;

public class TypeArray extends Type {
    public Type baseType;

    public TypeArray(Type baseType, String name) {
        this.baseType = baseType;
        this.name = name;
    }
    public TypeArray(Type baseType) {
        this.baseType = baseType;
        this.name = baseType.toString() + "[]";
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isArray(){ return true;}

}