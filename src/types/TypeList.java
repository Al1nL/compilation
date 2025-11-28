package types;

public class TypeList extends Type
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public Type head;
	public TypeList tail;
	public int len=0;
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TypeList(Type head, TypeList tail)
	{
		this.head = head;
		this.tail = tail;
		this.len=tail!=null?tail.len+1:1;
	}
}
