
package com.dataeng.lakehouse;

import java.io.Serializable;

/**
 * Represents an individual stock quote received from Kinesis.
 * public permite acceder a la clase del resto de la app, private lo contrario
 * getter y setter perimten consultar los atributos de una clase. getter devuelve el valor de price
 * setter establece un valor
 * el codigo hace que cada cotización que recibimos de Kinesis dentro de nuestra aplicación Java
 */ 
public class StockQuote implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ticker;
    private double price;
    private long eventTime; // long asigna 64 bits a difrencia de int. para datos id largos la vamos a necesitar son 9 trillones aprox

    // Required by Flink for POJO serialization
    public StockQuote() {
    }

    public StockQuote(String ticker, double price, long eventTime) {
        this.ticker = ticker;
        this.price = price;
        this.eventTime = eventTime;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "StockQuote{" +
                "ticker='" + ticker + '\'' +
                ", price=" + price +
                ", eventTime=" + eventTime +
                '}';
    }
}
