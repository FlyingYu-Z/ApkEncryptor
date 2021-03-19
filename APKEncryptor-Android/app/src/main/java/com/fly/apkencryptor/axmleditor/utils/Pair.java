package com.fly.apkencryptor.axmleditor.utils;

public class Pair<T1,T2> {
	public T1 first;
	public T2 second;

	public Pair(T1 t1, T2 t2){
		first = t1;
		second = t2;
	}
	
	public Pair(){}


	public static <A, B> Pair <A, B> create(A a, B b) {
		return new Pair<A,B>(a, b);
	}
}
