package AdvancedProgram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 集合排序
 * @author haibozhang
 * 2018.4.18
 */
public class CollectionsSort {

	public static void sortTest(List<Integer> list){
 		Collections.sort(list, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if(o1 > o2){
					return -1;
				}else if(o1 < o2){
					return 1;
				}
				return 0;
			}
		});
 		
 		for (Integer integer : list) {
			System.out.println(integer);
		}
	}
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(5);
		list.add(3);
		sortTest(list);
	}
	
}
