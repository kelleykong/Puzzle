import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JTextArea;


public class SearchTree {
	private State curState;
	private int hNum;
	private PriorityQueue<State> openTable = new PriorityQueue<State>();
	private Vector<State> closeTable = new Vector<State>();
	private Stack<State> solution = new Stack<State>();
	private int nodeNum;
	private int stepNum;
	
	public SearchTree() {
		curState = null;
	}
	
	private int h1(int[] ord) {//有几个图片不在该在位置
		int count = 0;
		for(int i = 0; i < 9; i++) {
			if (ord[i] != i+1 && ord[i] != 9)
				count++;
		}
		return count;
	}
	
	private int h2(int[] ord) {//每个图片与目标位置距离总和
		int count = 0;
		int i, j;
		for(i = 0; i < 9; i++) {
			if (ord[i] != i+1 && ord[i] != 9) {
				for(j = 0; j < 9; j++) 
					if (ord[j] == i+1)
						break;
				int t1, t2;
				if (i == 8) {
					t1 = Math.abs(1 - j/4);
					t2 = Math.abs(4 - j%4);
				}
				else if (j == 8) {
					t1 = Math.abs(i/4 - 1);
					t2 = Math.abs(i%4 - 4);
				}
				else {
					t1 = Math.abs(i/4 - j/4);
					t2 = Math.abs(i%4 - j%4);
				}
				count += t1 + t2;
			}
		}
		return count;
	}
	
	private State isInOpen(int[] ord) {
		Iterator<State> it = openTable.iterator();
		while(it.hasNext()) {
//			System.out.println("open iterator" + openTable.size());
			int i;
			State s = it.next();
			for(i = 0; i < 9; i++) {
				if (ord[i] != s.order[i]) 
					break;
			}
			if (i == 9) 
				return s;
		}
		return null;
	}
	
	private State isInClose(int[] ord) {
		for(int i = 0; i < closeTable.size(); i++) {
			State state = closeTable.get(i);
			int j;
			for(j = 0; j < 9; j++) {
				if (ord[j] != state.order[j])
					break;
			}
			if (j == 9)
				return state;
		}
//		System.out.println("isInClose:state is not in close");
		return null;
	}
	
	public boolean solute(int[] ord, int hNum, JTextArea jtaOut) {
		openTable.clear();
		closeTable.clear();
		solution.clear();
		this.hNum = hNum;
		
		int h;
		if (hNum == 0)
			h = h1(ord);
		else
			h = h2(ord);
		curState = new State(ord, 0, h, 0, 0, null);//初始节点
		openTable.add(curState);
		
		while(!openTable.isEmpty()) {
			curState = openTable.poll();
			closeTable.add(curState);
			if (curState.isTarget()) {//成功
				nodeNum = openTable.size()+closeTable.size();
				System.out.println("totally " + nodeNum + "nodes");
				stepNum = 0;
				while(curState.parent != null) {
					solution.push(curState);
					curState = curState.parent;
					stepNum++;
				}
				jtaOut.setText("Totally " + nodeNum + "nodes.\n" + "Totally " + stepNum + "steps.\n");
				return true;
			}
			for(int i = 0; i < 9; i++) {//扩展
				System.out.println(i + " " + curState.order[i]);
				if (curState.order[i] == 9) {
					System.out.println("blank9 in " + i);
					for(int j = 1; j < 5; j++)
						operate(i, j);
				}
			}
		}
		System.out.println("Failed!!!"+openTable.size()+closeTable.size());
		return false;
	}
	
	private boolean operate(int position, int op) {//左，右，上，下移
		int[] order1 = new int[9];
		for(int i = 0; i < 9; i++)
			order1[i] = curState.order[i];		
		switch(op) {
		case 1: {//左
			if (position == 3 || position == 8)
				return false;
			order1[position] = order1[position+1];
			order1[position+1] = 9; 
			break;
		}
		case 2: {//右
			if (position == 0 || position == 4)
				return false;
			order1[position] = order1[position-1];
			order1[position-1] = 9; 			
			break;
		}
		case 3: {//上
			if (position > 3 && position < 9)
				return false;
			order1[position] = order1[position+4];
			order1[position+4] = 9; 
			break;
		}
		case 4: {
			if (position >= 0 && position < 4 || position == 8)
				return false;
			order1[position] = order1[position-4];
			order1[position-4] = 9; 
			break;
		}
		}

		System.out.print("operate" + op);
		int d = curState.d + 1;
		int h;
		if (hNum == 0)
			h = h1(order1);
		else
			h = h2(order1);
		int f = d + h;
		int operand = order1[position];
		State state;
		if ((state = isInClose(order1)) != null) {//在close表中
			System.out.println("state in close?!");
			if (f < state.f) {
				state.d = d;
				state.h = h;
				state.f = f;
				state.parent = curState;
				update(state);
			}
			Iterator<State> it = openTable.iterator();
			while(it.hasNext()) {
				State s = it.next();
				s.d = s.parent.d + 1;
				s.f = s.d + s.h;
			}	
		}
		else if ((state = isInOpen(order1)) != null) {//在open表中
			System.out.println("state in open?!");
			if (f < state.f) {
				state.d = d;
				state.h = h;
				state.f = f;
				state.parent = curState;
			}			
		}
		else {
			System.out.println("add new state to openTABLE");
			State s = new State(order1, d, h, operand, op, curState);
			openTable.add(s);
		}
		return true;
	}	
	
	
	private void update(State state) {
		if (state.childNum != 0) {
			for(int i = 0; i < closeTable.size(); i++) {
				State state2 = closeTable.get(i);
				int j;
				for(j = 0; j < 9; j++) {
					if (state == state2.parent) {
						state2.d = state.d + 1;
						state2.f = state2.d + state2.h;
						update(state2);
					}				
				}
			}
		}
	}
	
	public int[] next(JTextArea jtaOut) {
		if (solution.isEmpty())
			return null;
		State state = solution.pop();
		String op = null;
		switch(state.operate) {
			case 1: op = "Picture" + state.operand + " goes left!\n";
					break;
			case 2: op = "Picture" + state.operand + " goes right!\n";
					break;
			case 3: op = "Picture" + state.operand + " goes up!\n";
					break;
			case 4: op = "Picture" + state.operand + " goes down!\n";
					break;
		}
		jtaOut.append(op);
		return state.getOrder();
	}
	
	static class State implements Comparable {
		private int[] order;
		private int d;
		private int h;
		private int f;
		private int operand;//移动的图像
		private int operate;//1 left, 2 right, 3 up, 4 down
		private State parent;
		private int childNum;
		
		public State(int[] ord, int n1, int n2, int oprd, int op, State o) {
			order = ord;
			d = n1;
			h = n2;
			f = d + h;
			operand = oprd;
			operate = op;
			parent = o;
			if (o != null)
				o.childNum++;
			childNum = 0;
		}
		
		public int[] getOrder() {
			return order;
		}
		
		public boolean isTarget() {
			for(int i = 0; i < 9; i++)
				if (order[i] != i+1)
					return false;
			return true;
		}
		
		public int compareTo(Object o) {
			return this.f - ((State)o).f;
		}
		
	}
}
