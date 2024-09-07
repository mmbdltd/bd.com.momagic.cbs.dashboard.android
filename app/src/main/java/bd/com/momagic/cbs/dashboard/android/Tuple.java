package bd.com.momagic.cbs.dashboard.android;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ObjectUtilities;

public final class Tuple {

    private final String json;
    private final Object[] elements;

    private Tuple() {
        this(ObjectUtilities.getEmptyObjectArray());
    }

    private Tuple(Object[] elements) {
        this.elements = elements;
        json = JsonSerializer.serialize(this.elements);
    }

    @SuppressWarnings(value = "unchecked")
    public <Type> Type get(int elementPosition) {
        final Object element = get(elementPosition, Object.class);

        return (Type) element;
    }

    @SuppressWarnings(value = "unchecked")
    public <Type> Type get(int elementPosition, Class<Type> classOfType) {
        final int elementIndex = elementPosition - 1;
        final Object element = elementIndex > -1 && elementIndex < elements.length
                ? elements[elementIndex] : null;

        // if the element is null or the types mismatch, we shall return null...
        if (element == null || !classOfType.isAssignableFrom(element.getClass())) { return null; }

        return (Type) element;
    }

    public int size() { return elements.length; }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        return json;
    }

    /**
     * Creates a tuple of items.
     * @implNote A tuple must contain at least two items.
     * @param firstElement The first element of the tuple.
     * @param secondElement The second element of the tuple.
     * @param restOfTheElements Rest of the elements of the tuple (optional).
     * @return A tuple containing all the elements provided.
     */
    public static Tuple of(Object firstElement,
                           Object secondElement,
                           Object... restOfTheElements) {
        final Object[] elements = new Object[restOfTheElements.length + 2];
        elements[0] = firstElement;
        elements[1] = secondElement;

        for (int i = 2, j = 0; i < elements.length || j < restOfTheElements.length; ++i, ++j) {
            elements[i] = restOfTheElements[j];
        }

        return new Tuple(elements);
    }

    /**
     * Creates a tuple that contains no elements.
     * @return An empty tuple.
     */
    public static Tuple empty() {
        return new Tuple();
    }
}
