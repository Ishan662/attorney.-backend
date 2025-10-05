package com.example.backend.service;

import java.util.List;

    //initializing gettters and setters
    //yyyyy
public class GoogleDistanceMatrixResponse {
    private String status;
    private List<Row> rows;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public static class Row {
        private List<Element> elements;

        public List<Element> getElements() {
            return elements;
        }

        public void setElements(List<Element> elements) {
            this.elements = elements;
        }
    }

    public static class Element {
        private Duration duration;
        private String status;

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Duration {
        private String text;
        private long value; // seconds

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }
}
