package AdvancedProgram;

import java.util.Stack;

public class StackDemo {
	
	public static void main(String[] args){
		Stack<String> st = new Stack<>();
		st.push("100");
		st.push("222");
		st.push("223");
		//showpop(st);
		System.out.println(st.empty());
		System.out.println(st.peek());
		Stack<String> newStack = (Stack<String>) st.clone();
		newStack.push("444");
		System.out.println(st);
		System.out.println(newStack);
		System.out.println(st.search("222"));
	}
	
	/**
	 * 显示栈中的元素
	 * @param st
	 */
	public static void showpop(Stack<String> st) {
        System.out.print("pop -> ");
        String a = st.pop();
        System.out.println(a);
        System.out.println("stack: " + st);
    }
	
	/**
	 * empty()  	测试栈是否为空
	 * peek()  		查看栈顶部对象
	 * pop()		移除堆栈顶部对象
	 * push()		将项压入栈顶部
	 * search() 	返回对象在堆栈中的位置
	 */
	
	public static boolean isStackEmpty(Stack<String> st){
		if(st.empty()){
			return true;
		}
		return false;
	}

}
