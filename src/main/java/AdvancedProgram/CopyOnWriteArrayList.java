package AdvancedProgram;

import java.util.Arrays;

/**
 * 写时复制
 * @author haibozhang
 * 2014.4.18
 */
public class CopyOnWriteArrayList {
	
	/**
	 * 写时复制
	 * @param arr
	 * @param el
	 * @return new_arr
	 */
	public static Object[] copyOnWriteArrayList(Object[] arr, Object el){
		Object[] new_arr = Arrays.copyOf(arr, arr.length + 1);
		new_arr[arr.length] = el;
		return new_arr;
	}
	
	public static void main(String[] args) {
		String[] arr = new String[2];
		arr[0] = "1";
		arr[1] = "5";
		Object[] new_arr = copyOnWriteArrayList(arr, "3");
		System.out.println(Arrays.asList(arr));
		System.out.println(Arrays.asList(new_arr));
	}

}
