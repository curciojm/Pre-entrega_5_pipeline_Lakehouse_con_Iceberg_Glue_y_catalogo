
// necesitamos otra clase que reciba el resultado de la agregación y le incorpore los límites temporales de la ventana
// StockWindowFunction prepara y emite el resultado hacia la siguiente fase del pipeline
package com.dataeng.lakehouse;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class StockWindowFunction
// ProcessWindowFunction heredado de apache flink
// aca estan los otros archivos, los que no estan es porque estan integrados por dos
// ¿Por qué recibe y devuelve StockAggregate? 
// Porque recibe el resultado de nuestra agregación y lo devuelve enriquecido con las fechas de inicio y fin
// extends significa heredar de otra clase
// La sotckwindowfunction entonces empaqueta todo el procesamiento
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