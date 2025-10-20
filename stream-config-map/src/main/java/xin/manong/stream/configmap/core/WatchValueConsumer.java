package xin.manong.stream.configmap.core;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.manong.weapon.base.event.ChangeEvent;
import xin.manong.weapon.base.event.EventListener;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * ETCD配置监控
 *
 * @author frankcl
 * @date 2025-10-20 13:48:00
 */
public class WatchValueConsumer implements Consumer<WatchResponse> {

    private static final Logger logger = LoggerFactory.getLogger(WatchValueConsumer.class);

    @Getter
    private final String key;
    private final EventListener listener;

    public WatchValueConsumer(String key, EventListener listener) {
        this.key = key;
        this.listener = listener;
    }

    @Override
    public void accept(WatchResponse watchResponse) {
        List<WatchEvent> watchEvents = watchResponse.getEvents();
        for (WatchEvent watchEvent : watchEvents) {
            try {
                KeyValue keyValue = watchEvent.getKeyValue();
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                WatchEvent.EventType eventType = watchEvent.getEventType();
                switch (eventType) {
                    case PUT:
                        KeyValue prevKeyValue = watchEvent.getPrevKV();
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        logger.info("Value changed for etcd value:{} for key:{}", value, key);
                        listener.onChange(new ChangeEvent<>(prevKeyValue == null ? null :
                                prevKeyValue.getValue().toString(StandardCharsets.UTF_8), value));
                        break;
                    case DELETE:
                        logger.warn("Etcd key:{} is deleted", key);
                        break;
                    default:
                        logger.warn("Unknown event type:{} for etcd key:{}", eventType.name(), key);
                        break;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
