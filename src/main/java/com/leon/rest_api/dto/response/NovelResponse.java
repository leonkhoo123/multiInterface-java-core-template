package com.leon.rest_api.dto.response;

import java.math.BigDecimal;
import java.util.Objects;

public class NovelResponse {
    private String name;
    private BigDecimal totalLength;
    private BigDecimal readPercentage;
    private boolean isLastRead;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NovelResponse that = (NovelResponse) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public NovelResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(BigDecimal totalLength) {
        this.totalLength = totalLength;
    }

    public BigDecimal getReadPercentage() {
        return readPercentage;
    }

    public void setReadPercentage(BigDecimal readPercentage) {
        this.readPercentage = readPercentage;
    }

    public boolean isLastRead() {
        return isLastRead;
    }

    public void setLastRead(boolean lastRead) {
        isLastRead = lastRead;
    }
}
