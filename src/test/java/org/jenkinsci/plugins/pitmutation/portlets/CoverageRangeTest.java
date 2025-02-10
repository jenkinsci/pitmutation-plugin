package org.jenkinsci.plugins.pitmutation.portlets;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.jenkinsci.plugins.pitmutation.portlets.CoverageRange.*;

public class CoverageRangeTest {

    @Test
    public void valueOfLessTHanZeroReturnsAbysmal() {
        assertThat(CoverageRange.valueOf(-1D), is(ABYSMAL));
    }

    @Test
    public void valueOfZeroReturnsAbysmal() {
        assertThat(CoverageRange.valueOf(0D), is(ABYSMAL));
    }

    @Test
    public void valueOfTwentyFiveReturnsTragic() {
        assertThat(CoverageRange.valueOf(25D), is(TRAGIC));
    }

    @Test
    public void valueOfFiftyReturnsPoor() {
        assertThat(CoverageRange.valueOf(50D), is(AVERAGE));
    }

    @Test
    public void valueOfSeventyFiveReturnsFair() {
        assertThat(CoverageRange.valueOf(75D), is(SUFFICIENT));
    }

    @Test
    public void valueOfEightyFiveReturnsSufficient() {
        assertThat(CoverageRange.valueOf(85D), is(GOOD));
    }

    @Test
    public void valueOfNinetyTwoReturnsGood() {
        assertThat(CoverageRange.valueOf(92D), is(EXCELLENT));
    }

    @Test
    public void valueOfNinetySevenReturnsExcellent() {
        assertThat(CoverageRange.valueOf(97D), is(EXCELLENT));
    }

    @Test
    public void valueOfOneHundredReturnsPerfect() {
        assertThat(CoverageRange.valueOf(100D), is(PERFECT));
    }

    @Test
    public void valueOfOneHundredAndOneReturnsPerfect() {
        assertThat(CoverageRange.valueOf(101D), is(PERFECT));
    }

    @Test
    public void getFillHexStringForAbysmal() {
        assertThat(ABYSMAL.getFillHexString(), is("FF0000"));
    }

    @Test
    public void getFillHexStringForTragic() {
        assertThat(TRAGIC.getFillHexString(), is("FF4500"));
    }

    @Test
    public void getFillHexStringForPoor() {
        assertThat(POOR.getFillHexString(), is("FF7F00"));
    }

    @Test
    public void getFillHexStringForFair() {
        assertThat(FAIR.getFillHexString(), is("FFFF00"));
    }

    @Test
    public void getFillHexStringForSufficient() {
        assertThat(SUFFICIENT.getFillHexString(), is("C8FF3F"));
    }

    @Test
    public void getFillHexStringForGood() {
        assertThat(GOOD.getFillHexString(), is("7AFF3F"));
    }

    @Test
    public void getFillHexStringForExcellent() {
        assertThat(EXCELLENT.getFillHexString(), is("00CD00"));
    }

    @Test
    public void getFillHexStringForPerfect() {
        assertThat(PERFECT.getFillHexString(), is("008B00"));
    }

    @Test
    public void getLineHexStringForAbysmal() {
        assertThat(ABYSMAL.getLineHexString(), is("EEEEEE"));
    }

    @Test
    public void getLineHexStringForTragic() {
        assertThat(TRAGIC.getLineHexString(), is("EEEEEE"));
    }

    @Test
    public void getLineHexStringForPoor() {
        assertThat(POOR.getLineHexString(), is("000000"));
    }

    @Test
    public void getLineHexStringForFair() {
        assertThat(FAIR.getLineHexString(), is("000000"));
    }

    @Test
    public void getLineHexStringForSufficient() {
        assertThat(SUFFICIENT.getLineHexString(), is("000000"));
    }

    @Test
    public void getLineHexStringForGood() {
        assertThat(GOOD.getLineHexString(), is("000000"));
    }

    @Test
    public void getLineHexStringForExcellent() {
        assertThat(EXCELLENT.getLineHexString(), is("000000"));
    }

    @Test
    public void getLineHexStringForPerfect() {
        assertThat(PERFECT.getLineHexString(), is("EEEEEE"));
    }

    @Test
    public void fillColorOfMinusOneReturnsAbysmalFloor() {
        assertThat(CoverageRange.fillColorOf(-1D), is(new Color(255, 0, 0)));
    }

    @Test
    public void fillColorOfThirtyReturnsProportionateBlend() {
        assertThat(CoverageRange.fillColorOf(30D), is(new Color(255, 88, 0)));
    }

    @Test
    public void fillColorOfThirtySevenFiveReturnsProportionateBlend() {
        assertThat(CoverageRange.fillColorOf(37.5), is(new Color(255, 117, 0)));
    }

    @Test
    public void fillColorOfFortyFiveReturnsProportionateBlend() {
        assertThat(CoverageRange.fillColorOf(45D), is(new Color(255, 146, 0)));
    }

    @Test
    public void fillColorOfFiftyFloorFillColor() {
        assertThat(CoverageRange.fillColorOf(50D), is(new Color(255, 165, 0)));
    }

    @Test
    public void fillColorOfOneHundredAndOneReturnsPerfectFloor() {
        assertThat(CoverageRange.fillColorOf(101D), is(new Color(0, 139, 0)));
    }

    @Test
    public void colorAsHexString() {
        Color color = new Color(0, 128, 255);
        assertThat(CoverageRange.colorAsHexString(color), is("0080FF"));
    }

}
