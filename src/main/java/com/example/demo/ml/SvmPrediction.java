package com.example.demo.ml;

import com.example.demo.entity.enums.PredictedLabel;
import java.math.BigDecimal;

public class SvmPrediction {

    private final PredictedLabel label;
    private final BigDecimal probFake;
    private final BigDecimal probReal;

    public SvmPrediction(PredictedLabel label, BigDecimal probFake, BigDecimal probReal) {
        this.label = label;
        this.probFake = probFake;
        this.probReal = probReal;
    }

    public PredictedLabel getLabel() {
        return label;
    }

    public BigDecimal getProbFake() {
        return probFake;
    }

    public BigDecimal getProbReal() {
        return probReal;
    }
}
