import java.util.Arrays;

/**
 * FibonacciHeap
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode first;
	private HeapNode last;
	private HeapNode min;
	private int numofNodes;
	private int numofTrees;
	private int numofMark;
	public static int numofLink;
	public static int numofCuts;

   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * complexity O(1)
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	if (numofNodes==0)
    		return true;
    	return false; 
    }
		
   /**
    * public HeapNode insert(int key)
    * complexity O(1)
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode node= new HeapNode(key);
    	if (this.isEmpty())
    	{
    		this.first = node;
    		this.min = node;
    		this.last = node;
    		node.prev = node;
    		node.next = node;
    		numofNodes++;
        	numofTrees++;
    		return node;
    	}
    	numofNodes++;
    	numofTrees++;
    	node.next = first;
    	this.first = node;
    	node.next.prev = node;
    	node.prev = last;
    	this.last.next = node;
    	if (key < min.key)
    	{	
    		this.min = node;
    	}
    	return node;
    }

   /**
    * public void deleteMin()
    * complexity O(log n) (amortised)
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	if (this.numofNodes == 1)
    	{
    		this.numofNodes--;
    		this.numofTrees--;
    		this.first =null;
    		this.last = null;
    		this.min = null;
    		return;    		
    	}
    	
    	if (!this.isEmpty())
    	{
    		this.numofNodes--; // decrease the number of nodes by 1
    		HeapNode minprev=this.min.prev;
    		HeapNode minnext=this.min.next;
    		if (this.min.child == null)//no children
    		{
    			if (this.min==this.first)//if the min node is the first node
    			{
    				this.first = minnext;
    			} 
    			if (this.min == this.last) // if the min is the last prev
    			{
    				this.last = minprev;
    			}
    			minprev.next = minnext;
    			minnext.prev = minprev;
    		}
    		else //has children
    		{
    			if (this.min == this.first)
    			{
    				this.first = this.min.child;
    			}
    			if (this.min == this.last)
    			{
    				this.last = minprev;
    			}
    			if (this.min.next != this.min) // the min tree is not the only tree
    			{
        			minprev.next=this.min.child;
        			this.min.child.prev.next = minnext;
        			minnext.prev = this.min.child.prev;
        			this.min.child.prev=minprev;
    			}
    			else // the min is the only tree
    			{
    				this.last = this.first.prev;
    				this.last.next = this.first;
    			}
    			HeapNode curr=this.min.child;
    			while(curr.next!=this.min.child) // update the childrens parent to null
    			{
    				curr.parent=null;
    				if (curr.mark!=0)
    				{
    					this.numofMark--;
    					curr.mark=0;
    				}
    				curr=curr.next;
    			}
    			curr.parent=null;
    			if (curr.mark!=0)
    			{
    				this.numofMark--;
    				curr.mark=0;
    			}
    		}
    		Consolidate(this.first);
    	}
    }
    /**
     * public void Consolidate()
     * complexity O(1)
     * link the trees to make sure there is log n trees (n is the number of nodes)
     *
     */
    private void Consolidate(HeapNode x)
    {
    	fromBuckets(toBuckets(x));
    }
    
    /**
     * public void toBuckets()
     * complexity O(log n) (amortised)
     * entering the sub trees in to buckets by linking them
     *
     */
    public HeapNode[] toBuckets(HeapNode x)
    {
    	int numoftrees = (int)(Math.log(numofNodes)/Math.log(2));
    	HeapNode[] arr=new HeapNode[numoftrees+1];
    	
    	x.prev.next=null;
    	while(x!=null)
    	{
    		HeapNode y=x;
    		x=x.next;
    		while(arr[y.rank]!=null)
    		{
    			y=link(y,arr[y.rank]);
    			arr[y.rank-1]=null;
    		}  		
    	arr[y.rank]=y;
    	}
    	return arr;
    }
    
    /**
     * public void fromBuckets()
     * complexity O(log n)
     * melding all the sub trees in to one heap
     *
     */ 
    public void fromBuckets(HeapNode[] B){
    	HeapNode x = null;
    	for (int i = 0; i < B.length;i++)
    	{
    		if(B[i]!=null)
    		{
    			if(x==null)
    			{
    				x=B[i];
    				this.first = x;
    				this.last=x;
    				this.min=x;
    				x.next=x;
    				x.prev=x;
    				this.numofTrees=1;
    			}
    			else
    			{
    				this.numofTrees++;
    				insertAfter(x,B[i]);
    				x=B[i];
    				if(B[i].key<this.min.key)
    				{
    					this.min=B[i];
    				}	
    					
    			}
    		}
    	}
    }
    
    /**
     * public void insertAfter()
     * complexity O(1)
     * linking a given tree y after another tree x
     *
     */
    public void insertAfter(HeapNode x,HeapNode y)
    {
    	x.next = y;
    	y.prev = x;
    	this.last = y;
    	this.first.prev = this.last;
    	y.next=this.first;
    }
    
    /**
     * public void link()
     * complexity O(1)
     * linking two binomial trees together
     *
     */
    public HeapNode link(HeapNode x,HeapNode y)
    {    	
    	numofLink++;
    	if (x.key > y.key)
    	{
    		HeapNode tmp = x;
    		x = y;
    		y = tmp;
    	}
    	x.rank++;
    	if (x.child == null)
    	{
    		x.child = y;
    		y.parent = x;
    		y.next = y;
    		y.prev = y;
    	}
    	else 
    	{
    		HeapNode tmpchild=x.child;
    		x.child = y;
    		y.parent = x;
    		y.next = tmpchild;
        	y.prev = tmpchild.prev;
        	tmpchild.prev = y;
        	y.prev.next = y;	
    	}
    	return x;
    	
    }
    
   /**
    * public HeapNode findMin()
    * complexity O(1)
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	if (this.isEmpty())
    		return null;
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    * complexity O(1)
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	if(this.isEmpty()&& heap2.isEmpty())
    	{
    		return;
    	}
    	if(!this.isEmpty()&& heap2.isEmpty())
    	{
    		return;
    	}
    	if(this.isEmpty()&& !heap2.isEmpty())
    	{
    		this.first = heap2.first;
    		this.last=heap2.last;
    		this.min=heap2.min;
    	}
    	else
    	{
    		heap2.first.prev = this.last;
    		this.last.next = heap2.first;
    		this.last = heap2.last;
    		this.first.prev = heap2.last;
    		this.last.next = first;
    		if(heap2.min.key < this.min.key)
    		{
    			this.min=heap2.min;
    		}
    	}
    	this.numofNodes+=heap2.numofNodes; // update the numbers of nodes
    	this.numofTrees+=heap2.numofTrees;// update the numbers of trees
    	this.numofMark+=heap2.numofMark;// update the numbers of marks nodes
    	
    }

   /**
    * public int size()
    * complexity O(1)
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.numofNodes;
    }
    	
    /**
    * public int[] countersRep()
    * complexity O(n)
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
	int[] arr = new int[this.numofNodes]; // the maximum rank will be the number of nodes
	HeapNode curr = this.first;
	while(curr.next  != this.first)
	{
		arr[curr.rank]++;
		curr=curr.next;
	}
	arr[curr.rank]++;
	return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    * complexity O (log n) (amortized)
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	double inf= Double.NEGATIVE_INFINITY;
    	this.decreaseKey(x, (int)inf);
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    * O(1) (amortized)
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.key-=delta;
    	if (x.key<this.min.key)
    	{
    		this.min=x; 
    	}
    	if (x.parent==null)
    		return;
    	if (x.parent.key<x.key)
    		return;
    	cascadingCut(x,x.parent);
      	
    }
    
    /**
     * public void cascadingCut
     * O(1) (amortised)
     * cut the child from his parent and cut the parent if his marked
     */
    private void cascadingCut(HeapNode child,HeapNode parent)
    {
    	cut(child,parent);
    	if (parent.parent!=null)
    	{
    		if (parent.mark==0)
    		{
    			parent.mark=1;
    			this.numofMark++;
    		}
    		else
    		{
    		cascadingCut(parent,parent.parent);
    		}
    	}
    }
    /**
     * public void cut
     * O(1)
     * cut the node from his parent and link him as the first sub tree of the heap
     */
    private void cut(HeapNode child,HeapNode parent)
    {
    	child.parent=null;
    	if (child.mark==1)
    	{
    		this.numofMark--;
    	}
    	child.mark=0;
    	this.numofTrees++;
    	numofCuts++;
    	parent.rank--;
    	if (child.next==child) // if the child is the only child
    	{
    		parent.child=null;
    	}
    	else
    	{
    		if (parent.child==child)
    		{
    		parent.child=child.next;
    		child.prev.next=child.next;
    		child.next.prev=child.prev;
    		}
    		else
    		{
    			if (child.next == parent.child) // if the child is the last child
    			{
    				child.prev.next = child.next;
    				parent.child.prev = child.prev;
    			}
    			else // if the child is in the middle
    			{
    				child.prev.next = child.next;
    				child.next.prev = child.prev;
    			}
    		}
    	}
    	this.first.prev.next = child;
    	child.prev = this.first.prev;
    	this.first.prev = child;
    	child.next = this.first;
    	this.first = child;
    }	
    
   /**
    * public int potential() 
    * complexity O(1)
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.numofTrees+2*this.numofMark;
    }

   /**
    * public static int totalLinks() 
    *complexity O(1)
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return numofLink;
    }

   /**
    * public static int totalCuts() 
    * complexity O(1)
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return numofCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    * complexity (k(log k + degH))
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)). 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
    	int[] arr = new int[k];
    	if (H.numofNodes == 0)
    	{
    		return arr;
    	}
    	FibonacciHeap heap= new FibonacciHeap();
    	
    	heap.insert(H.findMin().key); // insert the min of H to the help heap
    	
		HeapNode Hmin = heap.findMin(); // define a new heap as the min of the help heap
    	Hmin.nodePointer = H.findMin(); // define the node pointer of the help heap to the elem in H
    	
    	for (int i=0;i<k;i++)
    	{
    		arr[i]= Hmin.key; // insert the min to the array
    		
    		HeapNode Hchild = Hmin.nodePointer.child; // get the child from the H 
    		
    		HeapNode curr = Hchild;
    		if (Hchild != null)
    		{
    		while (curr.next != Hchild)
    		{
    			heap.insert(curr.key);
    			heap.first.nodePointer = curr;	
    			curr=curr.next;
    		}
    		heap.insert(curr.key);
    		heap.first.nodePointer = curr;
    		}
    		heap.deleteMin();
    		Hmin = heap.findMin();
    	}
        return arr; 
    }


/**
    * public class HeapNode
    *  
    */
    public class HeapNode
    {

    private int key;
	private HeapNode next;
	private HeapNode prev; 
	private HeapNode parent;
	private HeapNode child;
	private HeapNode nodePointer;
	public int rank;
	public int mark;
  	
	public HeapNode(int key)
	{
	    this.key = key;
	    this.child=null;
      }

  	public int getKey()
  	{
	    return this.key;
    }

	public void setPrev(HeapNode cur)
	{
		this.prev = cur;	
	}

	public void setNext(HeapNode cur)
	{
		this.next = cur;
		
	}

	public HeapNode getPrev()
	{
		 return this.prev;
	}
	public HeapNode getNext()
	{
		 return this.next;
	}

	public HeapNode getParent()
	{
		return this.parent;
	}
	public void setParent(HeapNode par)
	{
		this.parent = par;
	}

}
}
