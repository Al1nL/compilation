package types;

public class TypeClass extends Type
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TypeClass father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TypeList dataMembers;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeClass(TypeClass father, String name, TypeList dataMembers)
	{
		this.name = name;
		this.father = father;
		this.dataMembers = dataMembers;
	}
	
    // Find a field in this class or parent classes
    public Type findField(String fieldName)
    {
        // Search in this class's data members
        TypeList current = dataMembers;
        while (current != null)
        {
            if (current.head != null && current.head.name != null)
            {
                if (current.head.name.equals(fieldName))
                {
                    return current.head;
                }
            }
            current = current.tail;
        }
        
        // Search in parent class
        if (father != null)
        {
            return father.findField(fieldName);
        }
        
        return null;
    }
}

