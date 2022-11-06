package br.com.thecodeforce.recognize.demo.helper;

import java.util.function.Function;

public interface ThrowingFunction<T, R, E extends Throwable> {
	
	R exec(T t) throws E;
	
	static <T, R, E extends Throwable> Function<T, R> unchecked(ThrowingFunction<T, R, E> func) {
		return (t) -> {
			try {
				return func.exec(t);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
}