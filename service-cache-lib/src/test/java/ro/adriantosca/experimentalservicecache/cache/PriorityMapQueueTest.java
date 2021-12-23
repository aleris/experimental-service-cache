package ro.adriantosca.experimentalservicecache.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ro.adriantosca.experimentalservicecache.cache.PriorityMapQueue;

class PriorityMapQueueTest {

    @Test
    void get() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        queue.add(new CachedItem<>("id2", "value2"));
        queue.add(new CachedItem<>("id3", "value3"));
        var node2 = queue.get("id2");
        assertThat(node2).isNotNull();
        assertThat(node2.getItem().value()).isEqualTo("value2");
    }

    @Test
    void get__returns_null_when_does_not_exists() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        assertThat(queue.get("id2")).isNull();
    }

    @Test
    void size() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        queue.add(new CachedItem<>("id2", "value2"));
        assertThat(queue.size()).isEqualTo(2);
    }

    @Test
    void add() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        assertThat(queue.size()).isEqualTo(1);
        var node1 = queue.get("id1");
        assertThat(node1).isNotNull();
        assertThat(node1.getItem().value()).isEqualTo("value1");
    }

    @Test
    void makeMostRecent() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        queue.add(new CachedItem<>("id2", "value2"));
        queue.add(new CachedItem<>("id3", "value3"));
        var node1 = queue.get("id1");
        assertThat(node1).isNotNull();
        queue.makeMostRecent(node1);
        queue.removeLeastRecent();
        assertThat(queue.size()).isEqualTo(2);
        assertThat(queue.get("id2")).isNull();
    }

    @Test
    void removeLeastRecent() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        queue.add(new CachedItem<>("id2", "value2"));
        queue.removeLeastRecent();
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.get("id1")).isNull();
        assertThat(queue.get("id2")).isNotNull();
    }

    @Test
    void remove__with_single_node() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        assertThat(queue.size()).isEqualTo(1);
        var node1 = queue.get("id1");
        assertThat(node1).isNotNull();
        queue.remove(node1);
        assertThat(queue.size()).isEqualTo(0);
    }

    @Test
    void remove__start() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        queue.add(new CachedItem<>("id2", "value2"));
        queue.add(new CachedItem<>("id3", "value3"));
        assertThat(queue.size()).isEqualTo(3);
        var node1 = queue.get("id1");
        assertThat(node1).isNotNull();
        queue.remove(node1);
        assertThat(queue.size()).isEqualTo(2);
        assertThat(queue.get("id1")).isNull();
        assertThat(queue.get("id2")).isNotNull();
        assertThat(queue.get("id3")).isNotNull();
        queue.removeLeastRecent();
        assertThat(queue.get("id2")).isNull();
        queue.removeLeastRecent();
        assertThat(queue.get("id3")).isNull();
    }

    @Test
    void remove__middle() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        queue.add(new CachedItem<>("id2", "value2"));
        queue.add(new CachedItem<>("id3", "value3"));
        assertThat(queue.size()).isEqualTo(3);
        var node1 = queue.get("id2");
        assertThat(node1).isNotNull();
        queue.remove(node1);
        assertThat(queue.size()).isEqualTo(2);
        assertThat(queue.get("id1")).isNotNull();
        assertThat(queue.get("id2")).isNull();
        assertThat(queue.get("id3")).isNotNull();
        queue.removeLeastRecent();
        assertThat(queue.get("id1")).isNull();
        queue.removeLeastRecent();
        assertThat(queue.get("id3")).isNull();
    }

    @Test
    void remove__end() {
        var queue = new PriorityMapQueue<String, CachedItem<String, String>>(3);
        queue.add(new CachedItem<>("id1", "value1"));
        queue.add(new CachedItem<>("id2", "value2"));
        queue.add(new CachedItem<>("id3", "value3"));
        assertThat(queue.size()).isEqualTo(3);
        var node1 = queue.get("id3");
        assertThat(node1).isNotNull();
        queue.remove(node1);
        assertThat(queue.size()).isEqualTo(2);
        assertThat(queue.get("id1")).isNotNull();
        assertThat(queue.get("id2")).isNotNull();
        assertThat(queue.get("id3")).isNull();
        queue.removeLeastRecent();
        assertThat(queue.get("id1")).isNull();
        queue.removeLeastRecent();
        assertThat(queue.get("id2")).isNull();
    }
}
