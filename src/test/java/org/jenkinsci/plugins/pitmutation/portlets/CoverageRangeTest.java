package org.jenkinsci.plugins.pitmutation.portlets;

import org.junit.jupiter.api.Test;


import java.awt.Color;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.ABYSMAL;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.AVERAGE;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.EXCELLENT;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.FAIR;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.GOOD;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.PERFECT;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.POOR;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.SUFFICIENT;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.TRAGIC;

class CoverageRangeTest {

    @Test
    void valueOfLessTHanZeroReturnsAbysmal() {
        assertThat(CoverageRange.valueOf(-1D), is(ABYSMAL));
    }

    @Test
    void valueOfZeroReturnsAbysmal() {
        assertThat(CoverageRange.valueOf(0D), is(ABYSMAL));
    }

    @Test
    void valueOfTwentyFiveReturnsTragic() {
        assertThat(CoverageRange.valueOf(25D), is(TRAGIC));
    }

    @Test
    void valueOfFiftyReturnsPoor() {
        assertThat(CoverageRange.valueOf(50D), is(AVERAGE));
    }

    @Test
    void valueOfSeventyFiveReturnsFair() {
        assertThat(CoverageRange.valueOf(75D), is(SUFFICIENT));
    }

    @Test
    void valueOfEightyFiveReturnsSufficient() {
        assertThat(CoverageRange.valueOf(85D), is(GOOD));
    }

    @Test
    void valueOfNinetyTwoReturnsGood() {
        assertThat(CoverageRange.valueOf(92D), is(EXCELLENT));
    }

    @Test
    void valueOfNinetySevenReturnsExcellent() {
        assertThat(CoverageRange.valueOf(97D), is(EXCELLENT));
    }

    @Test
    void valueOfOneHundredReturnsPerfect() {
        assertThat(CoverageRange.valueOf(100D), is(PERFECT));
    }

    @Test
    void valueOfOneHundredAndOneReturnsPerfect() {
        assertThat(CoverageRange.valueOf(101D), is(PERFECT));
    }

    @Test
    void getFillHexStringForAbysmal() {
        assertThat(ABYSMAL.getFillHexString(), is("FF0000"));
    }

    @Test
    void getFillHexStringForTragic() {
        assertThat(TRAGIC.getFillHexString(), is("FF4500"));
    }

    @Test
    void getFillHexStringForPoor() {
        assertThat(POOR.getFillHexString(), is("FF7F00"));
    }

    @Test
    void getFillHexStringForFair() {
        assertThat(FAIR.getFillHexString(), is("FFFF00"));
    }

    @Test
    void getFillHexStringForSufficient() {
        assertThat(SUFFICIENT.getFillHexString(), is("C8FF3F"));
    }

    @Test
    void getFillHexStringForGood() {
        assertThat(GOOD.getFillHexString(), is("7AFF3F"));
    }

    @Test
    void getFillHexStringForExcellent() {
        assertThat(EXCELLENT.getFillHexString(), is("00CD00"));
    }

    @Test
    void getFillHexStringForPerfect() {
        assertThat(PERFECT.getFillHexString(), is("008B00"));
    }

    @Test
    void getLineHexStringForAbysmal() {
        assertThat(ABYSMAL.getLineHexString(), is("EEEEEE"));
    }

    @Test
    void getLineHexStringForTragic() {
        assertThat(TRAGIC.getLineHexString(), is("EEEEEE"));
    }

    @Test
    void getLineHexStringForPoor() {
        assertThat(POOR.getLineHexString(), is("000000"));
    }

    @Test
    void getLineHexStringForFair() {
        assertThat(FAIR.getLineHexString(), is("000000"));
    }

    @Test
    void getLineHexStringForSufficient() {
        assertThat(SUFFICIENT.getLineHexString(), is("000000"));
    }

    @Test
    void getLineHexStringForGood() {
        assertThat(GOOD.getLineHexString(), is("000000"));
    }

    @Test
    void getLineHexStringForExcellent() {
        assertThat(EXCELLENT.getLineHexString(), is("000000"));
    }

    @Test
    void getLineHexStringForPerfect() {
        assertThat(PERFECT.getLineHexString(), is("EEEEEE"));
    }

    @Test
    void fillColorOfMinusOneReturnsAbysmalFloor() {
        assertThat(CoverageRange.fillColorOf(-1D), is(new Color(255, 0, 0)));
    }

    @Test
    void fillColorOfThirtyReturnsProportionateBlend() {
        assertThat(CoverageRange.fillColorOf(30D), is(new Color(255, 88, 0)));
    }

    @Test
    void fillColorOfThirtySevenFiveReturnsProportionateBlend() {
        assertThat(CoverageRange.fillColorOf(37.5), is(new Color(255, 117, 0)));
    }

    @Test
    void fillColorOfFortyFiveReturnsProportionateBlend() {
        assertThat(CoverageRange.fillColorOf(45D), is(new Color(255, 146, 0)));
    }

    @Test
    void fillColorOfFiftyFloorFillColor() {
        assertThat(CoverageRange.fillColorOf(50D), is(new Color(255, 165, 0)));
    }

    @Test
    void fillColorOfOneHundredAndOneReturnsPerfectFloor() {
        assertThat(CoverageRange.fillColorOf(101D), is(new Color(0, 139, 0)));
    }

    @Test
    void colorAsHexString() {
        Color color = new Color(0, 128, 255);
        assertThat(CoverageRange.colorAsHexString(color), is("0080FF"));
    }

}
