
package com.dataeng.lakehouse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

public class StockQuoteParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static StockQuote parse(String json) {

        try {
            JsonNode node = MAPPER.readTree(json);

            String ticker = node.get("TICKER").asText();

            double price = node.get("PRICE").asDouble();

            long eventTime = Instant.parse(
                    node.get("EVENT_TIME").asText()

            ).toEpochMilli();

            return new StockQuote(ticker, price, eventTime);

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error parsing stock quote: " + json, e
            );
        }
    }
}
