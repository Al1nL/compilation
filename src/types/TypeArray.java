package types;

public class TypeArray extends Type {
    public Type baseType;

    public TypeArray(Type baseType) {
        this.baseType = baseType;
        this.name = baseType.name + "[]";
    }

    @Override
    public String toString() {
        return baseType + "[]";
    }

    public boolean isArray(){ return true;}

}