package types;

public class TypeFunction extends Type
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public Type returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TypeList params;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeFunction(Type returnType, String name, TypeList params)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
	}

	public boolean compareFunctions(TypeFunction other){
		// return type must match exactly
		if (!this.returnType.equals(other.returnType)){
			return false;
		}
			

		// params must match exactly
		if (this.params.len != other.params.len)
		{
			return false;
		}

		TypeList p1 = this.params;
		TypeList p2 = other.params;
		while (p1 != null && p2 != null) {

			if (!p1.head.equals(p2.head)) {
				return false;
			}

			p1 = p1.tail;
			p2 = p2.tail;
		}
			
		return true;
	}
}
