package com.dataeng.lakehouse;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class StockWindowFunction
        extends ProcessWindowFunction<
            StockAggregate,
            StockAggregate,
            String,
            TimeWindow
        > {

    @Override
    // procesar una ventana
    public void process(
            String ticker,
            Context context,
            Iterable<StockAggregate> elements,
            Collector<StockAggregate> out
    ) {

        StockAggregate aggregate = elements.iterator().next();
// aca se incorporan los limites temporales
        aggregate.setWindowStart(context.window().getStart());
        aggregate.setWindowEnd(context.window().getEnd());
// emitir el resultado
        out.collect(aggregate);
    }
}