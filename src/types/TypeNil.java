package types;

public class TypeNil extends Type
{
    public TypeNil()
    {
        this.name = "nil"; // optional, mainly for debugging
    }

    @Override
    public boolean isClass() { return false; }

    @Override
    public boolean isArray() { return false; }

    @Override
    public boolean isSameType(Type other)
    {
        // nil can be assigned to any class or array type
        return (other instanceof TypeClass || other instanceof TypeArray || other instanceof TypeNil);
    }

    @Override
    public boolean canAssignTo(Type target)
    {
        // nil can be assigned to any class or array
        return (target instanceof TypeClass || target instanceof TypeArray || target instanceof TypeNil);
    }

    @Override
    public String toString()
    {
        return "nil";
    }
}
