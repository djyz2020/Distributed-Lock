package AdvancedProgram;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListSort {
	
	public static void main(String[] args) {
		ListSort();
	}
	
	public static void ListSort(){
		List<Integer> integerList = Arrays.asList(4, 5, 2, 3, 7, 9);
        List<Integer> collect = integerList.stream()
                .map(i -> i * i).distinct()
                .collect(Collectors.toList());
//        Collections.sort(collect);  //升序
        collect.sort(Comparator.reverseOrder()); //倒序
        collect.forEach(System.out::println);
	}

}
