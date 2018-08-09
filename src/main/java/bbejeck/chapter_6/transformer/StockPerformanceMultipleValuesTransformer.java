package bbejeck.chapter_6.transformer;


import bbejeck.model.StockPerformance;
import bbejeck.model.StockTransaction;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Instant;
import java.util.List;

public class StockPerformanceMultipleValuesTransformer implements Transformer<String, StockTransaction, KeyValue<String, List<KeyValue<String, StockPerformance>>>> {

    private String stateStoreName ;
    private double differentialThreshold = 0.02;
    private ProcessorContext processorContext;
    private KeyValueStore<String, StockPerformance> keyValueStore;


    public StockPerformanceMultipleValuesTransformer(String stateStoreName, double differentialThreshold) {
        this.stateStoreName = stateStoreName;
        this.differentialThreshold = differentialThreshold;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void init(ProcessorContext processorContext) {
        // keep the processor context locally because we need it in punctuate() and commit()
        this.processorContext = processorContext;

        // retrieve the key-value store named stateStoreName
        keyValueStore = (KeyValueStore) this.processorContext.getStateStore(stateStoreName);

        // schedule a punctuate() method every 15000 milliseconds based on stream time
        this.processorContext.schedule(15000, PunctuationType.STREAM_TIME,
                (timestamp) -> {
                    KeyValueIterator<String, StockPerformance> performanceIterator = keyValueStore.all();
                    while (performanceIterator.hasNext()) {
                        KeyValue<String, StockPerformance> keyValue = performanceIterator.next();
                        StockPerformance stockPerformance = keyValue.value;

                        if (stockPerformance != null) {
                            if (stockPerformance.priceDifferential() >= differentialThreshold ||
                                    stockPerformance.volumeDifferential() >= differentialThreshold) {
                                processorContext.forward(keyValue.key, keyValue.value);
                            }
                        }
                    }
                    performanceIterator.close();

                    // commit the current processing progress
                    processorContext.commit();
                });
    }

    @Override
    public KeyValue<String, List<KeyValue<String, StockPerformance>>> transform(String symbol, StockTransaction transaction) {
        if (symbol != null) {
            StockPerformance stockPerformance = keyValueStore.get(symbol);

            if (stockPerformance == null) {
                stockPerformance = new StockPerformance();
            }

            stockPerformance.updatePriceStats(transaction.getSharePrice());
            stockPerformance.updateVolumeStats(transaction.getShares());
            stockPerformance.setLastUpdateSent(Instant.now());

            keyValueStore.put(symbol, stockPerformance);
        }
        return null;
    }

    @Override
    public void close() {
        //no-op
    }
}
