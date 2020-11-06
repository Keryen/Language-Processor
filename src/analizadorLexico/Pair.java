package analizadorLexico;

public class Pair<X,Y> {

	private X left;
	private Y right;
	
	public Pair(X o1,Y o2){
		this.left=o1;
		this.right=o2;
	}
	
	public Pair(){
	}
	
	public void setLeft(X o1) {
		this.left = o1;
	}
	
	public void setRight(Y o1) {
		this.right = o1;
	}
	
	public X getLeft() {
		return this.left;
	}
	
	public Y getRight() {
		return this.right;
	}
}
