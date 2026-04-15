package net.minecraft.util.collection;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class DefaultedList<E> extends AbstractList<E> {
	private final List<E> delegate;
	@Nullable
	private final E initialElement;

	public static <E> DefaultedList<E> of() {
		return new DefaultedList<>(Lists.<E>newArrayList(), null);
	}

	public static <E> DefaultedList<E> ofSize(int size) {
		return new DefaultedList<>(Lists.<E>newArrayListWithCapacity(size), null);
	}

	public static <E> DefaultedList<E> ofSize(int size, E defaultValue) {
		Objects.requireNonNull(defaultValue);
		Object[] objects = new Object[size];
		Arrays.fill(objects, defaultValue);
		return new DefaultedList<>(Arrays.asList(objects), defaultValue);
	}

	@SafeVarargs
	public static <E> DefaultedList<E> copyOf(E defaultValue, E... values) {
		return new DefaultedList<>(Arrays.asList(values), defaultValue);
	}

	protected DefaultedList(List<E> delegate, @Nullable E initialElement) {
		this.delegate = delegate;
		this.initialElement = initialElement;
	}

	public E get(int index) {
		return (E)this.delegate.get(index);
	}

	public E set(int index, E element) {
		Objects.requireNonNull(element);
		return (E)this.delegate.set(index, element);
	}

	public void add(int index, E element) {
		Objects.requireNonNull(element);
		this.delegate.add(index, element);
	}

	public E remove(int index) {
		return (E)this.delegate.remove(index);
	}

	public int size() {
		return this.delegate.size();
	}

	public void clear() {
		if (this.initialElement == null) {
			super.clear();
		} else {
			for (int i = 0; i < this.size(); i++) {
				this.set(i, this.initialElement);
			}
		}
	}
}
