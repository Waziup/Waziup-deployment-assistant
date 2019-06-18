package eu.waziup.app.utils;

import com.annimon.stream.Stream;
import com.annimon.stream.function.UnaryOperator;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;

import eu.waziup.app.data.network.model.devices.Device;

public class FilterOwner implements UnaryOperator<Stream<List<Device>>> {
    @Override
    public Stream<List<Device>> apply(Stream<List<Device>> deviceStream) {
        final Iterator<? extends Device> iterator = deviceStream.getIterator();
        final ArrayDeque<List<Device>> deque = new ArrayDeque<>();
        while (iterator.hasNext()) {
            deque.addFirst(iterator.next());
        }
        return Stream.of(deque.iterator());
    }
}
