import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Tree23h{
	
	public static void main(String[] args) {
		Tree23<String, Integer> st = new Tree23<>();
		Scanner sc = new Scanner(System.in);	
		System.out.print("입력 파일 이름? ");
		String fname = sc.nextLine();	// 파일 이름을 입력
		System.out.print("난수 생성을 위한 seed 값? ");
		Random rand = new Random(sc.nextLong());
		sc.close();
		try {
			sc = new Scanner(new File(fname));
			long start = System.currentTimeMillis();
			while (sc.hasNext()) {
				String word = sc.next();
				if (!st.contains(word))
					st.put(word, 1);
				else	st.put(word, st.get(word) + 1);
			}
			long end = System.currentTimeMillis();
			System.out.println("입력 완료: 소요 시간 = " + (end-start) + "ms");
			
			System.out.println("### 생성 시점의 트리 정보");
			print_tree(st);		// 정상적으로 출력되면 50점
			
			ArrayList<String> keyList = (ArrayList<String>) st.keys();
			Collections.shuffle(keyList, rand);
			int loopCount = (int)(keyList.size() * 0.95);
			for (int i = 0; i < loopCount; i++) {
				st.delete(keyList.get(i));						// 주석 처리 가능
			}
			System.out.println("\n### 키 삭제 후 트리 정보");			// 주석 처리 가능
			print_tree(st);										// 주석 처리 가능. 여기까지 정상적으로 출력되면 100점
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		if (sc != null)
			sc.close();
	}
	
	private static void print_tree(Tree23<String, Integer> st) {
		System.out.println("등록된 단어 수 = " + st.size());		
		System.out.println("트리의 깊이 = " + st.depth());		
		
		String maxKey = "";
		int maxValue = 0;
		for (String word : st.keys())
			if (st.get(word) > maxValue) {
				maxValue = st.get(word);
				maxKey = word;
			}
		System.out.println("가장 빈번히 나타난 단어와 빈도수: " + maxKey + " " + maxValue);
	}
}

//여기서 부터 Tree23 클래스를 정의하는 프로그램 추가할 것!
class Tree23<K extends Comparable<K>, V> {
	Node root = null;
	int size = 0;
	
	class Node {
		partialNode<K,V> leftNode;
		partialNode<K,V> rightNode;
		Node parent, left, middle, right;
		
		Node(partialNode<K,V> leftNode){
			this.leftNode = leftNode;
			parent = left = middle = right = null;
		}

		Node(partialNode<K,V> leftNode, partialNode<K,V> rightNode){
			this.leftNode = leftNode;
			this.rightNode = rightNode;
			parent = left = middle = right = null;
		}
	}
	
	class partialNode<K,V> {
		K key;
		V value;
		
		partialNode(K key, V val){
			this.key = key;
			this.value = val;
		}
	}
	
	int size() { return size;}
	
	Node treeSearch(K key){
		Node x = root;
		while(true) {
			int cmp = key.compareTo(x.leftNode.key);
			if(cmp==0) return x;
			else if(cmp<0) {
				if(x.left == null) return x;
				else x = x.left;
			}
			else if(cmp>0 && x.rightNode == null) {
				if(x.middle == null) return x;
				else x = x.middle;
			}
			else {
				cmp = key.compareTo(x.rightNode.key);
				if(cmp==0) return x;
				else if(cmp<0) {
					if(x.middle == null) return x;
					else x = x.middle;
				}
				else {
					if(x.right == null) return x;
					else x = x.right;
				}
			}
		}
	}
	
	V get(K key) {
		if(root == null) return null;
		Node x = treeSearch(key);
		if(key.equals(x.leftNode.key)) return x.leftNode.value;
		if(x.rightNode != null) if(key.equals(x.rightNode.key)) return x.rightNode.value;
		return null;
	}
	
	void put(K key, V val) {
		if(root == null) {
			root = new Node(new partialNode<K,V>(key, val));
			size++;
			return;
		}
		Node x = treeSearch(key);
		partialNode<K,V> tmp = new partialNode<K,V>(key, val);
		if(key.equals(x.leftNode.key)) {x.leftNode.value = val; return;}
		if(x.rightNode == null) {
			int cmp = key.compareTo(x.leftNode.key);
			if(cmp < 0) {
				x.rightNode = x.leftNode;
				x.leftNode = tmp;
			}
			else if(cmp > 0) x.rightNode = tmp;
		}
		else {
			if(key.equals(x.rightNode.key)) {x.rightNode.value = val; return; }
			else split(x, null, null, tmp); 
		}
		size ++;
		return;
	}
	
	void split(Node x, Node t1, Node t2, partialNode<K,V> tmp) {
		partialNode<K,V> midKV;
		Node n1 = null;
		if (x.leftNode.key.compareTo(tmp.key) > 0) {
			n1 = new Node(tmp);
			midKV = x.leftNode; x.leftNode = x.rightNode; x.rightNode = null;
			x.left = x.middle; x.middle = x.right;
			n1.parent = x.parent;

			if(t1 != null && t2 != null) {t1.parent = n1; t2.parent = n1;}
			n1.left = t1; n1.middle = t2; 
		}
		else if(x.rightNode.key.compareTo(tmp.key) < 0) {
			n1 = new Node(x.leftNode);
			midKV = x.rightNode; x.rightNode = null; x.leftNode = tmp;
			n1.left = x.left; n1.middle = x.middle; x.right = null;
			if(n1.left != null) {n1.left.parent = n1; n1.middle.parent = n1;}
			n1.parent = x.parent;
			
			if(t1 != null && t2 != null) {t1.parent = x; t2.parent = x;}
			x.left = t1; x.middle = t2; 
		}
		else {
			n1 = new Node(x.leftNode);
			midKV = tmp; x.leftNode = x.rightNode; x.rightNode = null;
			n1.left = x.left; x.middle = x.right; x.right = null;
			if(n1.left != null) {n1.left.parent = n1;}
			n1.parent = x.parent;
			
			if(t1 != null && t2 != null) {t1.parent = n1; t2.parent = x;}
			n1.middle = t1; x.left = t2;
		}
		
		if(x.parent == null) {
			root = new Node(midKV);
			root.left = n1; root.middle = x;
			n1.parent = root; x.parent = root;
		}
		else if(x.parent.rightNode == null) {
			if(x.parent.leftNode.key.compareTo(midKV.key) < 0) {
				x.parent.rightNode = midKV;
				x.parent.middle = n1; x.parent.right = x;
			}
			else {
				x.parent.rightNode = x.parent.leftNode;
				x.parent.leftNode = midKV;
				x.parent.left = n1;	x.parent.right = x.parent.middle; x.parent.middle = x;
			}
		}
		else split(x.parent, n1, x, midKV);
		
		return;
	}
	
	boolean contains(K key) {
		if(get(key) != null) return true;
		else return false;
	}
	
	boolean isEmpty() {
		if(root == null) return true;
		else return false;
	}
	
	int depth() {
		Node x = root;
		if(root == null) return 0;
		else {
			int depth = 1;
			while(x.left != null) {
				x = x.left;
				depth++;
			}
			return depth;
		}
	}
	Iterable<K> keys(){
		if(root == null) return null;
		ArrayList<K> keyList = new ArrayList<K>(size());
		inorder(root, keyList);
		return keyList;
	}
	
	void inorder(Node x, ArrayList<K> keyList) {
		if(x!=null) {
			inorder(x.left, keyList);
			keyList.add(x.leftNode.key);
			inorder(x.middle, keyList);
			if(x.rightNode != null) {
				keyList.add(x.rightNode.key);
				inorder(x.right, keyList);
			}
		}
	}
}
