
package com.dataeng.lakehouse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

public class StockQuoteParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static StockQuote parse(String json) {

        try {
            // MAPPER Es una herramienta de Jackson que permite interpretar JSON
            JsonNode node = MAPPER.readTree(json);
// se le indica a mapper que campos acceder y como se definen
            String ticker = node.get("TICKER").asText();

            double price = node.get("PRICE").asDouble();

            long eventTime = Instant.parse(
                    node.get("EVENT_TIME").asText()
                    // Tu evento contiene una fecha en formato ISO 8601. Instant la interpreta y toEpochMilli() 
                    // la convierte a milisegundos Unix, que es el formato que utilizaremos en Flink
            ).toEpochMilli();
// construccion del objeto
            return new StockQuote(ticker, price, eventTime);

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error parsing stock quote: " + json, e
            );
        }
    }
}
